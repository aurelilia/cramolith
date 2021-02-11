/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 10:20 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.kotcrab.vis.ui.widget.*
import ktx.actors.onClick
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.cramolith.common.pokemon.battle.Battle

class BattleWindow(private val screen: GameScreen) : Window("battle") {

    private val battleTable = VisTable()
    private val messageTable: VisTable

    init {
        add(battleTable).row()
        update(screen.player[playerM].battle!!)

        messageTable = scene2d.visTable { background = skin.getDrawable("dark-grey") }
        add(messageTable).expandX().fillX().height(100f).padTop(15f)
        message("Hello!")

        pack()
    }

    private fun update(battle: Battle) {
        battleTable.clearChildren()
        battleTable.add(PokemonDisplay(battle.left.activePokemon(screen))).pad(20f)
        battleTable.add(PokemonDisplay(battle.right.activePokemon(screen))).pad(20f)
    }

    fun message(msg: String) {
        messageTable.clearChildren()
        messageTable.add(VisLabel(msg))
        msgBtn("battle.attack") {}
        msgBtn("battle.switch") {}
    }

    private fun msgBtn(text: String, clicked: VisTextButton.() -> Unit) {
        val btn = VisTextButton(I18N[text])
        messageTable.add(btn).pad(8f)
        btn.onClick(clicked)
    }

    class PokemonDisplay(pokemon: Pokemon) : VisTable() {

        private val nameLabel = VisLabel(pokemon.displayName)
        private val hpLabel = VisLabel("${pokemon.battleState?.hp ?: 10} / ${pokemon.hp}")
        private val hpBar = VisProgressBar(0f, pokemon.hp.toFloat(), 1f, false)
        private val sprite = VisImage(pokemon.species.sprite)

        init {
            add(nameLabel).width(200f)
            add(hpLabel).row()

            hpBar.value = (pokemon.battleState?.hp ?: 10).toFloat()
            add(hpBar).fillX().expandX().colspan(2).row()

            add(sprite).colspan(2)
        }
    }
}