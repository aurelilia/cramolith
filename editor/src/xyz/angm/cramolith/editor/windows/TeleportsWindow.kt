/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/13/21, 2:34 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.common.world.Teleport
import xyz.angm.cramolith.common.world.TriggerType
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.EditorScreen

class TeleportsWindow(screen: EditorScreen) : Window("Map Teleports") {

    init {
        mapChanged(screen)
    }

    override fun mapChanged(screen: EditorScreen) {
        clearChildren()
        screen.map.map.teleports.forEachIndexed { idx, teleport ->
            add(VisLabel("$idx: ${WorldMap.of(teleport.map).ident} @ ${teleport.target}")).left()
            val btn = VisTextButton("Remove")
            add(btn).right().row()
            btn.onClick {
                val other = WorldMap.of(teleport.map)
                other.teleports.removeAt(teleport.target)
                screen.map.map.teleports.removeAt(idx)

                for (trigger in other.triggers) {
                    if (trigger.type == TriggerType.Teleport && trigger.idx > teleport.target) trigger.idx--
                }
                for (trigger in screen.map.map.triggers) {
                    if (trigger.type == TriggerType.Teleport && trigger.idx > idx) trigger.idx--
                }

                screen.mapOrLayoutChanged()
            }
        }
        textBtn("Add Teleport Pair", colspan = 2) {
            Dialogs.showInputDialog(stage, "Enter other map identifier", null, object : InputDialogAdapter() {
                override fun finished(input: String) {
                    val map = WorldMap.maybeOf(input)
                    if (map == null) Dialogs.showErrorDialog(stage, "Unknown map.")
                    else {
                        val offs = if (screen.map.map === map) 1 else 0
                        screen.map.map.teleports.add(Teleport(map = map.index, map.teleports.size + offs))
                        map.teleports.add(Teleport(map = screen.map.map.index, screen.map.map.teleports.size - 1))
                    }
                    screen.mapOrLayoutChanged()
                }
            })
        }.padTop(10f)
        pack()
    }
}