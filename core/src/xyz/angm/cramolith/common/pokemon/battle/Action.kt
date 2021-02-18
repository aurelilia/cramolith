/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 3:10 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.pokemon.battle

import java.io.Serializable

/** An action a player can perform on a turn inside of a battle. */
sealed class QueuedAction : Serializable
class QueuedMove(val moveIdx: Int = 0) : QueuedAction()
class QueuedSwitch(val pokemonIdx: Int = 0) : QueuedAction()
