/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 8:36 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs.components

import xyz.angm.rox.Component

/** A component for all entities that must be synced between client and server.
 * Tracks the unique ID of the entity, as well as if it needs sync.
 * @property id The ID of the entity.
 * @property needsSync If the entity needs to be updated via network. */
class NetworkSyncComponent : Component {
    var id = System.nanoTime().toInt()
    var needsSync = true

    fun requireSync(require: Boolean) {
        needsSync = needsSync || require
    }
}