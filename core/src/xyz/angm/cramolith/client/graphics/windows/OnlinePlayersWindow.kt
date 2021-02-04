/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM

class OnlinePlayersWindow(private val screen: GameScreen) : VisWindow(I18N["players-online"]) {

    private val onlinePlayers = Label("", skin)

    init {
        addCloseButton()
        add(onlinePlayers)
    }

    override fun act(delta: Float) {
        val s = StringBuilder()
        screen.onlinePlayers.forEach { s.append("\n${it[playerM].name}") }
        onlinePlayers.setText(s)
    }
}