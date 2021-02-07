/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 3:29 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.ecs

import com.badlogic.gdx.utils.IntMap
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.rox.Entity
import xyz.angm.rox.EntityListener
import xyz.angm.rox.Family.Companion.allOf

class PlayerMapper : EntityListener {

    override val family = allOf(playerM)
    private val map = IntMap<Entity>()

    override fun entityAdded(entity: Entity) {
        map.put(entity[playerM].clientUUID, entity)
    }

    override fun entityRemoved(entity: Entity) {
        map.remove(entity[playerM].clientUUID)
    }

    operator fun get(id: Int) = map[id]!![playerM]
}