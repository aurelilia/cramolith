/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 3:08 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import java.io.Serializable

class Pokemon(
    private val speciesIdent: String,
    var nickname: String?,
    var level: Int,
    var exp: Int,
    private val moveIdents: ArrayList<String> = ArrayList(),
) : Serializable {
    val species get() = Species.of(speciesIdent)
    val displayName get() = nickname ?: species.name

    val hp get() = hpFormula(species.hp, level)
    val attack get() = otherFormula(species.attack, level)
    val defense get() = otherFormula(species.defense, level)
    val speed get() = otherFormula(species.speed, level)

    var battleState: PokeBattleState? = null
}

// https://bulbapedia.bulbagarden.net/wiki/Stat#Determination_of_stats
private fun hpFormula(base: Int, level: Int) = baseFormula(base, level) + level + 10
private fun otherFormula(base: Int, level: Int) = baseFormula(base, level) + 5
private fun baseFormula(base: Int, level: Int) = baseFormula(base.toDouble(), level.toDouble()).toInt()
private fun baseFormula(base: Double, level: Double) = (2 * base * level) / 100
