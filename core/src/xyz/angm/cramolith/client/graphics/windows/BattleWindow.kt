/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/13/21, 3:20 AM.
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
import xyz.angm.cramolith.common.ecs.battleM
import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.networking.BattleUpdatePacket
import xyz.angm.cramolith.common.pokemon.Move
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.cramolith.common.pokemon.battle.PlayerOpponent
import xyz.angm.cramolith.common.pokemon.battle.QueuedMove
import xyz.angm.cramolith.common.pokemon.battle.QueuedSwitch

class BattleWindow(private val screen: GameScreen, private val onComplete: () -> Unit) : Window("battle") {

    private val battleTable = VisTable()
    private val messageTable: VisTable
    private val mapper = { it: Int -> screen.players[it] }
    private val player get() = screen.player[playerM]
    private val battle get() = screen.player[battleM].battle
    private val playerSide get() = (if ((battle.left as? PlayerOpponent)?.playerId == player.clientUUID) battle.left else battle.right) as PlayerOpponent

    init {
        add(battleTable).row()
        updatePokemon()

        messageTable = scene2d.visTable { background = skin.getDrawable("dark-grey") }
        add(messageTable).expandX().fillX().height(100f).padTop(15f)
        mainMenu()

        pack()
    }

    private fun updatePokemon() {
        battleTable.clearChildren()
        battleTable.add(PokemonDisplay(battle.left.activePokemon(mapper))).pad(20f)
        battleTable.add(PokemonDisplay(battle.right.activePokemon(mapper))).pad(20f)
    }

    private fun mainMenu() {
        messageTable.clearChildren()
        msgBtn("battle.attack", ::attacks)
        msgBtn("battle.switch", ::switch)
    }

    private fun attacks() {
        messageTable.clearChildren()
        msgBtn("back") { mainMenu() }
        playerSide.activePokemon(mapper).moveIds.forEachIndexed { idx, attack ->
            val move = Move.of(attack)
            msgBtn(move.name) {
                playerSide.queuedAction = QueuedMove(idx)
                moveQueued()
            }
        }
    }

    private fun switch() {
        messageTable.clearChildren()
        msgBtn("back") { mainMenu() }
        player.pokemon.forEachIndexed { idx, poke ->
            msgBtn(poke.displayName) {
                playerSide.queuedAction = QueuedSwitch(idx)
                moveQueued()
            }
        }
    }

    private fun moveQueued() {
        screen.player[network].needsSync = true
        message(I18N["battle.waiting"])
    }

    fun battleUpdate(update: BattleUpdatePacket) {
        if (update.battle == null) {
            endBattle()
            return
        }
        screen.player[playerM].pokemon = update.playerPoke!!
        screen.player[battleM].battle = update.battle
        updatePokemon()
        mainMenu()
    }

    private fun endBattle() {
        screen.player.remove(screen.engine, battleM)
        screen.player[playerM].pokemon.forEach { it.battleState = null }
        remove()
        onComplete()
    }

    private fun message(msg: String) {
        messageTable.clearChildren()
        messageTable.add(VisLabel(msg))
        msgBtn("battle.attack") {}
        msgBtn("battle.switch") {}
    }

    private fun msgBtn(text: String, clicked: () -> Unit) {
        val btn = VisTextButton(I18N.tryGet(text) ?: text)
        messageTable.add(btn).pad(8f)
        btn.onClick { clicked() }
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