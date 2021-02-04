/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.server.Server
import xyz.angm.cramolith.server.database.*

internal fun Server.handleChatMessage(msg: ChatMessagePacket) {
    if (msg.receiver == 0) {
        sendToAll(msg)

        DB.transaction {
            Post.new {
                title = "nothing"
                text = msg.message
                user = msg.sender
            }
        }
    } else {
        val player = players[msg.receiver] ?: return
        send(player.conn, msg)

        DB.transaction {
            val entry = Friend.find {
                ((Friends.player1 eq msg.sender) and (Friends.player2 eq msg.receiver)) or
                        ((Friends.player1 eq msg.receiver) and (Friends.player2 eq msg.sender))
            }.firstOrNull()

            val friends = entry ?: Friend.new {
                player1 = DaoEntityID(msg.sender, Players)
                player2 = DaoEntityID(msg.receiver, Players)
            }

            PrivateMessage.new {
                chat = friends.id
                message = msg.message
            }
        }
    }
}