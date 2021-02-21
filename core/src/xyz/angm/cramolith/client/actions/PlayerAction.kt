/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/21/21, 2:22 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.graphics.windows.DebugWindow
import xyz.angm.cramolith.client.graphics.windows.OnlinePlayersWindow
import xyz.angm.cramolith.client.graphics.windows.PartyWindow
import xyz.angm.cramolith.client.graphics.windows.Window
import xyz.angm.cramolith.client.resources.configuration
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.ecs.velocity

/** An action represents a function to be executed when the player presses a key.
 * @property type The internal name for an action, ex. 'walkLeft'
 * @property keyDown Function to be executed when the key is pressed down
 * @property keyUp Function to be executed when the key is released, can be null (which will be an empty function) */
data class PlayerAction(
    val type: String,
    val keyDown: (GameScreen) -> Unit,
    val keyUp: (GameScreen) -> Unit,
    var triggered: Boolean = false
)

/** The object that contains all actions and allows retrieving them. */
object PlayerActions {

    val actions = ObjectMap<String, PlayerAction>()
    var triggeredWalk: String? = null
    var deactivatedWalk: String? = null

    init {
        fun add(name: String, down: (GameScreen) -> Unit, up: (GameScreen) -> Unit) {
            actions[name] = PlayerAction(name, down, up)
        }

        fun add(name: String, down: (GameScreen) -> Unit) = add(name, down, {})

        fun window(ident: String, init: (GameScreen) -> Window) = add(ident) { it.toggleWindow(ident, init) }

        fun walk(dir: String, sprite: Int, down: (GameScreen) -> Unit, up: (GameScreen) -> Unit) {
            val name = "walk${dir.capitalize()}"
            add(name, {
                deactivatedWalk = null
                if (triggeredWalk != null) {
                    val trig = triggeredWalk
                    val action = actions[triggeredWalk!!]
                    action.triggered = false
                    action.keyUp(it)
                    deactivatedWalk = trig!!
                }
                triggeredWalk = name
                down(it)
                it.player[playerM].sprite = sprite + 1
            }, {
                up(it)
                it.player[playerM].sprite -= 1
                triggeredWalk = null
                if (deactivatedWalk != null) {
                    val action = actions[deactivatedWalk!!]
                    val bind = configuration.keybinds[deactivatedWalk!!] ?: -1
                    if (Gdx.input.isKeyPressed(bind)) {
                        action.triggered = true
                        action.keyDown(it)
                    }
                }
            })
        }

        walk("forward", 0, { it.player[velocity].y++ }, { it.player[velocity].y-- })
        walk("backward", 2, { it.player[velocity].y-- }, { it.player[velocity].y++ })
        walk("left", 4, { it.player[velocity].x-- }, { it.player[velocity].x++ })
        walk("right", 6, { it.player[velocity].x++ }, { it.player[velocity].x-- })

        window("onlinePlayers") { OnlinePlayersWindow(it) }
        window("chat") { throw UnsupportedOperationException() }
        window("debug") { DebugWindow(it) }
        window("party") { PartyWindow(it) }
    }

    /** Get an action. */
    operator fun get(type: String): PlayerAction? = actions[type]
}
