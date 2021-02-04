/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 5:47 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon

import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import ktx.assets.file
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.yaml

@Serializable
class Move(
    val ident: String,
    val damage: Int,
    val accuracy: Int,
    val type: Type
) {

    var name = I18N.tryGet(ident) ?: ident.capitalize()

    companion object {

        private val moves = ObjectMap<String, Move>()

        init {
            val raw = yaml.decodeFromString(ListSerializer(serializer()), file("moves.yaml").toString())
            for (move in raw) {
                moves.put(move.ident, move)
            }
        }

        fun of(ident: String) = moves[ident]!!
    }
}