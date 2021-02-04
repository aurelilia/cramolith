/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.server

import com.badlogic.gdx.utils.Array
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.util.concurrent.GlobalEventExecutor
import xyz.angm.cramolith.common.*
import xyz.angm.cramolith.common.networking.FSTDecoder
import xyz.angm.cramolith.common.networking.FSTEncoder

/** A socket for online communication, using Netty. */
internal class NettyServerSocket(private val server: Server) {

    private val connections = Array<Connection>()
    private var connectionIndex = 0

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val allChannels = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    init {
        ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(NETTY_BUFFER_SIZE))
            .childOption(ChannelOption.SO_RCVBUF, NETTY_BUFFER_SIZE)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(
                        LengthFieldPrepender(LENGTH_SIZE),
                        LengthFieldBasedFrameDecoder(MAX_NETTY_FRAME_SIZE, 0, LENGTH_SIZE, 0, LENGTH_SIZE),
                        FSTEncoder(),
                        FSTDecoder(),
                        ServerHandler(this@NettyServerSocket)
                    )
                }
            })
            .bind(PORT)
            .sync()
    }

    fun send(packet: Any, connection: Connection) {
        connection.channel.writeAndFlush(packet)
    }

    fun sendAll(packet: Any) {
        allChannels.writeAndFlush(packet)
    }

    fun closeConnection(connection: Connection) {
        connection.channel.close()
        server.onDisconnected(connection)
        connections.removeValue(connection, false)
        allChannels.remove(connection.channel)
    }

    fun close() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    private fun addNewChannel(ctx: ChannelHandlerContext): Connection {
        val connection = Connection(ctx.channel(), ++connectionIndex)
        connections.add(connection)
        server.onConnected(connection)
        allChannels.add(connection.channel)
        return connection
    }

    internal class ServerHandler(private val socket: NettyServerSocket) : ChannelInboundHandlerAdapter() {

        private lateinit var connection: Connection

        override fun channelActive(ctx: ChannelHandlerContext) {
            connection = socket.addNewChannel(ctx)
        }

        override fun channelInactive(ctx: ChannelHandlerContext?) {
            socket.closeConnection(connection)
        }

        override fun channelRead(ctx: ChannelHandlerContext, input: Any) = socket.server.received(connection, input)

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            log.info(cause) { "Exception during client communication:" }
            log.info { "Ignoring packet." }
        }
    }
}

/** A wrapper for a connection.
 * @property ip The IP of the client connected
 * @property id The id of the client. IDs are unique and count up from 0. */
internal class Connection(internal val channel: Channel, val id: Int) {
    val ip get() = channel.remoteAddress().toString()
}