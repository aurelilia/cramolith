/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 6:22 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server

import com.badlogic.gdx.utils.IntMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import ktx.collections.*
import xyz.angm.pokemmo.common.*
import xyz.angm.pokemmo.common.ecs.components.NetworkSyncComponent
import xyz.angm.pokemmo.common.ecs.components.RemoveFlag
import xyz.angm.pokemmo.common.ecs.components.specific.PlayerComponent
import xyz.angm.pokemmo.common.ecs.network
import xyz.angm.pokemmo.common.ecs.systems.NetworkSystem
import xyz.angm.pokemmo.common.ecs.systems.RemoveSystem
import xyz.angm.pokemmo.common.networking.ChatMessagePacket
import xyz.angm.pokemmo.common.networking.InitPacket
import xyz.angm.pokemmo.common.networking.JoinPacket
import xyz.angm.pokemmo.common.networking.Packet
import xyz.angm.rox.Engine
import xyz.angm.rox.Entity
import xyz.angm.rox.EntityListener
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.EntitySystem

/** A server, handles the world and interacts with clients.
 * @property engine Manager for all types of entities */
class Server {

    private val serverSocket = NettyServerSocket(this)
    private val coScope = CoroutineScope(Dispatchers.Default)

    val engine = SyncChannel(Engine(), coScope)
    private val netSystem = NetworkSystem(::sendToAll)
    private val players = IntMap<Entity>() // Key is the connection id
    private val networkedFamily = allOf(NetworkSyncComponent::class)

    init {
        schedule(2000, 1000 / TICK_RATE, coScope, ::tick)
        engine {
            add(netSystem as EntityListener)
            add(netSystem as EntitySystem)
            add(RemoveSystem())
        }

        // Executed on SIGTERM
        Runtime.getRuntime().addShutdownHook(Thread { close() })
    }

    private fun send(connection: Connection, packet: Packet) {
        serverSocket.send(packet, connection)
        log.debug { "[SERVER] Sent packet of class ${packet.javaClass.name} to ${connection.ip}" }
    }

    /** Send a packet to all connected clients. */
    fun sendToAll(packet: Any) {
        serverSocket.sendAll(packet)
        log.debug { "[SERVER] Sent packet of class ${packet.javaClass.name} to all" }
    }

    internal fun received(connection: Connection, packet: Any) {
        runLogE("Server", "answering request") {
            receivedInternal(connection, packet)
        }
    }

    private fun receivedInternal(connection: Connection, packet: Any) {
        log.debug { "[SERVER] Received object of class ${packet.javaClass.name}" }
        when (packet) {
            is ChatMessagePacket -> sendToAll(packet)
            is JoinPacket -> registerPlayer(connection, packet)
            is Entity -> {
                engine {
                    packet.c(network)?.needsSync = true // Ensure it syncs to all players
                    netSystem.receive(packet)
                }
            }
        }
    }

    internal fun onConnected(connection: Connection) {
        log.info { "[SERVER] Player connected. IP: ${connection.ip}." }
    }

    private fun registerPlayer(connection: Connection, packet: JoinPacket) {
        engine {
            val entities = this[networkedFamily].toArray(Entity::class)
            val playerEntity = PlayerComponent.create(this, packet.name, packet.uuid)
            players[connection.id] = playerEntity

            send(connection, InitPacket(playerEntity, entities))
            playerEntity[network].needsSync = true // Ensure player gets synced
        }
    }

    internal fun onDisconnected(connection: Connection) {
        val player = players[connection.id] ?: return
        engine { RemoveFlag.flag(this, player) }
        log.info { "[SERVER] Disconnected from connection id ${connection.id}." }
    }

    /** Perform a tick, stepping the engine forward. */
    private fun tick() = engine { update(1f / TICK_RATE) }

    /** Close the server. Will save world and close all connections, making the object unusable. */
    fun close() {
        log.info { "[SERVER] Shutting down..." }
        serverSocket.close()
        coScope.cancel()
    }
}
