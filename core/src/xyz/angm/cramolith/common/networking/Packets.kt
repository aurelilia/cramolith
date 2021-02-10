/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 9:31 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.networking

import xyz.angm.rox.Entity
import java.io.Serializable

/** Interface for data sent between client and server.
 * Not all data sent is wrapped in this; only in cases where the type of the object sent is not enough context. */
interface Packet : Serializable

/** Sent when the client joins the server.
 * @property user The user name.
 * @property password Password of the user. */
class JoinPacket(val user: String = "", val password: String = "") : Packet


/** A packet sent on first connect as a response to [JoinPacket].
 * Contains all data required by the client to begin init and world loading. */
class InitPacket(
    val player: Entity? = null, // never actually null, just there to allow empty constructor
    val entities: Array<Entity> = emptyArray(),
    val globalChatMessages: Array<String> = emptyArray()
) : Packet


/** Packet sent as response to am invalid login request.
 * Reason can be invalid user or wrong password.
 * @property reason Rejection reason as an I18N identifier. */
class LoginRejectedPacket(val reason: String = "") : Packet


/** Contains a chat message. Client sends it to server; server sends it to appropriate clients.
 * @param message The message to send
 * @param sender The UUID of the sender
 * @param receiver The UUID of the receiver; 0 is a special id for global chat. */
class ChatMessagePacket(
    val message: String = "",
    val sender: Int = 0,
    val receiver: Int = 0
) : Packet


/** A request for local chat messages. */
class PrivateMessageRequest(
    val requested: Int = 0,
    val requestedBy: Int = 0
) : Packet

class PrivateMessageResponse(
    val other: Int = 0,
    val messages: Array<String> = emptyArray()
) : Packet


/** Sent when a player changes map/dimension. Used to
 * allow other clients to update world actors.  */
class PlayerMapChangedPacket(val entityId: Int) : Packet