/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/17/21, 9:27 PM.
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
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.networking.*
import xyz.angm.cramolith.server.Connection
import xyz.angm.cramolith.server.Server
import xyz.angm.cramolith.server.database.*

internal fun Server.handleGlobalChatMsg(connection: Connection, msg: GlobalChatMsg) {
    val player = playerByConnection(connection) ?: return
    val post = DB.transaction {
        Post.new {
            title = msg.title
            text = msg.text
            user = EntityID(player.key, Players)
        }
    }
    msg.id = post.id.value
    msg.username = player.value.entity[playerM].name
    msg.userId = player.key
    sendToAll(msg)
}

internal fun Server.handleCommentPacket(msg: CommentPacket) {
    sendToAll(msg)
    DB.transaction {
        val cPost = Post.findById(msg.postId) ?: return@transaction
        Comment.new {
            post = cPost.id
            text = msg.comment
            user = EntityID(msg.userId, Players)
        }
    }
}

internal fun Server.handlePrivateMessage(msg: PrivateMessagePacket) {
    if (msg.receiver == 0) {
        sendToAll(msg)
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