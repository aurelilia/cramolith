/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.panels.menu

import com.badlogic.gdx.Gdx
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTable
import xyz.angm.pokemmo.client.graphics.Skin
import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.panels.menu.options.OptionsPanel
import xyz.angm.pokemmo.client.graphics.screens.MenuScreen
import xyz.angm.pokemmo.client.resources.I18N

/** Main menu panel. */
class MainMenuPanel(screen: MenuScreen) : Panel(screen) {

    init {
        reload(screen)
    }

    internal fun reload(screen: MenuScreen) {
        clearChildren()
        this += scene2d.visTable {
            pad(0f, 0f, 100f, 0f)

            visTextButton(I18N["main.start"]) {
                it.height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).row()
                onClick { screen.connectToServer() }
            }
            visTextButton(I18N["main.options"]) {
                it.height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).row()
                onClick { screen.pushPanel(OptionsPanel(screen, this@MainMenuPanel)) }
            }
            visTextButton(I18N["main.exit"]) {
                it.height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).row()
                onClick { Gdx.app.exit() }
            }

            setFillParent(true)
        }
    }
}