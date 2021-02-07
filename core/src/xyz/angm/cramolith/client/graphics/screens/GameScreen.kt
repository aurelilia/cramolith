/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 2:23 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.actions.PlayerInputHandler
import xyz.angm.cramolith.client.ecs.systems.RenderSystem
import xyz.angm.cramolith.client.graphics.panels.menu.MessagePanel
import xyz.angm.cramolith.client.graphics.windows.ChatWindow
import xyz.angm.cramolith.client.graphics.windows.MenuWindow
import xyz.angm.cramolith.client.networking.Client
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.components.IgnoreSyncFlag
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.ecs.systems.NetworkSystem
import xyz.angm.cramolith.common.ecs.systems.RemoveSystem
import xyz.angm.cramolith.common.ecs.systems.VelocitySystem
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.common.networking.InitPacket
import xyz.angm.cramolith.common.runLogE
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity
import xyz.angm.rox.EntityListener
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.EntitySystem

/** The game screen. Active during gameplay.
 *
 * This screen is mainly a bag of other objects that make up game state;
 * and should not have any other responsibility other than initializing them and
 * setting up their interactions that drive the game.
 * The only other responsibility of this class is putting together all graphics sources and drawing them.
 *
 * The screen is initialized by [Cramolith], which means that it's only created after a server connection
 * was established and the initial [InitPacket] was received, and the world around the player was meshed.
 *
 * @param client The client for communicating with the server
 *
 * @property engine The ECS engine used
 * @property player The player controlled by this game instance */
class GameScreen(
    private val game: Cramolith,
    val client: Client,
    val player: Entity,
    entities: Array<Entity>,
    messages: Array<String>
) : Screen() {

    private val coScope = CoroutineScope(Dispatchers.Default)

    // Entities
    val engine = Engine()
    private val inputHandler = PlayerInputHandler(this)
    private val players = allOf(playerM)

    // 2D Graphics
    val stage = Stage(ScreenViewport())
    private val activeWindows = HashMap<String, VisWindow>()

    val entitiesLoaded get() = engine.entities.size
    val systemsActive get() = engine.systems.size
    val onlinePlayers get() = engine[players]

    init {
        initSystems()
        engine.add(player)
        entities.forEach { engine.add(it) }

        initState(messages)
        initRender()
    }

    override fun render(delta: Float) {
        runLogE("Client", "rendering") { renderInternal(delta) }
    }

    private fun renderInternal(delta: Float) {
        client.lock()
        Cramolith.execRunnables()
        engine.update(delta)
        stage.act()
        client.unlock()

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    /** Toggles a window.
     * @param name The name of the window to uniquely identify its type
     * @param create A closure to create the window if it isn't active yet and will be added */
    fun toggleWindow(name: String, create: (GameScreen) -> VisWindow) {
        val window = activeWindows[name]
        when {
            window == null -> {
                val new = create(this)
                stage.addActor(new.fadeIn())
                new.centerWindow()
                activeWindows[name] = new
            }
            window.stage == stage -> window.fadeOut()
            else -> stage.addActor(window.fadeIn())
        }
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
    private fun initState(messages: Array<String>) {
        // Windows
        toggleWindow("menu") { MenuWindow(it) }
        activeWindows["chat"] = ChatWindow(this, messages)

        // Network
        client.disconnectListener = { Cramolith.postRunnable { returnToMenu(I18N["disconnected-from-server"]) } }
        client.send(ChatMessagePacket("[CYAN]${player[playerM].name}[LIGHT_GRAY] ${I18N["joined-game"]}"))

        // Input
        val multiplex = InputMultiplexer()
        multiplex.addProcessor(stage)
        multiplex.addProcessor(inputHandler)
        Gdx.input.inputProcessor = multiplex
    }

    // Adds local components to the player entity.
    private fun addLocalPlayerComponents() = player.apply {
        add(engine, IgnoreSyncFlag())
    }

    // Initialize all rendering components
    private fun initRender() {
        stage.addActor(panels)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    /** hide is called when the screen is no longer active, at which point this type of screen becomes dereferenced and needs to be disposed. */
    override fun hide() = dispose()

    override fun dispose() {
        client.close()
        coScope.cancel()
        panels.dispose()
    }
}