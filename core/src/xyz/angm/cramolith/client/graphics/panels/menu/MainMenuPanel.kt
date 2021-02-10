/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/9/21, 6:28 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels.menu

import com.badlogic.gdx.Gdx
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.panels.menu.options.OptionsPanel
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.client.graphics.screens.MenuScreen

/** Main menu panel. */
class MainMenuPanel(screen: MenuScreen) : Panel(screen) {

    init {
        reload(screen)
    }

    internal fun reload(screen: MenuScreen) {
        clearChildren()
        this += scene2d.visTable {
            pad(0f, 0f, 100f, 0f)

            textBtn("main.start") { screen.pushPanel(LoginPanel(screen)) }
            textBtn("main.options") { screen.pushPanel(OptionsPanel(screen, this@MainMenuPanel)) }
            textBtn("main.exit") { Gdx.app.exit() }

            setFillParent(true)
        }
    }
}