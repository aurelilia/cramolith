/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:18 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels.menu.options

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.actors.onKeyDown
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.actions.PlayerAction
import xyz.angm.cramolith.client.graphics.Skin
import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.screens.MenuScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.client.resources.configuration

/** Options submenu for controls. */
class ControlsPanel(private var screen: MenuScreen) : Panel(screen) {

    private var current: Pair<Int, PlayerAction>? = null
    private var currentBtn: TextButton? = null
    private var table: Table

    init {
        this += scene2d.visTable {
            focusedActor = scrollPane {
                table = visTable {}

                onKeyDown { keycode ->
                    if (current == null && keycode == Input.Keys.ESCAPE) {
                        configuration.save()
                        screen.popPanel()
                    } else {
                        val current = current ?: return@onKeyDown
                        configuration.keybinds.unregisterKeybind(current.first)
                        configuration.keybinds.unregisterKeybind(keycode)
                        configuration.keybinds.registerKeybind(keycode, current.second.type)
                        this@ControlsPanel.current = null
                        updateBinds()
                    }
                }

                it.pad(50f, 0f, 50f, 0f).expand().row()
            }

            backButton(screen)
            setFillParent(true)
        }
        clearListeners()
        updateBinds()
    }

    private fun updateBinds() {
        table.clearChildren()

        configuration.keybinds.getAllSorted().forEach { action ->
            val label = Label("${I18N["keybind.${action.second.type}"]}:", skin)
            table.add(label).pad(20f)

            val text = if (action.first == 0) "---" else Input.Keys.toString(action.first)
            val button = TextButton(text, skin)
            table.add(button).height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(20f).row()

            button.onClick {
                currentBtn?.label?.setColor(1f, 1f, 1f, 1f)
                if (current == action) {
                    current = null
                    currentBtn = null
                } else {
                    current = action
                    currentBtn = button
                    button.label.setColor(0f, 1f, 0f, 1f)
                }
            }
        }
    }
}