/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 3:47 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows


import com.kotcrab.vis.ui.widget.VisImage
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable
import ktx.scene2d.vis.visTextTooltip
import ktx.scene2d.vis.visTooltip
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.pokemon.Move
import xyz.angm.cramolith.common.pokemon.Pokemon

class PokemonSummaryWindow(pokemon: Pokemon) : Window("summary") {

    init {
        addCloseButton()
        add(VisImage(pokemon.species.sprite))
        add(scene2d.visTable {
            left()
            visTable {
                it.expandX().fillX()
                visLabel(pokemon.displayName) { it.expandX().fillX().padRight(10f) }
                visLabel("type.${pokemon.species.type}") { color = pokemon.species.type.color }
            }
            row()

            visLabel("${I18N["pokemon.level"]}: ${pokemon.level}") {
                visTextTooltip(pokemon.expLeft.toString())
                it.expandX().fillX().row()
            }
            visLabel("${I18N["pokemon.hp"]}: ${pokemon.hp}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.attack"]}: ${pokemon.attack}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.defense"]}: ${pokemon.defense}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.speed"]}: ${pokemon.speed}") { it.expandX().fillX().row() }
            for (move in pokemon.moveIdents) {
                val move = Move.of(move)
                visLabel(move.name) {
                    visTooltip(scene2d.visTable {
                        visLabel("type.${pokemon.species.type}") {
                            color = pokemon.species.type.color
                            it.row()
                        }
                        visLabel(move.damage.toString())
                        visLabel(move.accuracy.toString())
                    })
                    it.expandX().fillX().row()
                }
            }
            pack()
            pad(15f)
        })
        pack()
        isResizable = true
    }
}