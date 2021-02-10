/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 3:19 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.badlogic.gdx.Gdx
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.common.yaml
import xyz.angm.cramolith.editor.EditorScreen


class MenuWindow(screen: EditorScreen) : VisWindow("Menu") {

    init {
        textBtn("Create Map") {
            Dialogs.showInputDialog(stage, "Enter map identifier", null, object : InputDialogAdapter() {
                override fun finished(input: String) {
                    if (WorldMap.new(input)) Dialogs.showOKDialog(stage, "yes.", "Map created.")
                    else Dialogs.showErrorDialog(stage, "Map does not have a texture. Please create one and restart Editor or recreate the map.")
                }
            })
        }

        textBtn("Save") {
            Gdx.files.local("map/${screen.map.map.ident}.yaml").writeString(yaml.encodeToString(WorldMap.serializer(), screen.map.map), false)
        }
        textBtn("Exit") { Gdx.app.exit() }
        pack()
    }
}