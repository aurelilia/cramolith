/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import xyz.angm.pokemmo.client.resources.soundPlayer

/** A function that will make the given table make a clicking noise when
 * clicked by the user. Used for buttons in the menu and similar. */
fun Table.click() {
    addCaptureListener {
        if ((it as? InputEvent)?.type == InputEvent.Type.touchDown)
            soundPlayer.playSound("random/menu_click")
        false
    }
}