/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 6:44 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection


object DB {
    private val database = Database.connect("jdbc:sqlite:pokemmo.sqlite3", "org.sqlite.JDBC")

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(Players, Pokemons, Friends, PrivateMessages, Posts, Comments)
        }
    }

    fun <T> transaction(statement: Transaction.() -> T) =
        org.jetbrains.exposed.sql.transactions.transaction(database, statement)
}
