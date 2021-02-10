/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:06 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.Serializable
import xyz.angm.cramolith.client.resources.ResourceManager

@Serializable
class WorldActor(val texture: String, val index: Int, val script: MutableList<String>) {

    val drawable get() = ResourceManager.get<Texture>("sprites/actors/$texture.png")

    fun tryLoad(): Boolean {
        return try {
            ResourceManager.loadTexture("sprites/actors/${texture}.png")
            true
        } catch (e: Exception) {
            false
        }
    }
}