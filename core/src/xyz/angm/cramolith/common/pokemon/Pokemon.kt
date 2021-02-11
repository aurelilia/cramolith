/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 11:08 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import xyz.angm.cramolith.common.pokemon.battle.PokeBattleState
import java.io.Serializable

@kotlinx.serialization.Serializable
class Pokemon(
    private val speciesId: String = "",
    var nickname: String? = null,
    var level: Int = 0,
    var exp: Int = 0,
    val moveIds: ArrayList<String> = ArrayList(),
) : Serializable {
    val species get() = Species.of(speciesId)
    val displayName get() = nickname ?: species.name
    val expLeft get() = (level * level * level) - exp

    val hp get() = hpFormula(species.hp, level)
    val attack get() = otherFormula(species.attack, level)
    val defense get() = otherFormula(species.defense, level)
    val speed get() = otherFormula(species.speed, level)

    @Transient
    var battleState: PokeBattleState? = null
}

// https://bulbapedia.bulbagarden.net/wiki/Stat#Determination_of_stats
private fun hpFormula(base: Int, level: Int) = baseFormula(base, level) + level + 10
private fun otherFormula(base: Int, level: Int) = baseFormula(base, level) + 5
private fun baseFormula(base: Int, level: Int) = baseFormula(base.toDouble(), level.toDouble()).toInt()
private fun baseFormula(base: Double, level: Double) = (2 * base * level) / 100
