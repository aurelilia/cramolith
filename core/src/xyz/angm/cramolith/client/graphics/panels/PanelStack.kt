/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:26 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array

private const val TRANSITION_DURATION = 0.2f
private val TRANSITION = Interpolation.pow3

/** A stack of panels. Always displays the panel at the top of the stack,
 * while also using nice animations for transitioning between panels. */
class PanelStack : Actor() {

    private val panels = Array<Panel>(true, 5)
    val current get() = panels.last()!!

    /** Pops the top panel off the stack. Will automatically display the next panel. */
    fun popPanel(direction: Int = 1) {
        val panel = if (panels.isEmpty) return else panels.pop()
        panel?.addAction(
            Actions.sequence(
                Actions.moveTo(stage.width * direction, 0f, TRANSITION_DURATION, TRANSITION),
                Actions.visible(false),
                Actions.removeActor()
            )
        )

        if (!panels.isEmpty) transitionIn(panels.peek(), -1)
    }

    /** Pushes a panel on top of the stack. Hides the current top panel and displays the new one. */
    fun pushPanel(panel: Panel) {
        if (!panels.isEmpty) transitionOut(panels.peek())
        panels.add(panel)
        panel.setSize(stage.width, stage.height)
        stage.addActor(panel)
        transitionIn(panel)
    }

    private fun transitionIn(panel: Panel, direction: Int = 1) {
        panel.x = stage.width * direction
        panel.addAction(
            Actions.sequence(
                Actions.visible(true),
                Actions.moveTo(0f, 0f, TRANSITION_DURATION, TRANSITION),
                Actions.visible(true)
            )
        )
    }

    private fun transitionOut(panel: Panel) {
        panel.addAction(
            Actions.sequence(
                Actions.moveTo(-stage.width, 0f, TRANSITION_DURATION, TRANSITION),
                Actions.visible(false)
            )
        )
    }
}