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

object Players : IntIdTable() {
    val name = varchar("name", 20)
}

class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    val name by Players.name
    val pokemon by Pokemon referrersOn Pokemons.owner
    val friends by Player via Friends
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

    val species by Pokemons.species
    val nickname by Pokemons.nickname
    val owner by Pokemons.owner
    val move1 by Pokemons.move1
    val move2 by Pokemons.move2
    val move3 by Pokemons.move3
    val move4 by Pokemons.move4
}
