/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common

import ch.qos.logback.classic.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging

/** Global logger. Log level can be set to DEBUG using --debug as first VM argument, otherwise it's WARN. */
val log = KotlinLogging.logger { }

/** Extension property for easily getting and setting log level. */
var KLogger.level: Level
    get() = (underlyingLogger as ch.qos.logback.classic.Logger).level
    set(value) {
        (underlyingLogger as ch.qos.logback.classic.Logger).level = value
    }

/** Schedules a closure to run at given intervals, using the provided scope.
 * Used as a replacement for java timers. */
fun schedule(initial: Long, delay: Long, scope: CoroutineScope, run: () -> Unit) {
    val ticker = ticker(delay, initial, scope.coroutineContext)
    scope.launch {
        while (true) {
            ticker.receive()
            run()
        }
    }
}

/** Run the given closure, catching and logging any exceptions that occur instead of
 * having them go up the call chain. The string parameters are used to generate
 * the accompanying error message, see the catch block. */
inline fun runLogE(user: String, activity: String, run: () -> Unit) {
    try {
        run()
    } catch (e: Exception) {
        log.warn(e) { "$user encountered an exception while $activity:" }
    }
}