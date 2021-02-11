/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:20 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs.components.specific

import com.badlogic.gdx.math.MathUtils
import xyz.angm.cramolith.common.ecs.components.NetworkSyncComponent
import xyz.angm.cramolith.common.ecs.components.PositionComponent
import xyz.angm.cramolith.common.ecs.components.VelocityComponent
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.rox.Component
import xyz.angm.rox.Engine

class WildPokemonComponent : Component {
    lateinit var wildPokemon: Pokemon
}

/** Create a new wild Pokemon entity. */
fun createWildPokemonEntity(engine: Engine, pokemon: Pokemon) =
    engine.entity {
        with<WildPokemonComponent> {
            wildPokemon = pokemon

        }
        with<PositionComponent> { set(MathUtils.random(0, 1000).toFloat(), MathUtils.random(0, 1000).toFloat()) }
        with<VelocityComponent> {set(1f,1f)}
        with<NetworkSyncComponent>()
    }

