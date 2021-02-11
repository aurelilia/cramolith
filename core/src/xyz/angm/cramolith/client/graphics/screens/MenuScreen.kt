/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:20 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.panels.PanelStack
import xyz.angm.cramolith.client.graphics.panels.menu.LoadingPanel
import xyz.angm.cramolith.client.graphics.panels.menu.MainMenuPanel
import xyz.angm.cramolith.client.resources.ResourceManager

/** The menu screen. It manages the current menu panel stack and draws it on top of a nice background.
 * @param game The game instance. */
class MenuScreen(private val game: Cramolith) : Screen() {

    val panels = PanelStack()

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
    fun connectToServer(user: String, password: String) = game.connectToServer(user, password)

    /** Called when [ResourceManager] has finished loading. Will remove the loading screen
     * and show the main menu. */
    fun doneLoading() {
        panels.popPanel(-1)
        panels.pushPanel(MainMenuPanel(this))
    }

    /** Push a new panel on top of the PanelStack active. */
    fun pushPanel(panel: Panel) = panels.pushPanel(panel)

    /** Pops the current panel of the PanelStack and returns it. */
    fun popPanel() = panels.popPanel()

}
