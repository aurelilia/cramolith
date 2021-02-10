/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 4:14 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick
import xyz.angm.cramolith.common.world.TriggerType
import xyz.angm.cramolith.editor.EditorScreen
import xyz.angm.cramolith.editor.FirstTriggerMode

class DrawTriggerSelectWindow(screen: EditorScreen) : Window("Draw Trigger") {

    init {
        for (trigger in TriggerType.values()) {
            val btn = VisTextButton(trigger.name)
            btn.onClick { screen.map.mode = FirstTriggerMode(trigger) }
            add(btn).left().pad(5f)
        }
        pack()
    }
}