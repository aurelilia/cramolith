/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 6:03 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels.game

import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.panels.menu.options.OptionsPanel
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.client.graphics.screens.GameScreen

/** In-game pause screen. */
class PausePanel(screen: GameScreen) : Panel(screen) {

    init {
        textBtn("pause.continue") { screen.popPanel() }
        textBtn("pause.options") { screen.pushPanel(OptionsPanel(screen)) }
        textBtn("pause.exit") { screen.returnToMenu() }
    }
}