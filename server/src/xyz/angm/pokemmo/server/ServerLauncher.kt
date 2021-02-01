/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server

import ch.qos.logback.classic.Level
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import xyz.angm.pokemmo.common.level
import xyz.angm.pokemmo.common.log
import kotlin.system.exitProcess

/** To be used with HeadlessApplication for creating a standalone server */
class ServerLauncher : ApplicationAdapter() {
    /** Called on application creation */
    override fun create() {
        Server()
    }
}

/** Starts a server on the current thread. */
fun main(arg: Array<String>) {
    log.level = if (arg.isNotEmpty() && arg[0] == "--debug") Level.ALL else Level.DEBUG
    Thread.setDefaultUncaughtExceptionHandler(::handleException)
    HeadlessApplication(ServerLauncher())
}

/** Handle exceptions */
private fun handleException(thread: Thread, throwable: Throwable) {
    Gdx.app?.exit()
    log.error { "Whoops. This shouldn't have happened..." }
    log.error(throwable) { "Exception in thread ${thread.name}:\n" }
    log.error { "Server is shutting down." }
    exitProcess(-1)
}
