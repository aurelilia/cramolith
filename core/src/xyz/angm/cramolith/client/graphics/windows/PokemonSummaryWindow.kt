/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 12:59 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows


import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.pokemon.Move
import xyz.angm.cramolith.common.pokemon.Pokemon

class PokemonSummaryWindow(screen: GameScreen, pokemon: Pokemon) : Window("summary") {

    init {
        addCloseButton()
        this += scene2d.visTable {
            visImage(pokemon.species.sprite)
            visTable{
                visTable {
                    it.expandX().fillX()
                    visLabel(pokemon.displayName)
                    visLabel("type.${pokemon.species.type}") {color = pokemon.species.type.color }
                }.row()

                visLabel("${I18N["pokemon.level"]}${pokemon.level}").visTextTooltip(visLabel(I18N["pokemon.expLeft"]).toString()).row()
                visLabel("${I18N["pokemon.hp"]}${pokemon.hp}") {it.row() }
                visLabel("${I18N["pokemon.attack"]}${pokemon.attack}") {it.row()}
                visLabel("${I18N["pokemon.defense"]}${pokemon.defense}") {it.row()}
                visLabel("${I18N["pokemon.speed"]}${pokemon.speed}") {it.row()}
                for (move in pokemon.moveIdents){
                    visLabel(Move.of(move).name).visTooltip(visTable {
                        visLabel("type.${pokemon.species.type}") {color = pokemon.species.type.color; it.row()}
                        visLabel(Move.of(move).damage.toString())
                        visLabel(Move.of(move).accuracy.toString())
                    })
                }
            }.row()
            pack()
            setFillParent(true)
        }
    }
}