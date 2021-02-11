/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 10:41 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.util.dialog.Dialogs
import xyz.angm.cramolith.client.graphics.panels.textBtn
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.ecs.position

class DebugWindow(private val screen: GameScreen) : Window("debug") {

    private val text = Label("", skin)

    init {
        addCloseButton()
        add(text).row()
        textBtn("Clear tripped actors") {
            screen.player[playerM].actorsTriggered.clear()
            Dialogs.showOKDialog(stage, "cleared", "triggers cleared.")
        }
        act(0f)
        pack()
    }

    override fun act(delta: Float) {
        super.act(delta)
        val txt = """
        FPS: ${Gdx.graphics.framesPerSecond}
        Time since last frame: ${(Gdx.graphics.deltaTime * 1000).format(1)}ms

        Heap Size: ${Runtime.getRuntime().totalMemory()}
        Heap Free: ${Runtime.getRuntime().freeMemory()}

        OpenGL ${Gdx.graphics.glVersion.majorVersion}: ${Gdx.graphics.glVersion.rendererString}
        Display: ${Gdx.graphics.displayMode}

        Player position: ${screen.player[position].toStringFloor()} / ${screen.player[position]}
        
        Entities loaded: ${screen.entitiesLoaded}
        ECS systems active: ${screen.systemsActive}
        """.trimIndent()
        text.setText(txt)
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}