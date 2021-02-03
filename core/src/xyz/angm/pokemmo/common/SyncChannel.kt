/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 7:40 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common

class SyncChannel<T>(private val receiver: T) {

    @Synchronized
    operator fun invoke(fn: T.() -> Unit) {
        fn(receiver)
    }
}