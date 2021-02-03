/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 1:53 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Friends : Table() {
    val player1 = reference("player1", Players)
    val player2 = reference("player2", Players)
    override val primaryKey = PrimaryKey(player1, player2)
}

object Posts : IntIdTable() {
    val title = varchar("title", 100)
    val text = varchar("text", 1000)
}

class Post(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Post>(Posts)

    val title by Posts.title
    val text by Posts.text
    val comments by Comment referrersOn Comments.post
}

object Comments : IntIdTable() {
    val post = reference("post", Posts)
    val text = varchar("text", 500)
}

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    val title by Posts.title
    val text by Posts.text
}
