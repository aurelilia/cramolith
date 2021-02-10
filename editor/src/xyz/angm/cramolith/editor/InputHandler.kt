/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 3:21 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2

class InputHandler(private val screen: EditorScreen) : InputAdapter() {

    private val prev = Vector2()

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        prev.set(screenX.toFloat(), screenY.toFloat())
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val x = screenX - prev.x
        val y = screenY - prev.y
        prev.set(screenX.toFloat(), screenY.toFloat())
        screen.map.scroll(x, -y)
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        screen.map.scrollSnap()
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) screen.map.mode?.cancel(screen.map)
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        screen.map.zoom(-amountY)
        return true
    }
}