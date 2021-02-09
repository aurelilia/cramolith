/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/8/21, 9:13 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.kotcrab.vis.ui.VisUI
import ktx.collections.*
import xyz.angm.cramolith.client.graphics.panels.menu.MessagePanel
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.graphics.screens.MenuScreen
import xyz.angm.cramolith.client.networking.Client
import xyz.angm.cramolith.common.networking.InitPacket
import xyz.angm.cramolith.common.networking.JoinPacket
import xyz.angm.cramolith.common.networking.LoginRejectedPacket
import kotlin.system.exitProcess

/** The game itself. Only sets the screen, everything else is handled per-screen. */
open class Cramolith : Game() {

    /** Called when libGDX environment is ready. */
    override fun create() {
        VisUI.load()
        setScreen(MenuScreen(this))
    }

    fun connectToServer(user: String, password: String) {
        val client = Client()
        client.addListener { packet ->
            when (packet) {
                is InitPacket -> Gdx.app.postRunnable { startGame(client, packet) }
                is LoginRejectedPacket -> {
                    client.close()
                    val screen = (screen as MenuScreen)
                    screen.popPanel()
                    screen.pushPanel(MessagePanel(screen, packet.reason) { screen.popPanel() })
                }
            }
        }
        client.send(JoinPacket(user, password))
    }

    private fun startGame(client: Client, data: InitPacket) {
        setScreen(GameScreen(this, client, data.player!!, data.entities, data.globalChatMessages))
    }

    override fun dispose() = exitProcess(0)

    companion object {

        private val runnables = GdxArray<() -> Unit>(10)

        /** Post a runnable to be run on the main thread on the next frame.
         * This is a replacement for `Gdx.app.postRunnable` provided by
         * libGDX, which cannot be used as it does not allow for running code
         * each frame before the runnables - which is needed to lock the client
         * and prevent race conditions.
         * Only works while in-game - MenuScreen does not process this!
         * For menus and other things that might be called while not
         * in-game you should simply use the libGDX provided method. */
        fun postRunnable(runnable: () -> Unit) = runnables.add(runnable)

        /** Called once per frame by [GameScreen], executes all runnables. */
        fun execRunnables() {
            runnables.forEach { it() }
            runnables.clear()
        }
    }
}
