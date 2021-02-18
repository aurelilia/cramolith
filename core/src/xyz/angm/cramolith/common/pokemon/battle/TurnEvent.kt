/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 4:13 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import java.io.Serializable

sealed class TurnEvent(val side: BattleSide = BattleSide.Left) : Serializable

class Attack(
    attacker: BattleSide = BattleSide.Left,
    val attackName: String = "",
    val damage: Int = 0
) : TurnEvent(attacker)

class Switch(side: BattleSide = BattleSide.Left, val hpAtSwitch: Int = 0) : TurnEvent(side)

class Fainted(side: BattleSide = BattleSide.Left) : TurnEvent(side)

class BattleEnd(winner: BattleSide = BattleSide.Left) : TurnEvent(winner)
