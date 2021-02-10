/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/9/21, 10:51 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.Serializable
import ktx.assets.file
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

    enum class TriggerType(val color: Color) { Teleport(Color.FOREST), TrainerChallenge(Color.SCARLET), Cutscene(Color.ROYAL) }

    companion object {

        private val maps = ObjectMap<String, WorldMap>()

        init {
            for (map in file("map/").list(".yaml")) {
                val map = yaml.decodeFromString(serializer(), map.readString())
                maps.put(map.ident, map)
            }
        }

        fun all() = maps.values()!!

        fun of(ident: String) = maps[ident]!!
    }
}