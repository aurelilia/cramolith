/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/6/21, 11:53 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.ecs.playerM

class OnlinePlayersWindow(private val screen: GameScreen) : Window("players-online") {

    private val onlinePlayers = Label("", skin)

    init {
        addCloseButton()
        add(scene2d.scrollPane { actor = onlinePlayers })
    }

    override fun act(delta: Float) {
        val s = StringBuilder()
        screen.onlinePlayers.forEach { s.append("\n${it[playerM].name}") }
        onlinePlayers.setText(s)
    }
}