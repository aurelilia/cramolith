/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 3:16 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.pokemon.Pokemon
import java.io.Serializable

sealed class Opponent : Serializable {
    var activePkmnIdx = 0
    abstract fun activePokemon(playerGetter: (Int) -> PlayerComponent): Pokemon
    abstract fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle): QueuedAction?
    abstract fun getPokemon(playerGetter: (Int) -> PlayerComponent): Iterator<Pokemon>
    open fun clearQueuedAction() {}

    fun hasRemainingPokemon(playerGetter: (Int) -> PlayerComponent): Boolean {
        for (pokemon in getPokemon(playerGetter)) {
            if (pokemon.battleState?.hp ?: -1 > 0) return true
        }
        return false
    }
}

class PlayerOpponent(val playerId: Int = 0) : Opponent() {

    var queuedAction: QueuedAction? = null

    override fun activePokemon(playerGetter: (Int) -> PlayerComponent) = playerGetter(playerId).pokemon[activePkmnIdx]
    override fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle) = queuedAction
    override fun getPokemon(playerGetter: (Int) -> PlayerComponent) = playerGetter(playerId).pokemon.iterator()
    override fun clearQueuedAction() {
        queuedAction = null
    }
}

class AiOpponent(val party: Array<Pokemon> = emptyArray()) : Opponent() {
    override fun activePokemon(playerGetter: (Int) -> PlayerComponent) = party[activePkmnIdx]
    override fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle) = QueuedMove(0)
    override fun getPokemon(playerGetter: (Int) -> PlayerComponent) = party.iterator()
}
