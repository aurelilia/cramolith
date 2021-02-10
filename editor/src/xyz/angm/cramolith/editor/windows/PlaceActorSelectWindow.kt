/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:21 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.plusAssign
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.editor.EditorScreen
import xyz.angm.cramolith.editor.modes.PlaceActorMode

class PlaceActorSelectWindow(screen: EditorScreen) : Window("Place Actor") {

    init {
        mapChanged(screen)
    }

    override fun mapChanged(screen: EditorScreen) {
        clearChildren()
        for (actor in screen.map.map.actors) {
            textBtn(actor.key) {
                screen.map.mode = PlaceActorMode(actor.value)
            }
        }
        textBtn("New Actor") { stage += NewActorWindow(screen) }
        pack()
    }

    class NewActorWindow(screen: EditorScreen) : VisWindow("New Actor") {
        init {
            add(VisLabel("Name"))
            val name = VisTextField()
            add(name).row()
            add(VisLabel("Texture name"))
            val texture = VisTextField()
            add(texture).row()

            textBtn("Create", colspan = 2) {
                this@NewActorWindow.remove()
                val success = screen.map.map.newActor(name.text, texture.text)
                if (success) Dialogs.showOKDialog(screen.stage, "yes.", "Actor created.")
                else Dialogs.showErrorDialog(screen.stage, "Actor does not have a texture. Please create one and retry.")
                screen.mapOrLayoutChanged()
            }

            addCloseButton()
            pack()
            centerWindow()
        }
    }
}