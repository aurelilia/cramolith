/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/6/21, 7:25 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows


import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener
import com.kotcrab.vis.ui.widget.VisImage
import ktx.actors.onClick
import ktx.scene2d.scene2d
import ktx.scene2d.vis.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.networking.PokemonReleasedPacket
import xyz.angm.cramolith.common.pokemon.Move
import xyz.angm.cramolith.common.pokemon.Pokemon

class PokemonSummaryWindow(screen: GameScreen, pokemon: Pokemon) : Window("summary") {

    init {
        addCloseButton()
        add(VisImage(pokemon.species.sprite))
        add(scene2d.visTable {
            left()
            visTable {
                it.expandX().fillX()
                visLabel(pokemon.displayName) { it.expandX().fillX().padRight(10f) }
                visLabel(I18N["type.${pokemon.species.type}"]) { color = pokemon.species.type.color }
            }
            row()

            visLabel("${I18N["pokemon.level"]}: ${pokemon.level}") {
                visTextTooltip("${I18N["pokemon.exp-left"]}: ${pokemon.expLeft}")
                it.expandX().fillX().row()
            }
            visLabel("${I18N["pokemon.hp"]}: ${pokemon.hp}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.attack"]}: ${pokemon.attack}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.defense"]}: ${pokemon.defense}") { it.expandX().fillX().row() }
            visLabel("${I18N["pokemon.speed"]}: ${pokemon.speed}") { it.expandX().fillX().row() }

            visLabel(I18N["pokemon.moves"]) { it.expandX().fillX().padTop(10f).row() }
            for (move in pokemon.moveIds) {
                val move = Move.of(move)
                visLabel(move.name) {
                    color = move.type.color
                    visTooltip(scene2d.visTable {
                        visLabel(I18N["type.${move.type}"]) {
                            color = move.type.color
                            it.expandX().fillX().row()
                        }
                        visLabel("${I18N["pokemon.move.damage"]}: ${move.damage}") { it.expandX().fillX().row() }
                        visLabel("${I18N["pokemon.move.accuracy"]}: ${move.accuracy}") { it.expandX().fillX().row() }
                    })
                    it.expandX().fillX().row()
                }
            }

            visTextButton(I18N["pokemon.release"]) {
                onClick {
                    Dialogs.showOptionDialog(stage, I18N["pokemon.release-title"], I18N["pokemon.release-confirm"], Dialogs.OptionDialogType.YES_CANCEL,
                        object : OptionDialogListener {
                            override fun yes() {
                                screen.player[playerM].pokemon.remove(pokemon)
                                this@PokemonSummaryWindow.remove()
                                screen.client.send(PokemonReleasedPacket(pokemon.uuid))
                            }

                            override fun no() = throw UnsupportedOperationException()
                            override fun cancel() {}
                        }
                    )
                }
            }

            pack()
            pad(15f)
        })
        pack()
        centerWindow()
        isResizable = true
    }
}