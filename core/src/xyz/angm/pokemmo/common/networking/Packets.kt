/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.networking

import xyz.angm.rox.Entity
import java.io.Serializable

/** Interface for data sent between client and server.
 * Not all data sent is wrapped in this; only in cases where the type of the object sent is not enough context. */
interface Packet : Serializable

/** Sent when the client joins the server.
 * @property uuid The UUID of the client connecting.
 * @property name The name of the client. Only used if the client is connecting for the first time. */
class JoinPacket(val name: String = "Player", val uuid: Int = 0) : Packet


/** A packet sent on first connect as a response to [JoinPacket].
 * Contains all data required by the client to begin init and world loading. */
class InitPacket(
    val player: Entity? = null, // never actually null, just there to allow empty constructor
    val entities: Array<Entity> = emptyArray()
) : Packet


/** Contains a chat message. Client sends it to server; server sends it to all clients.
 * @param message The message to send */
class ChatMessagePacket(val message: String = "") : Packet
