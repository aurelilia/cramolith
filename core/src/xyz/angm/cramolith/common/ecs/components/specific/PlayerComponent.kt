/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/6/21, 11:53 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs.components.specific

import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.rox.Component

/** Component for all player state. Not all is persisted (see server-side DB)
 * @property name The (display)name of the player.
 * @property clientUUID The UUID of the client the player is from.
 * @property pokemon All pokemon the player owns. */
class PlayerComponent : Component {

    lateinit var name: String
    var clientUUID = 0
    val pokemon = ArrayList<Pokemon>()
}
