/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.resources

import com.badlogic.gdx.math.Vector2
import xyz.angm.cramolith.common.ecs.components.VectoredComponent

/** Interface for playing sound. */
interface ISound {

    /** Initialize and get ready for playing sounds. */
    fun init()

    /** Play a sound to the player. */
    fun playSound(sound: String)

    /** Play a 2D sound at the specified location. Coordinate system is the world. */
    fun playSound2D(sound: String, location: Vector2)

    /** Same as [playSound2D] but loops the source until [stopPlaying] is called. */
    fun playLooping(sound: String, location: Vector2, volume: Float = 1f): Int

    /** Interrupts the given source. Source ID is obtained from [playLooping]. */
    fun stopPlaying(source: Int)

    /** Updates the position and direction of the listener for 2D sound, which is usually the player. */
    fun updateListenerPosition(position: VectoredComponent)
}

/** A dummy sound implementation that deliberately does nothing.
 * Used by default when the launcher didn't specify an interface. */
private object DummySound : ISound {
    override fun init() {}
    override fun playSound(sound: String) {}
    override fun playSound2D(sound: String, location: Vector2) {}
    override fun playLooping(sound: String, location: Vector2, volume: Float) = 0
    override fun stopPlaying(source: Int) {}
    override fun updateListenerPosition(position: VectoredComponent) {}
}

/** An interface for playing sound effects.
 * Defaults to dummy that does nothing, can only be set to proper interface once.
 * (The dummy is used instead of a 'lateinit' variable to prevent crashes in cases
 * where no sound is the intended behavior, like the server or during unit tests) */
var soundPlayer: ISound = DummySound