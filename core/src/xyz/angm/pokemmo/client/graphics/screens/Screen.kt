/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 4:38 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.screens

import com.badlogic.gdx.ScreenAdapter
import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.panels.PanelStack

/** A basic interface for a Screen. */
abstract class Screen : ScreenAdapter() {

    val panels = PanelStack()

    /** Push a new panel on top of the PanelStack active. */
    fun pushPanel(panel: Panel) = panels.pushPanel(panel)

    /** Pops the current panel of the PanelStack and returns it. */
    fun popPanel() = panels.popPanel()
}
