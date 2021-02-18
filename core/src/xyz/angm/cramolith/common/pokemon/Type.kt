/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 4:34 PM.
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
    Electric(Color.YELLOW);

    infix fun attacks(other: Type) = when {
        superEffective[this]?.contains(other) ?: false -> SUPER_EFFECTIVE
        notEffective[this]?.contains(other) ?: false -> NOT_EFFECTIVE
        else -> 1f
    }

    companion object {
        val superEffective = mapOf(
            Fighting to arrayOf(Normal),
            Fire to arrayOf(Grass),
            Grass to arrayOf(Water),
            Water to arrayOf(Fire),
            Electric to arrayOf(Water)
        )
        val notEffective = mapOf(
            Fire to arrayOf(Fire, Water),
            Grass to arrayOf(Fire, Grass),
            Water to arrayOf(Grass, Water),
            Electric to arrayOf(Electric)
        )
    }
}

const val SUPER_EFFECTIVE = 2f
const val NOT_EFFECTIVE = 0.5f