/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:25 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.networking

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import xyz.angm.cramolith.common.*
import xyz.angm.cramolith.common.networking.FSTDecoder
import xyz.angm.cramolith.common.networking.FSTEncoder

/** Client socket. Uses Netty. */
class NettyClientSocket(private val client: Client) {

    private val workerGroup = NioEventLoopGroup()
    private lateinit var future: ChannelFuture

    fun connect(ip: String) {
        future = Bootstrap()
            .group(workerGroup)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(NETTY_BUFFER_SIZE))
            .option(ChannelOption.SO_RCVBUF, NETTY_BUFFER_SIZE)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(
                        LengthFieldPrepender(LENGTH_SIZE),
                        LengthFieldBasedFrameDecoder(MAX_NETTY_FRAME_SIZE, 0, LENGTH_SIZE, 0, LENGTH_SIZE),
                        FSTEncoder(),
                        FSTDecoder(),
                        ClientHandler(client)
                    )
                }
            })
            .connect(ip, PORT)
            .sync()
    }

    fun send(packet: Any) {
        future.channel().writeAndFlush(packet)
    }

    fun close() {
        workerGroup.shutdownGracefully()
    }

    private class ClientHandler(private val client: Client) : ChannelInboundHandlerAdapter() {

        lateinit var channel: Channel

        override fun channelActive(ctx: ChannelHandlerContext) {
            channel = ctx.channel()
            channel.closeFuture().addListener { client.disconnected() }
        }

        override fun channelRead(ctx: ChannelHandlerContext, input: Any) = client.receive(input)

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            log.info(cause) { "Exception during server communication:" }
            log.info { "Ignoring packet." }
        }
    }
}