/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 2:07 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.panels.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align

import xyz.angm.pokemmo.client.graphics.actors.Chat
import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.client.graphics.screens.worldHeight
import xyz.angm.pokemmo.client.graphics.screens.worldWidth
import xyz.angm.pokemmo.client.resources.I18N
import xyz.angm.pokemmo.common.ecs.position

/** The HUD during gameplay. Contains everything that is 2D. */
class GameplayOverlay(private val screen: GameScreen) : Panel(screen) {

    private val debugLabel = Label("", skin, "debug")
    private val onlinePlayers = Label("", skin)
    private val chat = Chat(skin, screen.client)

    init {
        addActor(debugLabel)
        addActor(onlinePlayers)
        addActor(chat)

        debugLabel.isVisible = false
        onlinePlayers.isVisible = false
        background = null

        resize()
    }

    /** Recalculate the positions of all elements. Called on viewport resize. */
    fun resize() {
        debugLabel.setPosition(5f, worldHeight - 200, Align.topLeft)
        onlinePlayers.setPosition(worldWidth - 400, worldHeight / 3)
        chat.setPosition(10f, 90f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (debugLabel.isVisible) debugLabel.setText(getDebugLabelString())
        if (onlinePlayers.isVisible) updateOnlinePlayers()
    }

    private fun updateOnlinePlayers() {
        val s = StringBuilder()
        s.append(I18N["players-online"])
        screen.onlinePlayers.forEach { s.append("\n$it") }
        onlinePlayers.setText(s)
    }

    /** Displays the chat, without it fading. */
    fun displayChat() = chat.update(fade = false)

    /** Toggle the debug menu/info. */
    fun toggleDebugInfo() {
        debugLabel.isVisible = !debugLabel.isVisible
    }

    /** Toggle the online players list. */
    fun toggleOnlinePlayers() {
        onlinePlayers.isVisible = !onlinePlayers.isVisible
    }

    private fun getDebugLabelString() =
        """
        FPS: ${Gdx.graphics.framesPerSecond}
        Time since last frame: ${(Gdx.graphics.deltaTime * 1000).format(1)}ms

        Heap Size: ${Runtime.getRuntime().totalMemory()}
        Heap Free: ${Runtime.getRuntime().freeMemory()}

        OpenGL ${Gdx.graphics.glVersion.majorVersion}: ${Gdx.graphics.glVersion.rendererString}
        Display: ${Gdx.graphics.displayMode}

        Player position: ${screen.player[position].toStringFloor()} / ${screen.player[position]}
        
        Entities loaded: ${screen.entitiesLoaded}
        ECS systems active: ${screen.systemsActive}
        """.trimIndent()

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}