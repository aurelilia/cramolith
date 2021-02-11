/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:20 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import ktx.actors.plusAssign
import ktx.collections.*
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.graphics.screens.Screen
import xyz.angm.cramolith.common.runLogE
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.windows.*

class EditorScreen : Screen() {

    val map = Map(this, WorldMap.of("overworld"))
    private val windows = GdxArray<Window>()
    private var lastWindowHeight = 0f

    init {
        stage += map

        // Windows
        loadWindow(MenuWindow(this))
        loadWindow(SelectMapWindow(this))
        loadWindow(DrawTriggerSelectWindow(this))
        loadWindow(TeleportsWindow(this))
        loadWindow(PlaceActorSelectWindow(this))

        // Input
        val multiplex = InputMultiplexer()
        multiplex.addProcessor(InputHandler(this))
        multiplex.addProcessor(stage)
        Gdx.input.inputProcessor = multiplex
    }

    override fun render(delta: Float) {
        runLogE("Editor", "rendering") { renderInternal() }
    }

    private fun renderInternal() {
        Cramolith.execRunnables()
        stage.act()

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    internal fun mapOrLayoutChanged() {
        lastWindowHeight = 0f
        windows.forEach { window ->
            window.mapChanged(this)
            window.setPosition(0f, lastWindowHeight)
            lastWindowHeight = window.y + window.height + 10f
        }
    }

    private fun loadWindow(window: Window) {
        stage += window
        windows.add(window)
        window.setPosition(0f, lastWindowHeight)
        lastWindowHeight = window.y + window.height + 10f
    }
}