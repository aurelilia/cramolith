/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 8:50 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.server.handlers

import xyz.angm.pokemmo.server.Server
import xyz.angm.rox.Entity

internal fun Server.handleEntity(entity: Entity) {
    engine {
        netSystem.receive(entity)
        sendToAll(entity)
    }
}