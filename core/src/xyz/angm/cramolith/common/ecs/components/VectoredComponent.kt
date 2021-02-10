/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 8:58 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs.components

import com.badlogic.gdx.math.Vector2
import xyz.angm.rox.Component

/** A simple class for components containing a float vector.
 * @property x The first/X axis.
 * @property y The second/Y axis. */
abstract class VectoredComponent : Vector2(), Component {
    override fun toString() = "($x | $y)"
    fun toStringFloor() = "(${x.toInt()} | ${y.toInt()})"
}


/** Component for all entities with an in-world position. */
class PositionComponent : VectoredComponent() {
    var map = 0
}


/** Component for all entities with a velocity. Also requires a position component. */
class VelocityComponent : VectoredComponent()