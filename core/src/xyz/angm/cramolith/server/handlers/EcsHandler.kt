/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import xyz.angm.cramolith.server.Server
import xyz.angm.rox.Entity

internal fun Server.handleEntity(entity: Entity) {
    engine {
        netSystem.receive(entity)
        sendToAll(entity)
    }
}