/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:06 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.kotcrab.vis.ui.widget.VisTextButton
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.EditorScreen

class SelectMapWindow(screen: EditorScreen) : Window("Select Map") {

    private val group = ButtonGroup<VisTextButton>()

    init {
        mapChanged(screen)
    }

    override fun mapChanged(screen: EditorScreen) {
        clearChildren()
        group.clear()
        for (map in WorldMap.all()) {
            group.add(textBtn("${map.index}: ${map.ident}") { screen.map.map = map })
        }
        pack()
    }
}