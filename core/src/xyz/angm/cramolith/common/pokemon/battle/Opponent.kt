/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/13/21, 3:06 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.pokemon.Pokemon
import java.io.Serializable

sealed class Opponent : Serializable {
    abstract fun activePokemon(playerGetter: (Int) -> PlayerComponent): Pokemon
    abstract fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle): QueuedAction?
    open fun clearQueuedAction() {}
}

class PlayerOpponent(val playerId: Int = 0, val activePokemonIdx: Int = 0) : Opponent() {

    var queuedAction: QueuedAction? = null

    override fun activePokemon(playerGetter: (Int) -> PlayerComponent) = playerGetter(playerId).pokemon[activePokemonIdx]
    override fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle) = queuedAction

    override fun clearQueuedAction() {
        queuedAction = null
    }
}

class NpcTrainerOpponent(val party: Array<Pokemon> = emptyArray(), val activeIdx: Int = 0) : Opponent() {
    override fun activePokemon(playerGetter: (Int) -> PlayerComponent) = party[activeIdx]
    override fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle) = QueuedMove(0)
}

class WildPokemonOpponent(val pokemon: Pokemon = Pokemon()) : Opponent() {
    override fun activePokemon(playerGetter: (Int) -> PlayerComponent) = pokemon
    override fun calcQueuedAction(playerGetter: (Int) -> PlayerComponent, battle: Battle) = QueuedMove(0)
}
