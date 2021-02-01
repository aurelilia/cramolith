/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/** A simple channel-based synchronization technique that
 * ensures an object can only be accessed by one thread at a time.
 * Call invoke to modify the object; the given closure is queued and
 * the function returns immediately, executing the closure once
 * the channel is free. */
class SyncChannel<T>(receiver: T, private val scope: CoroutineScope) {

    private val channel = Channel<T.() -> Unit>()

    init {
        scope.launch {
            while (true) {
                runLogE("Game", "processing sync call") {
                    channel.receive()(receiver)
                }
            }
        }
    }

    operator fun invoke(fn: T.() -> Unit) {
        scope.launch { channel.send(fn) }
    }
}