/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 7:23 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.resources

import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.ObjectIntMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.file
import ktx.assets.toLocalFile
import ktx.collections.*
import xyz.angm.cramolith.client.actions.PlayerAction
import xyz.angm.cramolith.client.actions.PlayerActions
import xyz.angm.cramolith.common.yaml
import kotlin.collections.set

val configuration = {
    val file = file("configuration.yaml")
    val conf = if (file.exists()) yaml.decodeFromString(Configuration.serializer(), file.readString())
    else Configuration()
    conf.init()
    conf
}()

/** Handles all things that the player can configure.
 * @property language The language to use for [I18N].
 * @property keybinds All keybinds. */
@Serializable
class Configuration {

    // All these are initialized by deserialization
    val keybinds = Keybinds()

    var language = "English"

    /** Should be called after deserialization to allow the object to correct its state. */
    fun init() = keybinds.init()

    /** Saves current state to disk. */
    fun save() = "configuration.yaml".toLocalFile().writeString(yaml.encodeToString(serializer(), this), false)

    /** A simple class containing all keybinds. */
    @Serializable
    class Keybinds(
        @Transient private val binds: IntMap<PlayerAction> = IntMap(),
        @Transient private val bindsRev: ObjectIntMap<String> = ObjectIntMap()
    ) {

        // Needed for persisting data via Json.serialization
        private val bindings = HashMap<String, String>()

        // Needed to set state from deserialized data
        internal fun init() {
            bindings.forEach { (key, actionName) ->
                val keyInt = Input.Keys.valueOf(key)
                val action = PlayerActions[actionName]!!
                binds[keyInt] = action
                bindsRev.put(actionName, keyInt)
            }
        }

        /** Get the bound action, or null. */
        operator fun get(key: Int): PlayerAction? = binds[key]

        /** Get the key bound to, or -1. */
        operator fun get(action: String): Int? = bindsRev[action, -1]

        /** Iterates over all registered keys. */
        fun forEach(func: (PlayerAction) -> Unit) = binds.forEach { func(it.value) }

        /** Returns all binds as a sorted list of pairs.
         * The weird array gymnastics are needed since IntMap.Entry is static and not reusable. */
        fun getAllSorted(): List<Pair<Int, PlayerAction>> {
            val array = Array<Pair<Int, PlayerAction>>(PlayerActions.actions.size)
            PlayerActions.actions.entries().forEach {
                val bind = bindsRev[it.key, 0]
                array.add(Pair(bind, it.value))
            }
            return array.sortedBy { I18N["keybind.${it.second.type}"] }
        }

        /** Register a keybind.
         * @param key The key to bind to. Also see [com.badlogic.gdx.Input.Keys]
         * @param action The type of action to bind to the key */
        fun registerKeybind(key: Int, action: String) {
            binds[key] = PlayerActions[action]!!
            bindsRev.put(action, key)
            bindings[Input.Keys.toString(key)] = action
        }

        /** Unregister a keybind.
         * @param key The key to unbind. */
        fun unregisterKeybind(key: Int) {
            if (!binds.containsKey(key)) return
            bindsRev.remove(this[key]?.type, -1)
            binds.remove(key)
            bindings.remove(Input.Keys.toString(key))
        }
    }
}