/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/9/21, 6:28 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.onKeyDown
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.textField
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.panels.Panel
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.client.graphics.screens.MenuScreen
import xyz.angm.cramolith.client.resources.I18N

/** Login panel pushed after pressing 'Start' in the main menu. */
class LoginPanel(private val screen: MenuScreen) : Panel(screen) {

    private val user: TextField
    private val password: TextField

    init {
        this += scene2d.visTable {
            visLabel(I18N["login.login"]) { it.pad(20f).row() }

            onKeyDown { keycode ->
                when (keycode) {
                    Input.Keys.ESCAPE, Input.Keys.ENTER -> start()
                }
            }

            user = textField { it.width(400f).pad(20f).padBottom(40f).row() }
            focusedActor = user
            password = textField {
                it.width(400f).pad(20f).padBottom(40f).row()
                isPasswordMode = true
            }

            textBtn("login.start") { start() }

            backButton(screen)

            setFillParent(true)
        }
    }

    private fun start() {
        if (user.text.isBlank() || password.text.isBlank()) return
        screen.connectToServer(user.text, password.text)
    }
}