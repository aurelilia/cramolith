/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 6:25 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server.handlers

import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import xyz.angm.pokemmo.common.ecs.components.NetworkSyncComponent
import xyz.angm.pokemmo.common.ecs.components.PositionComponent
import xyz.angm.pokemmo.common.ecs.components.VelocityComponent
import xyz.angm.pokemmo.common.ecs.components.specific.PlayerComponent
import xyz.angm.pokemmo.common.ecs.network
import xyz.angm.pokemmo.common.networking.InitPacket
import xyz.angm.pokemmo.common.networking.JoinPacket
import xyz.angm.pokemmo.server.Connection
import xyz.angm.pokemmo.server.Server
import xyz.angm.pokemmo.server.database.DB
import xyz.angm.pokemmo.server.database.Player
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity

internal fun Server.handleJoinPacket(connection: Connection, packet: JoinPacket) {
    val dbEntry = DB.transaction {
        Player.findById(packet.uuid) ?: Player.new {
            name = packet.name
        }
    }

    engine {
        val entities = this[networkedFamily].toArray(Entity::class)
        val playerEntity = createPlayerEntity(this, dbEntry)
        players[packet.uuid] = Server.OnlinePlayer(connection, playerEntity)

        send(connection, InitPacket(playerEntity, entities))
        playerEntity[network].needsSync = true // Ensure player gets synced
    }
}

/** The default player spawn location. */
private val defaultSpawnLocation = Vector2(100f, 100f)

/** Create a new player entity. */
fun createPlayerEntity(engine: Engine, dbEntry: Player) =
    engine.entity {
        with<PlayerComponent> {
            name = dbEntry.name
            clientUUID = dbEntry.id.value
        }
        with<PositionComponent> { set(defaultSpawnLocation) }
        with<VelocityComponent>()
        with<NetworkSyncComponent>()
    }
