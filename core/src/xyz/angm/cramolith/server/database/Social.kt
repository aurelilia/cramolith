/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 2:16 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Friends : IntIdTable() {
    val player1 = reference("player1", Players)
    val player2 = reference("player2", Players)
}

class Friend(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Friend>(Friends)

    var player1 by Friends.player1
    var player2 by Friends.player2
    val privateMessages by PrivateMessage referrersOn PrivateMessages.chat
}

object PrivateMessages : IntIdTable() {
    val chat = reference("chat", Friends)
    val message = varchar("message", 1000)
}

class PrivateMessage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PrivateMessage>(PrivateMessages)

    var chat by PrivateMessages.chat
    var message by PrivateMessages.message
}

object Posts : IntIdTable() {
    val title = varchar("title", 100)
    val text = varchar("text", 1000)
    val user = integer("user")
}

class Post(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Post>(Posts)

    var title by Posts.title
    var text by Posts.text
    var user by Posts.user
    val comments by Comment referrersOn Comments.post
}

object Comments : IntIdTable() {
    val post = reference("post", Posts)
    val text = varchar("text", 500)
}

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    var title by Posts.title
    var text by Posts.text
}
