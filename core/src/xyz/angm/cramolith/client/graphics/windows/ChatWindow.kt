/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/1/21, 1:44 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
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
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.common.networking.PrivateMessageRequest
import xyz.angm.cramolith.common.networking.PrivateMessageResponse


class ChatWindow(private val screen: GameScreen, initMsgs: Array<String>) : Window("chat") {

    private val pane = TabbedPane()
    private val container = VisTable()
    private val chats = HashMap<String, Chat>()
    private val playerId get() = screen.player[playerM].clientUUID

    init {
        addCloseButton()
        setSize(500f, 500f)
        isResizable = true

        screen.client.addListener { packet ->
            when (packet) {
                is ChatMessagePacket -> addMessage(packet)
                is PrivateMessageResponse -> addMessages(packet)
            }
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
            visTextButton(I18N["chat.new"]) {
                onClick {
                    val sender = screen.onlinePlayers.find { it[playerM].name == field.text }?.get(playerM)
                    if (sender == null) Dialogs.showErrorDialog(stage, I18N["chat.unknown-user"])
                    else chats[sender.name] ?: createTab(sender.name, sender.clientUUID)
                }
            }
        }
        val firstTab = ChatTab(I18N["chat.new"], firstTabTable, false)
        pane.add(firstTab)

        val global = createTab("Global", 0)
        for (msg in initMsgs) {
            (global.msgTable.actor as VisTable).add(VisLabel(msg)).left().expandX().row()
        }
    }

    private fun createTab(name: String, id: Int): Chat {
        val msgTable: ScrollPane
        val contentTable = scene2d.visTable {
            msgTable = scrollPane {
                actor = visTable {}
                setScrollbarsVisible(true)
                it.expand().fill()
            }

            row()

            visTextField {
                it.expandX().fillX()
                onKeyDown {
                    if (it == Input.Keys.ENTER && text.isNotBlank()) {
                        val message = formatMessage(text)
                        screen.client.send(
                            ChatMessagePacket(
                                message,
                                sender = playerId,
                                receiver = id
                            )
                        )
                        if (id != 0) (msgTable.actor as VisTable).add(VisLabel(message)).left().expandX().row()
                        text = ""
                    }
                }
            }
        }

        val tab = ChatTab(name, contentTable, id != 0)
        val chat = Chat(tab, msgTable, ArrayList(), id)
        chats[name] = chat
        pane.add(tab)

        if (id != 0) {
            screen.client.send(PrivateMessageRequest(requested = id, requestedBy = playerId))
        }

        return chat
    }

    private fun addMessage(msg: ChatMessagePacket) = insertMessage(getChat(msg.sender, msg.receiver), msg.message)

    private fun addMessages(packet: PrivateMessageResponse) {
        val chat = getChat(packet.other, -1)
        for (msg in packet.messages) insertMessage(chat, msg)
    }

    private fun insertMessage(chat: Chat, msg: String) {
        val cell = (chat.msgTable.actor as VisTable).add(VisLabel(msg)).left().expandX()
        cell.row()
        chat.msgTable.layout()
        chat.msgTable.scrollTo(0f, cell.actorY, 0f, 0f)
    }

    private fun getChat(sender: Int, receiver: Int): Chat {
        val sender = screen.onlinePlayers.find { it[playerM].clientUUID == sender }?.get(playerM)
        val name = if (receiver == 0 || sender == null) "Global" else sender.name
        return chats[name] ?: createTab(sender!!.name, sender.clientUUID)
    }

    private fun formatMessage(message: String) = "<[CYAN]${screen.player[playerM].name}[WHITE]> $message"

    private class ChatTab(private val title: String, private val table: Table, userClosable: Boolean) : Tab(false, userClosable) {
        override fun getTabTitle() = title
        override fun getContentTable() = table
    }

    private data class Chat(val tab: ChatTab, val msgTable: ScrollPane, val messages: ArrayList<ChatMessagePacket>, val otherId: Int)
}