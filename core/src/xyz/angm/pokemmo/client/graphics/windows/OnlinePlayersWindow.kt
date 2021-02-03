/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 4:01 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.windows

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.client.resources.I18N
import xyz.angm.pokemmo.common.ecs.playerM

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