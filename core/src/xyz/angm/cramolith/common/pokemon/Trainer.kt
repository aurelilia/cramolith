/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 6:17 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import ktx.assets.file
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.yaml

class Trainer(val ident: String, val texture: String, val pokemon: Array<Pokemon>) {

    val name get() = I18N["trainer.$ident"]

    companion object {

        private val trainers = ObjectMap<String, Trainer>()

        init {
            val raw = yaml.decodeFromString(MapSerializer(String.serializer(), Serialized.serializer()), file("trainers.yaml").readString())
            for (move in raw) {
                trainers.put(move.key, move.value.into(move.key))
            }
        }

        fun of(ident: String) = trainers[ident]!!

        @Serializable
        private class Serialized(
            val texture: String, val pokemon: Array<Pokemon>
        ) {
            fun into(ident: String): Trainer {
                return Trainer(ident, texture, pokemon)
            }
        }

    }
}