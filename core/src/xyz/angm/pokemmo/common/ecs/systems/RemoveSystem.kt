/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs.systems

import xyz.angm.pokemmo.common.ecs.components.RemoveFlag
import xyz.angm.rox.Entity
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.IteratingSystem

/** A system that removes all entities with a [RemoveFlag].
 * ALWAYS ADD LAST TO ENSURE IT GETS EXECUTED AT THE END OF A CYCLE. */
class RemoveSystem : IteratingSystem(allOf(RemoveFlag::class), Int.MAX_VALUE) {
    /** Removes all entities with [RemoveFlag]. */
    override fun process(entity: Entity, delta: Float) = engine.remove(entity)
}