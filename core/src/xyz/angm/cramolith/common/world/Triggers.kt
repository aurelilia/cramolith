/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 4:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class Trigger(val type: TriggerType, val x: Int, val y: Int, val width: Int, val height: Int, val idx: Int)

enum class TriggerType(val color: Color, val indexSays: String?) {
    Collision(Color.ROYAL, null),
    Teleport(Color.FOREST, "Map ID"),
    Actor(Color.SCARLET, "Actor ID")
}

@Serializable
data class Teleport(val map: Int, val target: Int)
