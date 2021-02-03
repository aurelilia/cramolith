/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 9:19 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common

/** This file contains constants used by both server and client
 * that would not reasonably fit in any other file in the common package. */


/** World constants */

/** Ticks per second. */
const val TICK_RATE = 10L


/** Networking constants */

/** The port server and client use. */
const val PORT = 25620

/** Maximum size of a netty packet, in bytes. */
const val MAX_NETTY_FRAME_SIZE = 128_000 // 128 KB

/** Size of the receive buffer. */
const val NETTY_BUFFER_SIZE = 8192

/** Size of the length field of sent packets. */
const val LENGTH_SIZE = 4