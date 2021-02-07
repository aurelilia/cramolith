/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 10:37 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import ktx.collections.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import xyz.angm.cramolith.common.ecs.components.NetworkSyncComponent
import xyz.angm.cramolith.common.ecs.components.PositionComponent
import xyz.angm.cramolith.common.ecs.components.RemoveFlag
import xyz.angm.cramolith.common.ecs.components.VelocityComponent
import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.networking.InitPacket
import xyz.angm.cramolith.common.networking.JoinPacket
import xyz.angm.cramolith.common.networking.LoginRejectedPacket
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.cramolith.server.Connection
import xyz.angm.cramolith.server.Server
import xyz.angm.cramolith.server.database.DB
import xyz.angm.cramolith.server.database.Player
import xyz.angm.cramolith.server.database.Players
import xyz.angm.cramolith.server.database.Posts
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity

internal fun Server.handleJoinPacket(connection: Connection, packet: JoinPacket) {
    var globalMessages = emptyArray<String>()
    val dbEntry = DB.transaction {
        val player = Player.find { Players.name eq packet.user }.firstOrNull()

        val list = Posts.selectAll().orderBy(Posts.id, SortOrder.DESC).limit(25).toMutableList()
        list.reverse()
        globalMessages = list.stream().map { it[Posts.text] }.toArray { arrayOfNulls(it) }

        player
    }

    val error = when {
        dbEntry == null -> "login.unknown-user"
        packet.password != dbEntry.password -> "login.wrong-password"
        else -> null
    }
    if (error == null) engine {
        val entities = this[networkedFamily].toArray(Entity::class)
        val playerEntity = createPlayerEntity(this, dbEntry!!)
        players[dbEntry.id.value] = Server.OnlinePlayer(connection, playerEntity)

        send(connection, InitPacket(playerEntity, entities, globalMessages))
        playerEntity[network].needsSync = true // Ensure player gets synced
    } else {
        send(connection, LoginRejectedPacket(error))
        connection.channel.close()
    }
}

/** Create a new player entity. */
fun createPlayerEntity(engine: Engine, dbEntry: Player) =
    engine.entity {
        with<PlayerComponent> {
            name = dbEntry.name
            clientUUID = dbEntry.id.value
            pokemon.add(Pokemon("pikachu", "Test Subject", 20, 64, arrayListOf("thundershock")))
            pokemon.add(Pokemon("pikachu", "pika!", 10, 30, arrayListOf("quickattack", "thundershock")))
        }
        with<PositionComponent> { set(dbEntry.posX.toFloat(), dbEntry.posY.toFloat()) }
        with<VelocityComponent>()
        with<NetworkSyncComponent>()
    }

internal fun Server.handleDisconnect(connection: Connection) {
    val player = players.find { it.value.conn.id == connection.id } ?: return
    engine { RemoveFlag.flag(this, player.value.entity) }

    val playerC = player.value.entity[playerM]
    val posC = player.value.entity[position]
    DB.transaction {
        val db = Player.findById(playerC.clientUUID) ?: return@transaction
        db.posX = posC.x.toInt()
        db.posY = posC.y.toInt()
    }
}