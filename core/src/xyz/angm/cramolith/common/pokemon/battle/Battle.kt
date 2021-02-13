/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/13/21, 3:19 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import java.io.Serializable

class Battle(
    private var leftOp: Opponent? = null,
    private var rightOp: Opponent? = null
) : Serializable {

    val left get() = leftOp!!
    val right get() = rightOp!!
    var isOver = false

    fun advance(playerGetter: (Int) -> PlayerComponent) {
        val lhp = left.activePokemon(playerGetter)
        val rhp = right.activePokemon(playerGetter)
        lhp.battleState!!.hp -= 10
        rhp.battleState!!.hp -= 10

        left.clearQueuedAction()
        right.clearQueuedAction()

        if (lhp.battleState!!.hp < 0 || rhp.battleState!!.hp < 0) isOver = true
    }
}

@kotlinx.serialization.Serializable
class PokeBattleState(var hp: Int = 0, var status: StatusEffect? = null) : Serializable

enum class StatusEffect : Serializable {
    POISONED, PARALYSED, ASLEEP
}
