package com.byyw.demo;

import com.byyw.demo.NettyTcpServer.TcpResponseEvent;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyTcpClient {

    public static void main(String[] args) {
        Bootstrap client = new Bootstrap();
        client.group(new NioEventLoopGroup(1))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DefaultEventExecutorGroup(20),
                                        new SimpleChannelInboundHandler<ByteBuf>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf)
                                                    throws Exception {
                                                TcpResponseEvent e = new TcpResponseEvent(
                                                        ctx.channel().id().asLongText(),
                                                        ByteBufUtil.hexDump(buf));
                                                System.out.println(e.getMsg());
                                            }

                                            @Override
                                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                                log.info("{} is registered.", ctx.channel().id().asLongText());
                                            }

                                            @Override
                                            public void channelUnregistered(ChannelHandlerContext ctx)
                                                    throws Exception {
                                                log.info("{} is unregistered.", ctx.channel().id().asLongText());
                                            }
                                        });
                    }
                });
        ChannelFuture cf = client.connect("127.0.0.1", 10111);
        int i = 0;
        while (cf.isSuccess()) {
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
            buf.writeBytes(("" + i).getBytes());
            buf.writeByte(0x00);
            try {
                cf.channel().writeAndFlush(buf).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
