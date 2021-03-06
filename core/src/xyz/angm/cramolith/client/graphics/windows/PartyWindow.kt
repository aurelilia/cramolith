/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/6/21, 7:23 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Scaling
import com.kotcrab.vis.ui.widget.VisTable
import ktx.actors.onClick
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM

class PartyWindow(private val screen: GameScreen) : Window("party") {

    private var sinceLastUpdate = 0f
    private val table = VisTable()
    private val pane = ScrollPane(table)

    init {
        addCloseButton()
        add(pane)
        reload()
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceLastUpdate += delta
        if (sinceLastUpdate > 1f) {
            reload()
            sinceLastUpdate = 0f
        }
    }

    private fun reload() {
        table.apply {
            clearChildren()
            for (pokemon in screen.player[playerM].pokemon) {
                val button = scene2d.button("list") {
                    isDisabled = true
                    left().pad(5f)

                    visImage(pokemon.species.icon) {
                        it.height(54f).width(60f)
                        setScaling(Scaling.fit)
                    }
                    visLabel(pokemon.displayName) { it.expandX().fillX().padRight(30f) }
                    visLabel("${I18N["party.level"]} ${pokemon.level}")
                    pack()

                    onClick { stage.addActor(PokemonSummaryWindow(screen, pokemon)) }
                }
                add(button).expandX().fillX().row()
            }
        }
        pack()
    }
}