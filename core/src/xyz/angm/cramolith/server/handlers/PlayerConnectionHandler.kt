/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/6/21, 6:46 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import ktx.assets.ignore
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
import xyz.angm.cramolith.server.database.*
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity
import xyz.angm.cramolith.server.database.Pokemon as DBPoke

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
            actorsTriggered = dbEntry.triggeredActors

            DB.transaction { // TODO 2 transactions per login is... suboptimal
                for (db in dbEntry.pokemon) {
                    val moves = arrayListOf(db.move1)
                    if (db.move2 != null) moves.add(db.move2!!)
                    if (db.move3 != null) moves.add(db.move3!!)
                    if (db.move4 != null) moves.add(db.move4!!)
                    pokemon.add(Pokemon(db.species, db.nickname, db.level, db.exp, moves, db.id.value))
                }
            }

            if (pokemon.isEmpty()) {
                pokemon.add(Pokemon("pikachu", "Test Subject", 20, 64, arrayListOf("thundershock")))
                pokemon.add(Pokemon("pikachu", "pika!", 10, 30, arrayListOf("quickattack", "thundershock")))
            }
        }
        with<PositionComponent> {
            set(dbEntry.posX.toFloat(), dbEntry.posY.toFloat())
            map = dbEntry.posMap
        }
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
        db.posMap = posC.map
        db.triggeredActors = playerC.actorsTriggered

        for (mon in playerC.pokemon.subList(0, 6)) {
            val writer: DBPoke.() -> Unit = {
                species = mon.species.ident
                nickname = mon.nickname
                owner = db.id
                level = mon.level
                exp = mon.exp

                try {
                    move1 = mon.moveIds[0]
                    move2 = mon.moveIds[1]
                    move3 = mon.moveIds[2]
                    move4 = mon.moveIds[3]
                } catch (e: IndexOutOfBoundsException) {
                    e.ignore()
                }
            }

            if (mon.uuid == -1) DBPoke.new(writer)
            else writer(DBPoke.find { Pokemons.id eq mon.uuid }.first())
        }
    }
}