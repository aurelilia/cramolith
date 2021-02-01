/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs.components.specific

import com.badlogic.gdx.math.Vector2
import xyz.angm.pokemmo.common.ecs.components.NetworkSyncComponent
import xyz.angm.pokemmo.common.ecs.components.PositionComponent
import xyz.angm.pokemmo.common.ecs.components.VelocityComponent
import xyz.angm.rox.Component
import xyz.angm.rox.Engine

/** Component for all persistent player state.
 * @property name The (display)name of the player.
 * @property clientUUID The UUID of the client the player is from. */
class PlayerComponent : Component {

    lateinit var name: String
    var clientUUID: Int = 0

    companion object {
        /** The default player spawn location. */
        private val defaultSpawnLocation = Vector2(100f, 100f)

        /** Create a new player entity. */
        fun create(engine: Engine, pName: String, uuid: Int) =
            engine.entity {
                with<PlayerComponent> {
                    name = pName
                    clientUUID = uuid
                }
                with<PositionComponent> { set(defaultSpawnLocation) }
                with<VelocityComponent>()
                with<NetworkSyncComponent>()
            }
    }
}
