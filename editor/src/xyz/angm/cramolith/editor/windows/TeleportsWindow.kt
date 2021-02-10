/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 3:40 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.EditorScreen

class TeleportsWindow(private val screen: EditorScreen) : VisWindow("Map Teleports") {

    private var map = screen.map.map

    init {
        reload()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (map !== screen.map.map) {
            map = screen.map.map
            reload()
        }
    }

    private fun reload() {
        clearChildren()
        map.teleports.forEachIndexed { idx, teleport ->
            add(VisLabel("$idx: ${WorldMap.of(teleport.map).ident} @ ${teleport.target}")).left().row()
        }
        textBtn("Add Teleport Pair") {
            Dialogs.showInputDialog(stage, "Enter other map identifier", null, object : InputDialogAdapter() {
                override fun finished(input: String) {
                    val map = WorldMap.maybeOf(input)
                    if (map == null) Dialogs.showErrorDialog(stage, "Unknown map.")
                    else {
                        screen.map.map.teleports.add(WorldMap.Teleport(map = map.index, map.teleports.size))
                        map.teleports.add(WorldMap.Teleport(map = screen.map.map.index, screen.map.map.teleports.size - 1))
                    }
                    reload()
                }
            })
        }.padTop(10f)
        pack()
    }
}