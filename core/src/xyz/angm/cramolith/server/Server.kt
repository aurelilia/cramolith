/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 10:37 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server

import com.badlogic.gdx.utils.IntMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import xyz.angm.cramolith.common.*
import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.ecs.systems.NetworkSystem
import xyz.angm.cramolith.common.ecs.systems.RemoveSystem
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.common.networking.JoinPacket
import xyz.angm.cramolith.common.networking.Packet
import xyz.angm.cramolith.common.networking.PrivateMessageRequest
import xyz.angm.cramolith.server.handlers.*
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

    val engine = SyncChannel(Engine())
    internal val netSystem = NetworkSystem(::sendToAll)
    internal val players = IntMap<OnlinePlayer>() // Key is the player UUID
    internal val networkedFamily = allOf(network)

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

    internal fun send(connection: Connection, packet: Packet) {
        serverSocket.send(packet, connection)
        log.trace { "[SERVER] Sent packet of class ${packet.javaClass.name} to ${connection.ip}" }
    }

    /** Send a packet to all connected clients. */
    internal fun sendToAll(packet: Any) {
        serverSocket.sendAll(packet)
        log.trace { "[SERVER] Sent packet of class ${packet.javaClass.name} to all" }
    }

    internal fun received(connection: Connection, packet: Any) {
        runLogE("Server", "answering request") {
            receivedInternal(connection, packet)
        }
    }

    private fun receivedInternal(connection: Connection, packet: Any) {
        log.trace { "[SERVER] Received object of class ${packet.javaClass.name}" }
        when (packet) {
            is ChatMessagePacket -> handleChatMessage(packet)
            is PrivateMessageRequest -> handlePMRequest(connection, packet)
            is JoinPacket -> handleJoinPacket(connection, packet)
            is Entity -> handleEntity(packet)
        }
    }

    internal fun onConnected(connection: Connection) {
        log.info { "[SERVER] Player connected. IP: ${connection.ip}." }
    }

    internal fun onDisconnected(connection: Connection) {
        handleDisconnect(connection)
        log.info { "[SERVER] Disconnected from connection id ${connection.id}." }
    }

    /** Perform a tick, stepping the engine forward. */
    private fun tick() = engine { update(1f / TICK_RATE) }

    /** Close the server. Will save world and close all connections, making the object unusable. */
    private fun close() {
        log.info { "[SERVER] Shutting down..." }
        serverSocket.close()
        coScope.cancel()
    }

    internal data class OnlinePlayer(val conn: Connection, val entity: Entity)
}
