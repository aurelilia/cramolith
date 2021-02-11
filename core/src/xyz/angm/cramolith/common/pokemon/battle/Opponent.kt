/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 10:36 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.pokemon.Pokemon
import java.io.Serializable

sealed class Opponent : Serializable {
    abstract fun activePokemon(screen: GameScreen): Pokemon
    abstract fun calcQueuedAction(battle: Battle): QueuedAction?
}

class PlayerOpponent(val playerId: Int = 0, val activePokemonIdx: Int = 0) : Opponent() {

    var queuedAction: QueuedAction? = null

    override fun activePokemon(screen: GameScreen) = screen.netSystem.entityOf(playerId)!![playerM].pokemon[activePokemonIdx]
    override fun calcQueuedAction(battle: Battle) = queuedAction
}

class NpcTrainerOpponent(val party: Array<Pokemon> = emptyArray(), val activeIdx: Int = 0) : Opponent() {
    override fun activePokemon(screen: GameScreen) = party[activeIdx]
    override fun calcQueuedAction(battle: Battle) = QueuedMove(0)
}

class WildPokemonOpponent(val pokemon: Pokemon = Pokemon()) : Opponent() {
    override fun activePokemon(screen: GameScreen) = pokemon
    override fun calcQueuedAction(battle: Battle) = QueuedMove(0)
}
