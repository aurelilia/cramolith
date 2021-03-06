/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/6/21, 5:45 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import com.badlogic.gdx.math.MathUtils
import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.pokemon.Move
import xyz.angm.cramolith.common.pokemon.Pokemon
import java.io.Serializable

class Battle(
    private var leftOp: Opponent? = null,
    private var rightOp: Opponent? = null
) : Serializable {

    val left get() = leftOp!!
    val right get() = rightOp!!
    private var lastRoundFaint = false

    fun advance(turn: ArrayList<TurnEvent>, playerGetter: (Int) -> PlayerComponent) {
        var lhp = left.activePokemon(playerGetter)
        var rhp = right.activePokemon(playerGetter)
        val lturn = left.calcQueuedAction(playerGetter, this)!!
        val rturn = right.calcQueuedAction(playerGetter, this)!!
        left.clearQueuedAction()
        right.clearQueuedAction()

        fun checkFaint(side: Opponent, sideE: BattleSide, mon: Pokemon) = if (mon.battleState!!.hp <= 0) {
            turn.add(Fainted(sideE))
            mon.battleState!!.status = StatusEffect.Fainted
            lastRoundFaint = true
            if (!side.hasRemainingPokemon(playerGetter)) turn.add(BattleEnd(sideE.other()))
            true
        } else false

        fun execMove(move: QueuedAction, side: Opponent, sideE: BattleSide, mon: Pokemon, otherMon: Pokemon) {
            when (move) {
                is QueuedSwitch -> {
                    side.activePkmnIdx = move.pokemonIdx
                    val newPoke = side.activePokemon(playerGetter)
                    if (newPoke.battleState == null) newPoke.battleState = PokeBattleState(newPoke.hp)
                    if (sideE == BattleSide.Left) lhp = newPoke else rhp = newPoke
                    turn.add(Switch(sideE, newPoke.battleState!!.hp))
                }

                is QueuedMove -> {
                    val move = Move.of(mon.moveIds[move.moveIdx])
                    val damage = applyDamage(mon, otherMon, move)
                    turn.add(Attack(sideE, move.ident, damage))
                }
            }
        }

        fun execLeft() = execMove(lturn, left, BattleSide.Left, lhp, rhp)
        fun execRight() = execMove(rturn, right, BattleSide.Right, rhp, lhp)

        if (lastRoundFaint) {
            lastRoundFaint = false
            if (lhp.battleState!!.status == StatusEffect.Fainted) execLeft()
            else execRight()
        } else if (movePriority(lturn, lhp) > movePriority(rturn, rhp)) {
            execLeft()
            if (checkFaint(right, BattleSide.Right, rhp)) return
            execRight()
            checkFaint(left, BattleSide.Left, lhp)
        } else {
            execRight()
            if (checkFaint(left, BattleSide.Left, lhp)) return
            execLeft()
            checkFaint(right, BattleSide.Right, rhp)
        }
    }

    private fun movePriority(move: QueuedAction, mon: Pokemon) = when (move) {
        is QueuedSwitch -> Int.MAX_VALUE
        is QueuedMove -> Move.of(mon.moveIds[move.moveIdx]).priority * 1000 * mon.speed
    }

    fun side(side: BattleSide) = if (side == BattleSide.Left) left else right
}

fun applyDamage(mon: Pokemon, otherMon: Pokemon, move: Move): Int {
    // https://bulbapedia.bulbagarden.net/wiki/Damage
    val crit = if (MathUtils.random(15) == 0) 2f else 1f
    val random = MathUtils.random(0.85f, 1f)
    val stab = if (mon.species.type == move.type) 1.5f else 1f
    val type = move.type attacks otherMon.species.type

    val modifier = crit * random * stab * type
    val level = ((2f * mon.level) / 5f) + 2
    val dividend = level * move.damage * (mon.attack / otherMon.defense)

    val damage = ((dividend / 50f) + 2) * modifier
    otherMon.battleState!!.hp -= damage.toInt()
    return damage.toInt()
}

enum class BattleSide : Serializable {
    Left, Right;

    fun other() = if (this == Left) Right else Left
}

@kotlinx.serialization.Serializable
class PokeBattleState(var hp: Int = 0, var status: StatusEffect? = null) : Serializable

enum class StatusEffect : Serializable {
    Poisoned, Paralyzed, Asleep, Fainted
}
