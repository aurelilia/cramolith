/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/6/21, 10:59 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import com.badlogic.gdx.graphics.Color

enum class Type(val color: Color) {
    Normal(Color.WHITE),
    Fighting(Color.BROWN),
    Fire(Color.RED),
    Grass(Color.GREEN),
    Water(Color.BLUE),
    Electric(Color.YELLOW)
}