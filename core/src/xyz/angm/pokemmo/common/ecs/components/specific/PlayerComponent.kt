/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 9:11 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs.components.specific

import xyz.angm.rox.Component

/** Component for all player state. Not all is persisted (see server-side DB)
 * @property name The (display)name of the player.
 * @property clientUUID The UUID of the client the player is from. */
class PlayerComponent : Component {

    lateinit var name: String
    var clientUUID: Int = 0
}
