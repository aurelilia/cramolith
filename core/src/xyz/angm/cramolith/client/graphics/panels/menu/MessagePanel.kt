/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/6/21, 1:51 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels.menu

import com.badlogic.gdx.Input
import ktx.actors.onKeyDown
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.screens.Screen
import xyz.angm.cramolith.client.resources.I18N

/** A panel for displaying a message.
 * @param visLabelText The text to display.
 * @param callback Called when the user pressed the back button. */
class MessagePanel(screen: Screen, visLabelText: String, callback: () -> Unit) : Panel(screen) {

    init {
        focusedActor = scene2d.visTable {
            visLabel(I18N[visLabelText]) { it.pad(20f).row() }

            backButton(screen)

            onKeyDown { keycode ->
                when (keycode) {
                    Input.Keys.ESCAPE, Input.Keys.ENTER -> callback()
                }
            }

            setFillParent(true)
        }
        this += focusedActor
        clearListeners() // Remove escape listener in Panel
    }
}