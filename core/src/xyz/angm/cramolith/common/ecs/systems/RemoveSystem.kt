/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs.systems

import xyz.angm.cramolith.common.ecs.components.RemoveFlag
import xyz.angm.cramolith.common.ecs.remove
import xyz.angm.rox.Entity
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.IteratingSystem

/** A system that removes all entities with a [RemoveFlag].
 * ALWAYS ADD LAST TO ENSURE IT GETS EXECUTED AT THE END OF A CYCLE. */
class RemoveSystem : IteratingSystem(allOf(remove), Int.MAX_VALUE) {
    /** Removes all entities with [RemoveFlag]. */
    override fun process(entity: Entity, delta: Float) = engine.remove(entity)
}