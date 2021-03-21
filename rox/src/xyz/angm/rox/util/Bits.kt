/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/21/21, 10:37 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.rox.util

import java.io.Serializable
import java.util.*

/** A simple bitset that grows as required automatically.
 * Most of this implementation is very similar to libGDX's Bits. */
class Bits : Serializable {

    private var bits = longArrayOf(0)

    operator fun get(index: Int): Boolean {
        val word = index ushr 6
        return (word < bits.size) && bits[word] and (1L shl (index and 0x3F)) != 0L
    }

    fun set(index: Int) {
        val word = index ushr 6
        checkCapacity(word)
        bits[word] = bits[word] or (1L shl (index and 0x3F))
    }

    private fun checkCapacity(len: Int) {
        if (len >= bits.size) {
            val newBits = LongArray(len + 1)
            System.arraycopy(bits, 0, newBits, 0, bits.size)
            bits = newBits
        }
    }

    fun clear(index: Int) {
        val word = index ushr 6
        if (word >= bits.size) return
        bits[word] = bits[word] and (1L shl (index and 0x3F)).inv()
    }

    fun clear() = Arrays.fill(bits, 0)

    fun containsAll(other: Bits): Boolean {
        val bits = bits
        val otherBits = other.bits
        val otherBitsLength = otherBits.size
        val bitsLength = bits.size
        for (i in bitsLength until otherBitsLength) {
            if (otherBits[i] != 0L) {
                return false
            }
        }
        for (i in bitsLength.coerceAtMost(otherBitsLength) - 1 downTo 0) {
            if (bits[i] and otherBits[i] != otherBits[i]) {
                return false
            }
        }
        return true
    }
}
