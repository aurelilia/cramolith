/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.graphics.panels.menu.LoadingPanel
import xyz.angm.cramolith.client.graphics.panels.menu.MainMenuPanel
import xyz.angm.cramolith.client.resources.ResourceManager

/** The menu screen. It manages the current menu panel stack and draws it on top of a nice background.
 * @param game The game instance. */
class MenuScreen(private val game: Cramolith) : Screen() {

    private val stage = Stage(ScreenViewport())

    override fun show() {
        stage.addActor(panels)
        ResourceManager.init()
        pushPanel(LoadingPanel(this))
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    /** Connect to server or display error, see method in [Cramolith] */
    fun connectToServer() = game.connectToServer()

    /** Called when [ResourceManager] has finished loading. Will remove the loading screen
     * and show the main menu. */
    fun doneLoading() {
        panels.popPanel(-1)
        panels.pushPanel(MainMenuPanel(this))
    }

    override fun resize(width: Int, height: Int) = stage.viewport.update(width, height, true)

    override fun dispose() {
        stage.dispose()
        panels.dispose()
    }
}
