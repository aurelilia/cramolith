/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 3:38 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.plusAssign
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.graphics.screens.Screen
import xyz.angm.cramolith.common.runLogE
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.windows.DrawTriggerSelectWindow
import xyz.angm.cramolith.editor.windows.MenuWindow
import xyz.angm.cramolith.editor.windows.SelectMapWindow
import xyz.angm.cramolith.editor.windows.TeleportsWindow

class EditorScreen(val game: Cramolith) : Screen() {

    val stage = Stage(ScreenViewport())
    val map = Map(WorldMap.of("overworld"))
    private var lastWindowHeight = 0f

    init {
        stage += map

        // Windows
        loadWindow(MenuWindow(this))
        loadWindow(SelectMapWindow(this))
        loadWindow(DrawTriggerSelectWindow(this))
        loadWindow(TeleportsWindow(this))

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

    private fun loadWindow(window: VisWindow) {
        stage += window
        window.setPosition(0f, lastWindowHeight, Align.bottomLeft)
        lastWindowHeight = window.y + window.height
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    /** hide is called when the screen is no longer active, at which point this type of screen becomes dereferenced and needs to be disposed. */
    override fun hide() = dispose()

    override fun dispose() {
        panels.dispose()
        stage.dispose()
    }
}