/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 1:01 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTooltip
import xyz.angm.cramolith.client.actions.PlayerActions
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N

class MenuWindow(screen: GameScreen) : Window("menu") {

    init {
        fun addButton(name: String, action: String) {
            val btn = VisTextButton(action.substring(0, 1).toUpperCase())
            btn.onClick { PlayerActions[action]!!.keyDown(screen) }
            btn.visTooltip(scene2d.visLabel(I18N["window.$name"]))
            add(btn).left().pad(5f)
        }

        addButton("chat", "chat")
        addButton("players-online", "onlinePlayers")
        addButton("debug", "debug")
        addButton("party", "party")
        addButton("pause", "pauseMenu")
        pack()
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        setPosition(stage?.width ?: return, 0f, Align.bottomRight)
    }
}