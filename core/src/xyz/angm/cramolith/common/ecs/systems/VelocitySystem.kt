/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs.systems

import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.ecs.velocity
import xyz.angm.rox.Entity
import xyz.angm.rox.Family
import xyz.angm.rox.systems.IteratingSystem

class VelocitySystem : IteratingSystem(Family.allOf(position, velocity)) {
    override fun process(entity: Entity, delta: Float) {
        val vel = entity[velocity]
        val pos = entity[position]
        pos.x += vel.x
        pos.y += vel.y
        entity.c(network)?.needsSync = true
    }
}