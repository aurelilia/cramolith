/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Players : IntIdTable() {
    val name = varchar("name", 20)
}

class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    var name by Players.name
    val pokemon by Pokemon referrersOn Pokemons.owner
}

object Pokemons : IntIdTable() {
    val species = varchar("species", 15)
    val nickname = varchar("nickname", 25)
    val owner = reference("owner", Players)
    val move1 = integer("move1")
    val move2 = integer("move2")
    val move3 = integer("move3")
    val move4 = integer("move4")
}

class Pokemon(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Pokemon>(Pokemons)

    var species by Pokemons.species
    var nickname by Pokemons.nickname
    var owner by Pokemons.owner
    var move1 by Pokemons.move1
    var move2 by Pokemons.move2
    var move3 by Pokemons.move3
    var move4 by Pokemons.move4
}
