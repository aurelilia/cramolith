/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 3:08 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisProgressBar
import com.kotcrab.vis.ui.widget.VisTable
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.cramolith.common.pokemon.Species

class BattleWindow(screen: GameScreen) : Window("battle") {

    private val battleTable: VisTable
    private val messageTable: VisTable

    init {
        battleTable = scene2d.visTable {
            visImage(Species.of("pikachu").sprite)
        }
        add(battleTable).row()
        update(screen.player[playerM].pokemon[0], screen.player[playerM].pokemon[1])

        messageTable = scene2d.visTable { background = skin.getDrawable("dark-grey") }
        add(messageTable).expandX().fillX().height(100f).padTop(15f)
        message("Hello!")

        pack()
    }

    private fun update(pokemon1: Pokemon, pokemon2: Pokemon) {
        battleTable.clearChildren()
        battleTable.add(PokemonDisplay(pokemon1)).pad(20f)
        battleTable.add(PokemonDisplay(pokemon2)).pad(20f)
    }

    fun message(msg: String) {
        messageTable.clearChildren()
        messageTable.add(VisLabel(msg))
    }

    class PokemonDisplay(pokemon: Pokemon) : VisTable() {

        val nameLabel = VisLabel(pokemon.displayName)
        val hpLabel = VisLabel("${pokemon.battleState?.hp ?: 10} / ${pokemon.hp}")
        val hpBar = VisProgressBar(0f, pokemon.hp.toFloat(), 1f, false)
        val sprite = VisImage(pokemon.species.sprite)

        init {
            add(nameLabel).width(200f)
            add(hpLabel).row()

            hpBar.value = (pokemon.battleState?.hp ?: 10).toFloat()
            add(hpBar).fillX().expandX().colspan(2).row()

            add(sprite).colspan(2)
        }
    }
}