/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:22 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.file
import ktx.collections.*
import xyz.angm.cramolith.client.resources.ResourceManager
import xyz.angm.cramolith.common.yaml

@Serializable
class WorldMap(
    val ident: String,
    val index: Int,
    val triggers: MutableList<Trigger> = ArrayList(),
    val teleports: MutableList<Teleport> = ArrayList(),
    val actors: MutableMap<String, WorldActor> = HashMap()
) {

    val texture get() = ResourceManager.get<Texture>("map/$ident.png")

    @Transient
    val actorsId = IntMap<WorldActor>()

    init {
        for (actor in actors.values) {
            actorsId[actor.index] = actor
        }
    }

    fun newActor(name: String, texture: String): Boolean {
        val actor = WorldActor(texture, actorsId.size)
        if (!actor.tryLoad()) return false
        actors[name] = actor
        actorsId[actorsId.size] = actor
        return true
    }

    companion object {

        private val maps = ObjectMap<String, WorldMap>()
        private val mapsId = IntMap<WorldMap>()

        init {
            for (map in file("map/").list(".yaml")) {
                val map = yaml.decodeFromString(serializer(), map.readString())
                maps[map.ident] = map
                mapsId[map.index] = map
            }
        }

        fun size() = maps.size

        fun all() = maps.values()!!

        fun of(ident: String) = maps[ident]!!
        fun maybeOf(ident: String): WorldMap? = maps[ident]
        fun of(index: Int) = mapsId[index]!!

        /** Create a new map. Will automatically load texture of the map,
         * if it does not exist then `false` is returned and the map is not added
         * to the list of maps. Its definition file will still be created however. */
        fun new(ident: String): Boolean {
            val map = WorldMap(ident, maps.size)
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