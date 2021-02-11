/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 5:25 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import ch.qos.logback.classic.Level
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.kotcrab.vis.ui.VisUI
import xyz.angm.cramolith.client.Cramolith
import xyz.angm.cramolith.client.resources.ResourceManager
import xyz.angm.cramolith.common.level
import xyz.angm.cramolith.common.log
import kotlin.system.exitProcess

/** The LWJGL configuration used for the game */
val configuration = Lwjgl3ApplicationConfiguration()

/** The game instance */
val game = object : Cramolith() {
    override fun create() {
        VisUI.load()
        ResourceManager.init()
        ResourceManager.continueLoading(Int.MAX_VALUE)
        setScreen(EditorScreen())
    }
}

/** Initialize and launch the game. */
fun main(arg: Array<String>) {
    log.level = if (arg.contains("--debug")) Level.ALL else Level.WARN
    Thread.setDefaultUncaughtExceptionHandler { _, it ->
        Gdx.app?.exit()
        it.printStackTrace()
        exitProcess(-1)
    }

    setConfiguration()
    Lwjgl3Application(game, configuration)
}

/** Returns the LWJGL configuration. */
private fun setConfiguration() {
    configuration.setIdleFPS(1)
    configuration.useVsync(true)
    configuration.setTitle("CramolithEdit")
}