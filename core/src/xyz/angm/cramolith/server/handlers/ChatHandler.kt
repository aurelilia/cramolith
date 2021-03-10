/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/10/21, 8:36 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.common.networking.PrivateMessageRequest
import xyz.angm.cramolith.common.networking.PrivateMessageResponse
import xyz.angm.cramolith.server.Connection
import xyz.angm.cramolith.server.Server
import xyz.angm.cramolith.server.database.*

internal fun Server.handleChatMessage(msg: ChatMessagePacket) {
    if (msg.receiver == 0) {
        sendToAll(msg)

        DB.transaction {
            Post.new {
                title = "nothing"
                text = msg.message
                user = EntityID(msg.sender, Players)
            }
        }
    } else {
        val player = players[msg.receiver] ?: return
        send(player.conn, msg)

        DB.transaction {
            val friends = getFriends(msg.sender, msg.receiver)
            PrivateMessage.new {
                chat = friends.id
                message = msg.message
            }
        }
    }
}

internal fun Server.handlePMRequest(conn: Connection, msg: PrivateMessageRequest) {
    DB.transaction {
        val friends = getFriends(msg.requested, msg.requestedBy)
        val list = PrivateMessages.select { PrivateMessages.chat eq friends.id }.orderBy(PrivateMessages.id, SortOrder.DESC).limit(25).toMutableList()
        list.reverse()
        val messages: Array<String> = list.stream().map { it[PrivateMessages.message] }.toArray { arrayOfNulls(it) }
        send(conn, PrivateMessageResponse(msg.requested, messages))
    }
}

private fun getFriends(first: Int, second: Int): UserRelationship {
    val entry = UserRelationship.find {
        ((UserRelationships.player1 eq first) and (UserRelationships.player2 eq second)) or
                ((UserRelationships.player1 eq second) and (UserRelationships.player2 eq first))
    }.firstOrNull()

    return entry ?: UserRelationship.new {
        player1 = DaoEntityID(first, Players)
        player2 = DaoEntityID(second, Players)
    }
}