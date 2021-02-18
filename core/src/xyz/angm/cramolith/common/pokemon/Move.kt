/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 3:58 PM.
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

@Serializable
class Move(
    val ident: String,
    val damage: Int,
    val accuracy: Int,
    val type: Type,
    val priority: Int = 1
) {

    val name get() = i18nMoveName(ident)

    companion object {

        private val moves = ObjectMap<String, Move>()

        init {
            val raw = yaml.decodeFromString(MapSerializer(String.serializer(), Serialized.serializer()), file("moves.yaml").readString())
            for (move in raw) {
                moves.put(move.key, move.value.into(move.key))
            }
        }

        fun of(ident: String) = moves[ident]!!

        fun i18nMoveName(ident: String) = I18N.tryGet("move.$ident") ?: ident.capitalize()

        @Serializable
        private class Serialized(
            val damage: Int,
            val accuracy: Int,
            val type: Type
        ) {
            fun into(ident: String): Move {
                return Move(ident, damage, accuracy, type)
            }
        }
    }
}