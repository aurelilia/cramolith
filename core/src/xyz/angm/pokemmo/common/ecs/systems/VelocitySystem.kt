/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 8:51 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs.systems

import xyz.angm.pokemmo.common.ecs.network
import xyz.angm.pokemmo.common.ecs.position
import xyz.angm.pokemmo.common.ecs.velocity
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