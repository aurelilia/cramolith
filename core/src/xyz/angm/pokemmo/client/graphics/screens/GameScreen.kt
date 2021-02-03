/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 6:04 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.PerformanceCounter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import xyz.angm.pokemmo.client.PokeMMO
import xyz.angm.pokemmo.client.actions.PlayerInputHandler
import xyz.angm.pokemmo.client.ecs.systems.RenderSystem
import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.panels.PanelStack
import xyz.angm.pokemmo.client.graphics.panels.game.GameplayOverlay
import xyz.angm.pokemmo.client.graphics.panels.menu.MessagePanel
import xyz.angm.pokemmo.client.networking.Client
import xyz.angm.pokemmo.client.resources.I18N
import xyz.angm.pokemmo.common.ecs.components.IgnoreSyncFlag
import xyz.angm.pokemmo.common.ecs.components.specific.PlayerComponent
import xyz.angm.pokemmo.common.ecs.playerM
import xyz.angm.pokemmo.common.ecs.systems.NetworkSystem
import xyz.angm.pokemmo.common.ecs.systems.RemoveSystem
import xyz.angm.pokemmo.common.ecs.systems.VelocitySystem
import xyz.angm.pokemmo.common.networking.ChatMessagePacket
import xyz.angm.pokemmo.common.networking.InitPacket
import xyz.angm.pokemmo.common.runLogE
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity
import xyz.angm.rox.EntityListener
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.EntitySystem

/** The game screen. Active during gameplay. Uses 2 panels; 1 for hotbar and a stack for the other panels required by the Screen interface.
 *
 * This screen is mainly a bag of other objects that make up game state;
 * and should not have any other responsibility other than initializing them and
 * setting up their interactions that drive the game.
 * The only other responsibility of this class is putting together all graphics sources and drawing them.
 *
 * The screen is initialized by [PokeMMO], which means that it's only created after a server connection
 * was established and the initial [InitPacket] was received, and the world around the player was meshed.
 *
 * @param client The client for communicating with the server
 *
 * @property engine The ECS engine used
 * @property player The player controlled by this game instance */
class GameScreen(
    private val game: PokeMMO,
    val client: Client,
    val player: Entity,
    entities: Array<Entity>
) : ScreenAdapter(), Screen {

    private val coScope = CoroutineScope(Dispatchers.Default)
    val bench = PerformanceCounter("render")

    // Entities
    val engine = Engine()
    private val inputHandler = PlayerInputHandler(this)
    private val players = allOf(PlayerComponent::class)

    // 2D Graphics
    val stage = Stage(viewport)
    private val uiPanels = PanelStack()
    val gameplayPanel = GameplayOverlay(this)

    val entitiesLoaded get() = engine.entities.size
    val systemsActive get() = engine.systems.size
    val onlinePlayers get() = engine[players].map { it[playerM].name }

    init {
        initSystems()
        engine.add(player)
        entities.forEach { engine.add(it) }

        initState()
        initRender()
    }

    override fun render(delta: Float) {
        runLogE("Client", "rendering") { renderInternal(delta) }
    }

    private fun renderInternal(delta: Float) {
        // Uncomment this and the stop call at the end to enable performance profiling.
        // startBench(delta)

        client.lock()
        PokeMMO.execRunnables()
        engine.update(delta)
        stage.act()
        client.unlock()

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()

        // bench.stop()
    }

    @Suppress("unused")
    private fun startBench(delta: Float) {
        bench.tick(delta)
        bench.start()
    }

    override fun pushPanel(panel: Panel) {
        if (uiPanels.panelsInStack == 0) { // First UI panel, switch gameplay > ui for input
            inputHandler.beforeUnregister()
            Gdx.input.inputProcessor = stage
            Gdx.input.isCursorCatched = false
            Gdx.input.setCursorPosition(stage.viewport.screenWidth / 2, (stage.viewport.screenY + stage.viewport.screenHeight) / 2)
        }
        uiPanels.pushPanel(panel)
    }

    override fun popPanel() {
        if (uiPanels.panelsInStack == 1) { // About to pop last panel, return to gameplay
            inputHandler.beforeRegister()
            Gdx.input.inputProcessor = inputHandler
            Gdx.input.isCursorCatched = true
        }
        uiPanels.popPanel()
    }

    /** Called when the game can no longer continue (disconnect; player quit; etc.)
     * Returns to the menu screen.
     * @param message The message to display. Defaults to no message which will return to menu screen immediately. */
    fun returnToMenu(message: String? = null) {
        client.send(player) // Make sure the player is up-to-date on the server
        client.disconnectListener = {} // Prevent it from showing the 'disconnected' message when it shouldn't
        game.screen = MenuScreen(game)
        dispose()
        (game.screen as Screen).pushPanel(MessagePanel(game.screen as Screen, message ?: return) {
            (game.screen as Screen).popPanel()
        })
    }

    // Initialize all ECS systems
    private fun initSystems() = engine.apply {
        addLocalPlayerComponents()
        val netSystem = NetworkSystem(client::send)
        client.addListener { if (it is Entity) netSystem.receive(it) }
        add(netSystem as EntitySystem)
        add(netSystem as EntityListener)

        val renderSystem = RenderSystem(this@GameScreen)
        add(renderSystem as EntitySystem)
        add(renderSystem as EntityListener)

        add(VelocitySystem())
        add(RemoveSystem())
    }

    // Initialize everything not render-related
    private fun initState() {
        // Network
        client.disconnectListener = { PokeMMO.postRunnable { returnToMenu(I18N["disconnected-from-server"]) } }
        client.send(ChatMessagePacket("[CYAN]${player[playerM].name}[LIGHT_GRAY] ${I18N["joined-game"]}"))

        // Input
        Gdx.input.inputProcessor = inputHandler
        Gdx.input.isCursorCatched = true
    }

    // Adds local components to the player entity.
    private fun addLocalPlayerComponents() = player.apply {
        add(engine, IgnoreSyncFlag())
    }

    // Initialize all rendering components
    private fun initRender() {
        stage.addActor(gameplayPanel)
        stage.addActor(uiPanels)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        gameplayPanel.resize()
    }

    /** hide is called when the screen is no longer active, at which point this type of screen becomes dereferenced and needs to be disposed. */
    override fun hide() = dispose()

    override fun dispose() {
        client.close()
        coScope.cancel()
        gameplayPanel.dispose()
        uiPanels.dispose()
    }
}