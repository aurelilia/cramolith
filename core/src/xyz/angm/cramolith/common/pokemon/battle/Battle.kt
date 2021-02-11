/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 9:36 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import java.io.Serializable

class Battle(
    private var leftOp: Opponent? = null,
    private var rightOp: Opponent? = null
) : Serializable {
    val left get() = leftOp!!
    val right get() = rightOp!!
}

class PokeBattleState(var hp: Int, var status: StatusEffect?) : Serializable

enum class StatusEffect : Serializable {
    POISONED, PARALYSED, ASLEEP
}
