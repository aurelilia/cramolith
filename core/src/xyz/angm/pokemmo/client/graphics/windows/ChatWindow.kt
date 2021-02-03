/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 9:11 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.graphics.windows

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import ktx.actors.onClick
import ktx.actors.onKeyDown
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.vis.visTable
import ktx.scene2d.vis.visTextButton
import ktx.scene2d.vis.visTextField
import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.client.resources.I18N
import xyz.angm.pokemmo.common.ecs.playerM
import xyz.angm.pokemmo.common.networking.ChatMessagePacket


class ChatWindow(private val screen: GameScreen) : VisWindow(I18N["chat"]) {

    private val pane = TabbedPane()
    private val container = VisTable()
    private val chats = HashMap<String, Chat>()

    init {
        addCloseButton()
        setSize(500f, 500f)
        isResizable = true

        screen.client.addListener { packet ->
            if (packet is ChatMessagePacket) addMessage(packet)
        }

        pane.addListener(object : TabbedPaneAdapter() {
            override fun switchedTab(tab: Tab) {
                container.clearChildren()
                container.add(tab.contentTable).expand().fill()
            }
        })

        add(pane.table).expandX().fillX()
        row()
        add(container).expand().fill()

        val firstTabTable = scene2d.visTable {
            val field = visTextField { it.padBottom(15f) }
            row()
            visTextButton(I18N["add-chat"]) {
                onClick {
                    val sender = screen.onlinePlayers.find { it[playerM].name == field.text }?.get(playerM)
                    if (sender == null) Dialogs.showErrorDialog(stage, I18N["unknown-user"])
                    else chats[sender.name] ?: createTab(sender.name, sender.clientUUID)
                }
            }
        }
        val firstTab = ChatTab(I18N["new-chat"], firstTabTable, false)
        pane.add(firstTab)

        createTab("Global", 0)
    }

    private fun createTab(name: String, id: Int): Chat {
        val msgTable: VisTable
        val contentTable = scene2d.visTable {
            scrollPane {
                msgTable = visTable {}
                it.expand().fill()
            }

            row()

            visTextField {
                it.expandX().fillX()
                onKeyDown { if (it == Input.Keys.ENTER) onEnter(text, id) }
            }
        }

        val tab = ChatTab(name, contentTable, id != 0)
        val chat = Chat(tab, msgTable, ArrayList(), id)
        chats[name] = chat
        pane.add(tab)
        return chat
    }

    private fun addMessage(msg: ChatMessagePacket) {
        val sender = screen.onlinePlayers.find { it[playerM].clientUUID == msg.sender }!![playerM]
        val name = if (msg.receiver == 0) "Global" else sender.name
        val chat = chats[name] ?: createTab(sender.name, sender.clientUUID)
        chat.msgTable.add(VisLabel(msg.message)).row()
    }

    private fun onEnter(message: String, receiver: Int) {
        screen.client.send(
            ChatMessagePacket(
                message = formatMessage(message),
                sender = screen.player[playerM].clientUUID,
                receiver
            )
        )
        screen.popPanel()
    }

    private fun formatMessage(message: String) = "<[CYAN]${screen.player[playerM].name}[WHITE]> $message"

    private class ChatTab(private val title: String, private val table: Table, userClosable: Boolean) : Tab(false, userClosable) {
        override fun getTabTitle() = title
        override fun getContentTable() = table
    }

    private data class Chat(val tab: ChatTab, val msgTable: VisTable, val messages: ArrayList<ChatMessagePacket>, val otherId: Int)
}