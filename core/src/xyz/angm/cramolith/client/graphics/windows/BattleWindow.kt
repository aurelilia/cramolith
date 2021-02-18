/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 5:38 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.kotcrab.vis.ui.widget.*
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.actors.then
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
import xyz.angm.cramolith.common.pokemon.battle.*
import kotlin.math.max

class BattleWindow(private val screen: GameScreen, private val onComplete: (Boolean) -> Unit) : Window("battle") {

    private val battleTable = VisTable()
    private val messageTable: VisTable
    private val mapper = { it: Int -> screen.players[it] }
    private val player get() = screen.player[playerM]
    private val battle get() = screen.player[battleM].battle
    private val playerSide get() = (if ((battle.left as? PlayerOpponent)?.playerId == player.clientUUID) battle.left else battle.right) as PlayerOpponent
    private val playerSideE get() = if (playerSide == battle.left) BattleSide.Left else BattleSide.Right

    init {
        add(battleTable).row()
        updatePokemon()

        messageTable = scene2d.visTable { background = skin.getDrawable("dark-grey") }
        add(messageTable).expandX().fillX().height(100f).padTop(15f)
        mainMenu()

        centerWindow()
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
        screen.player[playerM].pokemon = update.playerPoke!!
        screen.player[battleM].battle = update.battle
        execEvents(update.turn)
    }

    private fun execEvents(events: ArrayList<TurnEvent>) {
        val action = Actions.action(SequenceAction::class.java)
        events.forEach { execEvent(it, action) }
        action += Actions.run {
            updatePokemon()
            mainMenu()
        }
        addAction(action)
    }

    private fun execEvent(event: TurnEvent, seq: SequenceAction) {
        val idx = if (event.side == BattleSide.Left) 0 else 1
        val side = battle.side(event.side)
        val cell = battleTable.cells.get(idx)

        when (event) {
            is Attack -> {
                seq then Actions.run {
                    val mon = side.activePokemon(mapper)
                    message(I18N.fmt("battle.attack-used", mon.displayName, Move.i18nMoveName(event.attackName)))
                    (cell.actor as PokemonDisplay).sprite +=
                        Actions.sequence(
                            Actions.moveBy(0f, 10f, 0.3f, Interpolation.exp10),
                            Actions.moveBy(0f, -10f, 0.3f, Interpolation.exp10)
                        )
                    val other = if (event.side == BattleSide.Left) 1 else 0
                    (battleTable.cells.get(other).actor as PokemonDisplay).goalHp -= event.damage
                }
                seq then Actions.delay(2.2f)
            }

            is Switch -> {
                seq then Actions.run {
                    val mon = side.activePokemon(mapper)
                    message(I18N.fmt("battle.switched-in", mon.displayName))
                    cell.actor.addAction(Actions.fadeOut(0.5f, Interpolation.pow2))
                }
                seq then Actions.delay(0.6f)
                seq then Actions.run {
                    val mon = side.activePokemon(mapper)
                    cell.setActor<VisTable>(PokemonDisplay(mon, event.hpAtSwitch))
                    cell.actor.alpha = 0f
                    cell.actor.addAction(Actions.fadeIn(0.5f, Interpolation.pow2))
                }
                seq then Actions.delay(1.6f)
            }

            is Fainted -> seq then Actions.run {
                val mon = side.activePokemon(mapper)
                message(I18N.fmt("battle.fainted", mon.displayName))
                cell.actor.addAction(Actions.fadeOut(1f, Interpolation.pow2))
            } then Actions.delay(2f)

            is BattleEnd -> seq then Actions.run { endBattle(event.side == playerSideE) }
        }
    }

    private fun endBattle(won: Boolean) {
        screen.player.remove(screen.engine, battleM)
        screen.player[playerM].pokemon.forEach { it.battleState = null }
        remove()
        onComplete(won)
    }

    private fun message(msg: String) {
        messageTable.clearChildren()
        messageTable.add(VisLabel(msg))
    }

    private fun msgBtn(text: String, clicked: () -> Unit) {
        val btn = VisTextButton(I18N.tryGet(text) ?: text)
        messageTable.add(btn).pad(8f).expand().fill()
        btn.onClick { clicked() }
    }

    class PokemonDisplay(private var pokemon: Pokemon, var goalHp: Int = pokemon.battleState!!.hp) : VisTable() {

        private var currentHp = goalHp.toFloat()
        private val nameLabel = VisLabel(pokemon.displayName)
        private val hpLabel = VisLabel("${currentHp.toInt()} / ${pokemon.hp}")
        private val hpBar = VisProgressBar(0f, pokemon.hp.toFloat(), 0.1f, false)
        val sprite = VisImage(pokemon.species.sprite)

        init {
            add(nameLabel).width(200f)
            add(hpLabel).row()

            hpBar.value = currentHp
            add(hpBar).fillX().expandX().colspan(2).row()

            add(sprite).colspan(2)
        }

        override fun act(delta: Float) {
            super.act(delta)
            if (goalHp < currentHp) {
                currentHp -= pokemon.hp * delta / 3f
                currentHp = max(currentHp, max(goalHp.toFloat(), 0f))
                hpLabel.setText("${currentHp.toInt()} / ${pokemon.hp}")
                hpBar.value = currentHp
            }
        }
    }
}