/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 7:26 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.actions

import com.badlogic.gdx.InputAdapter
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.configuration

/** Used for handling input while in-game.
 * @param screen The game screen */
class PlayerInputHandler(private val screen: GameScreen) : InputAdapter() {

    var disabled = false
        set(value) {
            field = value
            if (!value) return
            configuration.keybinds.forEach {
                if (it.triggered) {
                    it.triggered = false
                    it.keyUp(screen)
                }
            }
        }

    /** Searches and executes the action bound to the key */
    override fun keyDown(keycode: Int): Boolean {
        if (disabled) return false
        val bind = configuration.keybinds[keycode] ?: return false
        if (!bind.triggered) {
            bind.triggered = true
            bind.keyDown(screen)
        }
        return true
    }

    /** Searches and executes the action bound to the key */
    override fun keyUp(keycode: Int): Boolean {
        if (disabled) return false
        val bind = configuration.keybinds[keycode] ?: return false
        if (bind.triggered) {
            bind.triggered = false
            bind.keyUp(screen)
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        screen.world.zoom(-amountY)
        return true
    }
}