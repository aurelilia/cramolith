/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.resources

import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.ObjectIntMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.file
import ktx.assets.toLocalFile
import ktx.collections.*
import xyz.angm.pokemmo.client.actions.PlayerAction
import xyz.angm.pokemmo.client.actions.PlayerActions
import xyz.angm.pokemmo.common.yaml
import kotlin.collections.set
import kotlin.math.pow

val configuration = {
    val file = file("configuration.yaml")
    val conf = if (file.exists()) yaml.decodeFromString(Configuration.serializer(), file.readString())
    else Configuration()
    conf.init()
    conf
}()

/** Handles all things that the player can configure.
 * @property playerName The player's name.
 * @property clientUUID The UUID of this client, used to identify with servers.
 * @property language The language to use for [I18N].
 *
 * @property keybinds All keybinds. */
@Serializable
class Configuration {

    // All these are initialized by deserialization
    val keybinds = Keybinds()

    val clientUUID = System.nanoTime().toInt()
    var playerName = "player #$clientUUID"
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
        fun forEach(func: (Int) -> Unit) = binds.forEach { func(it.key) }

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

    /** Video options controllable by the user.
     * @property blend If blending on blocks should be enabled.
     * @property shadowQuality Shadow resolution multiplier.
     * @property shadowFBO Size of the shadow framebuffer, not player-configured. */
    @Serializable
    class VideoOptions {
        var blend = true
        var shadowQuality = 2
        val shadowFBO get() = (2f.pow(shadowQuality).toInt() * 2048)
    }
}