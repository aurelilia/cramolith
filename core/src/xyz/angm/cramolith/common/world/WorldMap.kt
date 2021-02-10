/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 2:50 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.Serializable
import ktx.assets.file
import ktx.collections.*
import xyz.angm.cramolith.client.resources.ResourceManager
import xyz.angm.cramolith.common.yaml

@Serializable
class WorldMap(
    val ident: String,
    val triggers: MutableList<Trigger>
) {

    val texture get() = ResourceManager.get<Texture>("map/$ident.png")

    @Serializable
    data class Trigger(val type: TriggerType, val x: Int, val y: Int, val width: Int, val height: Int, val idx: Int)

    enum class TriggerType(val color: Color, val indexSays: String?) {
        Collision(Color.DARK_GRAY, null),
        Teleport(Color.FOREST, "Map ID"),
        TrainerChallenge(Color.SCARLET, "Trainer ID"),
        Cutscene(Color.ROYAL, "Cutscene ID")
    }

    companion object {

        private val maps = ObjectMap<String, WorldMap>()

        init {
            for (map in file("map/").list(".yaml")) {
                val map = yaml.decodeFromString(serializer(), map.readString())
                maps[map.ident] = map
            }
        }

        fun all() = maps.values()!!

        fun of(ident: String) = maps[ident]!!
        fun maybeOf(ident: String): WorldMap? = maps[ident]

        /** Create a new map. Will automatically load texture of the map,
         * if it does not exist then `false` is returned and the map is not added
         * to the list of maps. Its definition file will still be created however. */
        fun new(ident: String): Boolean {
            val map = WorldMap(ident, ArrayList())
            Gdx.files.local("map/$ident.yaml").writeString(yaml.encodeToString(serializer(), map), false)

            return try {
                ResourceManager.loadTexture("map/$ident.png")
                maps[ident] = map
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}