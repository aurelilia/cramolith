/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.panels.game

import com.badlogic.gdx.Input
import com.kotcrab.vis.ui.widget.VisTextField
import ktx.actors.onKeyDown

import xyz.angm.pokemmo.client.graphics.panels.Panel
import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.common.ecs.playerM
import xyz.angm.pokemmo.common.networking.ChatMessagePacket

/** Panel for typing chat messages. */
class ChatPanel(private val screen: GameScreen) : Panel(screen) {

    private val inputField = VisTextField("", "chat-input")

    init {
        background = null

        addActor(inputField)
        inputField.setPosition(10f, 60f)
        inputField.width = 400f

        inputField.onKeyDown { if (it == Input.Keys.ENTER) onEnter(inputField.text) }
        focusedActor = inputField

        screen.gameplayPanel.displayChat()
    }

    private fun onEnter(message: String) {
        screen.client.send(ChatMessagePacket(formatMessage(message)))
        screen.popPanel()
    }

    private fun formatMessage(message: String) = "<[CYAN]${screen.player[playerM].name}[WHITE]> $message"
}