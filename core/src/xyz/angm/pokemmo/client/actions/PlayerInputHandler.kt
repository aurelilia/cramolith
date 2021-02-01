/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter

import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.client.resources.configuration

/** Used for handling input while in-game.
 * @param screen The game screen */
class PlayerInputHandler(private val screen: GameScreen) : InputAdapter() {

    private var active = true

    /** Searches and executes the action bound to the key */
    override fun keyDown(keycode: Int): Boolean {
        configuration.keybinds[keycode]?.keyDown?.invoke(screen)
        return true
    }

    /** Searches and executes the action bound to the key */
    override fun keyUp(keycode: Int): Boolean {
        configuration.keybinds[keycode]?.keyUp?.invoke(screen)
        return true
    }

    /** Should be called before registering as input handler. */
    fun beforeRegister() {
        configuration.keybinds.forEach { if (Gdx.input.isKeyPressed(it) && !Gdx.input.isKeyJustPressed(it)) keyDown(it) }
        active = true
    }

    /** Should be called before unregistering as input handler. */
    fun beforeUnregister() {
        configuration.keybinds.forEach { if (Gdx.input.isKeyPressed(it)) keyUp(it) }
        active = false
    }
}