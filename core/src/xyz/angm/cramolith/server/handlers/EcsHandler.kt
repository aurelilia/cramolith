/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/21/21, 10:22 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server.handlers

import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.server.Connection
import xyz.angm.cramolith.server.Server
import xyz.angm.rox.Entity

internal fun Server.handleEntity(connection: Connection, entity: Entity) {
    engine {
        if (entity[playerM].clientUUID != playerByConnection(connection)?.key) return@engine
        netSystem.receive(entity)
        sendToAll(entity)
    }
}