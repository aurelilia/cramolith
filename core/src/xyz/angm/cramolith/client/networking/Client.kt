/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/21/21, 4:39 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.networking

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import ktx.collections.*
import xyz.angm.cramolith.common.log
import xyz.angm.cramolith.common.runLogE

/** A client used for sending and receiving packages from a server.
 * This client implementation uses a coroutine to process
 * incoming packets, and can be 'locked' to prevent it from processing
 * anything; this is used to prevent data races with the main/render thread.
 * @property disconnectListener Called when the client is disconnected. */
class Client {

    private var socket = NettyClientSocket(this)
    private val listeners = GdxArray<(Any) -> Unit>(false, 10)
    var disconnectListener: () -> Unit = {}

    // If the client is locked and forbidden to process packets to prevent race conditions
    private var locked = false

    // Queued packets to process once the client is unlocked again
    private val queued = GdxArray<Any>()

    // The channel that is used by the processing coroutine
    private val packetChannel = Channel<Any>()

    // If a packet is currently processing, will spinlock if so and lock() is called
    private var processing = false
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        addListener { packet -> log.debug { "[CLIENT] Received packet of class ${packet.javaClass.name}" } }
        scope.launch { // Processing / worker coroutine
            while (true) processPacket(packetChannel.receive())
        }
        socket.connect("lgs-cramolith.angm.xyz") // TODO: replace with actual server ip when moving to prod
    }

    /** Add a listener for received packets */
    fun addListener(listener: (Any) -> Unit) {
        listeners.add(listener)
    }

    /** Send the specified packet to server. */
    fun send(packet: Any) {
        socket.send(packet)
        log.debug { "[CLIENT] Sent ${packet.javaClass.simpleName}" }
    }

    internal fun receive(packet: Any) {
        if (locked) queued.add(packet)
        else scope.launch { packetChannel.send(packet) }
    }

    /** Should only be called from the processing coroutine. */
    private fun processPacket(packet: Any) {
        processing = true
        log.debug { "[CLIENT] Processed received ${packet.javaClass.simpleName}" }
        runLogE("Client", "processing packet") { listeners.forEach { it(packet) } }
        processing = false
    }

    /** Locks this client until [Client.unlock] is called, preventing
     * it from doing any processing on incoming packets.
     * Used to prevent race conditions with the main game thread concurrently
     * accessing not-thread-safe data.
     * Packets received while locked will be processed in unlock(). */
    fun lock() {
        locked = true
        // Spin until processing finished if still active
        while (processing) Thread.sleep(0, 50000) // 0.05ms
    }

    /** Unlocks this client again, processing all packets that arrived in the meantime.
     * Will cause client to immediately process incoming packets again. */
    fun unlock() {
        locked = false
        // Kick this off on a coroutine to prevent locking main thread
        scope.launch {
            while (!locked && !queued.isEmpty) {
                packetChannel.send(queued.pop() ?: continue) // this is null sometimes??
            }
        }
    }

    internal fun disconnected() = disconnectListener()

    /** Dispose of the client. Object is unusable after this. */
    fun close() {
        socket.close()
        scope.cancel()
    }
}
