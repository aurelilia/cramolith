/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import xyz.angm.cramolith.common.ecs.components.NetworkSyncComponent
import xyz.angm.cramolith.common.ecs.components.PositionComponent
import xyz.angm.cramolith.common.ecs.components.VelocityComponent
import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.networking.InitPacket
import xyz.angm.cramolith.common.networking.JoinPacket
import xyz.angm.cramolith.server.Connection
import xyz.angm.cramolith.server.Server
import xyz.angm.cramolith.server.database.DB
import xyz.angm.cramolith.server.database.Player
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
