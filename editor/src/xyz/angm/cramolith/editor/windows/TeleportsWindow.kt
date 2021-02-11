/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 11:21 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisLabel
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.common.world.Teleport
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.EditorScreen

class TeleportsWindow(screen: EditorScreen) : Window("Map Teleports") {

    init {
        mapChanged(screen)
    }

    override fun mapChanged(screen: EditorScreen) {
        clearChildren()
        screen.map.map.teleports.forEachIndexed { idx, teleport ->
            add(VisLabel("$idx: ${WorldMap.of(teleport.map).ident} @ ${teleport.target}")).left().row()
        }
        textBtn("Add Teleport Pair") {
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