/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 3:32 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import java.io.Serializable

class Battle(
    var side1: BattleSide = BattleSide(),
    var side2: BattleSide = BattleSide()
) : Serializable

class BattleSide(
    val playerId: Int = 0,
    val activePokemonIdx: Int = 0,
    val queuedAction: QueuedAction? = null
)

class PokeBattleState(var hp: Int, var status: StatusEffect?) : Serializable

enum class StatusEffect : Serializable {
    POISONED, PARALYSED, ASLEEP
}

sealed class QueuedAction : Serializable
class QueuedMove(val moveIdx: Int = 0) : QueuedAction()
class QueuedSwitch(val pokemonIdx: Int = 0) : QueuedAction()
