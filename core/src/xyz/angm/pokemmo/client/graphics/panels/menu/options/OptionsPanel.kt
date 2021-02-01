/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.panels.menu.options

import ktx.actors.onChange
import ktx.actors.onClick
import ktx.actors.onKey
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visSelectBoxOf
import ktx.scene2d.vis.visTable
import ktx.scene2d.vis.visTextField
import xyz.angm.pokemmo.client.graphics.Skin
import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.panels.menu.MainMenuPanel
import xyz.angm.pokemmo.client.graphics.screens.MenuScreen
import xyz.angm.pokemmo.client.graphics.screens.Screen
import xyz.angm.pokemmo.client.resources.I18N
import xyz.angm.pokemmo.client.resources.configuration

/** Main options menu. */
class OptionsPanel(screen: Screen, parent: MainMenuPanel? = null) : Panel(screen) {

    init {
        reload(screen, parent)
    }

    private fun reload(screen: Screen, parent: MainMenuPanel?) {
        clearChildren()
        this += scene2d.visTable {
            // Only show certain options on menu screen
            if (screen is MenuScreen) {
                val box = visSelectBoxOf(I18N.languages())
                box.selected = configuration.language
                box.onChange {
                    I18N.setLanguage(box.selected)
                    parent!!.reload(screen)
                    reload(screen, parent)
                    this@OptionsPanel.isVisible = true // Regrab focus lost by reload
                }
                box.inCell.colspan(2)
                row()
            }

            visTextButton(I18N["options.controls"]) {
                it.height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).colspan(2).row()
                onClick { screen.pushPanel(ControlsPanel(screen)) }
            }

            visLabel(I18N["options.playername"]) {
                it.pad(10f)
            }

            visTextField(configuration.playerName) {
                it.width(400f).pad(20f).row()
                onKey { configuration.playerName = text }
            }

            visTextButton(I18N["back"]) {
                it.height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).colspan(2)
                onClick { screen.popPanel() }
            }

            setFillParent(true)
        }
    }

    override fun dispose() = configuration.save()
}