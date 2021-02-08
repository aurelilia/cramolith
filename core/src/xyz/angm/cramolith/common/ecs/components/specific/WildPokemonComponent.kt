package xyz.angm.cramolith.common.ecs.components.specific

import com.badlogic.gdx.math.MathUtils
import xyz.angm.cramolith.common.ecs.components.NetworkSyncComponent
import xyz.angm.cramolith.common.ecs.components.PositionComponent
import xyz.angm.cramolith.common.ecs.components.VelocityComponent
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.ecs.velocity
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.rox.Component
import xyz.angm.rox.ComponentMapper
import xyz.angm.rox.Engine

class WildPokemonComponent() : Component {
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

