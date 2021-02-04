/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 4:47 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.networking

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import xyz.angm.cramolith.common.MAX_NETTY_FRAME_SIZE
import xyz.angm.cramolith.common.fst
import xyz.angm.cramolith.common.log

/** An encoder for the Netty pipeline that turns any object sent into a byte array using FST. */
class FSTEncoder : MessageToByteEncoder<Any>() {

    private val len = IntArray(1)

    /** Encodes using FST. */
    override fun encode(ctx: ChannelHandlerContext, toWrite: Any, out: ByteBuf) {
        val arr = fst.asSharedByteArray(toWrite, len)
        out.writeBytes(arr, 0, len[0])
    }
}

/** An encoder for the Netty pipeline that turns any bytes received into an object using FST. */
class FSTDecoder : ByteToMessageDecoder() {

    private var buf = ByteArray(MAX_NETTY_FRAME_SIZE)

    /** Decodes using FST. */
    override fun decode(ctx: ChannelHandlerContext?, input: ByteBuf, out: MutableList<Any>) {
        try {
            input.readBytes(buf, 0, input.readableBytes())
            out.add(fst.asObject(buf))
        } catch (e: Exception) {
            log.warn { "FST encountered error. Trying to recover..." }
            buf = ByteArray(MAX_NETTY_FRAME_SIZE)
        }
    }
}