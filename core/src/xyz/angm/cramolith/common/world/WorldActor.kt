/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:42 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.Serializable
import xyz.angm.cramolith.client.resources.ResourceManager

/** An actor in the game world, able to be interacted with by players. Interaction
 * the actor's script to be executed on the client.
 * Most actors are either NPCs or empty actors used as holder for a cutscene script, activated with triggers. */
@Serializable
class WorldActor(
    val texture: String,
    val index: Int,
    var x: Int = 0,
    var y: Int = 0,
    val script: MutableList<String> = ArrayList()
) {

    val drawable get() = ResourceManager.get<Texture>("sprites/actors/$texture.png")

    /** Used by editor when creating a new actor. */
    fun tryLoad(): Boolean {
        return try {
            ResourceManager.loadTexture("sprites/actors/${texture}.png")
            true
        } catch (e: Exception) {
            false
        }
    }
}