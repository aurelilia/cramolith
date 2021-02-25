/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/25/21, 1:26 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.kotcrab.vis.ui.util.dialog.Dialogs
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

class BattleWindow(private val screen: GameScreen, msg: String, private val onComplete: (Boolean) -> Unit) : Window("battle") {

    private val battleTable = VisTable()
    private val message = VisLabel()
    private val buttonsTable = scene2d.visTable { background = skin.getDrawable("dark-grey") }
    private val backButton = msgBtn("back") { mainMenu(I18N["battle.what-do"]) }

    private val mapper = { it: Int -> screen.players[it] }
    private val player get() = screen.player[playerM]
    private val battle get() = screen.player[battleM].battle
    private val isWildEncounter get() = (battle.right as? AiOpponent)?.isWild ?: false
    private val playerSide get() = (if ((battle.left as? PlayerOpponent)?.playerId == player.clientUUID) battle.left else battle.right) as PlayerOpponent
    private val playerSideE get() = if (playerSide == battle.left) BattleSide.Left else BattleSide.Right

    init {
        add(battleTable).row()
        updatePokemon()

        add(buttonsTable).expandX().fillX().height(100f).padTop(15f).row()
        val msgTable = VisTable()
        msgTable.add(message).expandX().fillX().height(25f).padTop(5f).padBottom(5f)
        msgTable.add(backButton)
        add(msgTable).expandX().fillX().height(25f).padTop(5f).padBottom(5f)
        backButton.isVisible = false

        mainMenu(msg)
        centerWindow()
        pack()
    }

    private fun updatePokemon() {
        battleTable.clearChildren()
        battleTable.add(PokemonDisplay(battle.left.activePokemon(mapper))).pad(20f)
        battleTable.add(PokemonDisplay(battle.right.activePokemon(mapper))).pad(20f)
    }

    private fun mainMenu(msg: String) {
        buttonsTable.clearChildren()
        message.setText(msg)
        msgBtn("battle.attack", ::attacks)
        msgBtn("battle.switch", ::switch)
        if (isWildEncounter) {
            buttonsTable.row()
            msgBtn("battle.catch", ::catch)
            msgBtn("battle.run", ::run)
        }
    }

    private fun attacks() {
        message(I18N["battle.choose-attack"])
        buttonsTable.clearChildren()
        enableBackButton()
        playerSide.activePokemon(mapper).moveIds.forEachIndexed { idx, attack ->
            val move = Move.of(attack)
            if (idx % 2 == 0) buttonsTable.row()
            msgBtn(move.name) {
                playerSide.queuedAction = QueuedMove(idx)
                moveQueued()
            }
        }
    }

    private fun switch() {
        message(I18N["battle.choose-pokemon"])
        buttonsTable.clearChildren()
        enableBackButton()
        player.pokemon.forEachIndexed { idx, poke ->
            if (idx % 3 == 0) buttonsTable.row()
            msgBtn(poke.displayName) {
                playerSide.queuedAction = QueuedSwitch(idx)
                moveQueued()
            }
        }
    }

    private fun catch() {
        screen.player[playerM].pokemon.add(battle.right.activePokemon(mapper))
        Dialogs.showOKDialog(stage, I18N["battle.caught-title"], I18N.fmt("battle.caught", battle.right.activePokemon(mapper).displayName))
        endBattle(true)
    }

    private fun run() = endBattle(false)

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
            mainMenu(I18N["battle.what-do"])
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
                    val otherCell = battleTable.cells.get(if (idx == 0) 1 else 0)
                    (otherCell.actor as PokemonDisplay).sprite +=
                        Actions.sequence(
                            Actions.delay(0.5f),
                            Actions.moveBy(5f, 0f, 0.2f, Interpolation.pow2),
                            Actions.moveBy(-10f, 0f, 0.2f, Interpolation.pow2),
                            Actions.moveBy(10f, 0f, 0.2f, Interpolation.pow2),
                            Actions.moveBy(-5f, 0f, 0.2f, Interpolation.pow2)
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
        message.setText(msg)
        buttonsTable.clearChildren()
        backButton.isVisible = false
    }

    private fun msgBtn(text: String, clicked: () -> Unit): VisTextButton {
        val btn = VisTextButton(I18N.tryGet(text) ?: text)
        buttonsTable.add(btn).pad(5f).expand().fill().uniform()
        btn.onClick { clicked() }
        return btn
    }

    private fun enableBackButton() {
        backButton.isVisible = true
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