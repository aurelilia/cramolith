/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/6/21, 7:09 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.ecs

import xyz.angm.cramolith.common.ecs.battleM
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.networking.BattleUpdatePacket
import xyz.angm.cramolith.common.pokemon.battle.Battle
import xyz.angm.cramolith.common.pokemon.battle.Opponent
import xyz.angm.cramolith.common.pokemon.battle.PlayerOpponent
import xyz.angm.cramolith.common.pokemon.battle.TurnEvent
import xyz.angm.cramolith.server.Server
import xyz.angm.rox.Entity
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.IteratingSystem

class BattleUpdateSystem(private val server: Server) : IteratingSystem(allOf(playerM, battleM)) {

    private val getter = { it: Int -> server.players[it].entity[playerM] }
    private val turn = ArrayList<TurnEvent>()

    override fun process(entity: Entity, delta: Float) {
        val battle = entity[battleM].battle
        val turn1 = battle.left.calcQueuedAction(getter, battle.right)
        val turn2 = battle.right.calcQueuedAction(getter, battle.left)

        if (turn1 != null && turn2 != null) {
            turn.clear()
            battle.advance(turn, getter)
            sendUpdate(battle, battle.left)
            sendUpdate(battle, battle.right)
        }
    }

    private fun sendUpdate(battle: Battle, side: Opponent) {
        if (side is PlayerOpponent) {
            val player = server.players[side.playerId]
            server.send(player.conn, BattleUpdatePacket(battle, turn, player.entity[playerM].pokemon))
        }
    }
}