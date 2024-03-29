package com.byyw.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;

/**
 * @Author: Zpsw
 * @Date: 2019-05-15
 * @Description:
 * @Version: 1.0
 */

@Slf4j
@Component
public class NettyTcpServer {
    @Autowired
    private AsyncEventBus asyncEventBus;

    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);
    private EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(20);

    private Map<String, Channel> channelMap = new HashMap<>();

    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture = null;
    private boolean run = false;

    private Integer port = 10111;

    @PostConstruct
    public void construct() {
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 4096) // 服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(4200,
                                        Unpooled.copiedBuffer(new byte[] { 0x00 })))
                                .addLast(businessGroup, new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf)
                                            throws Exception {
                                        TcpResponseEvent e = new TcpResponseEvent(ctx.channel().id().asLongText(),
                                                ByteBufUtil.hexDump(buf));
                                        log.info("<<< " + ctx.channel().id().asLongText() + " : " + e.getMsg());
                                        if (e.getMsg().equals("383636323937303336383637303632")) {
                                            return;
                                        }
                                        asyncEventBus.post(e);
                                    }

                                    @Override
                                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                        channelMap.put(ctx.channel().id().asLongText(), ctx.channel());
                                        log.info("{} is registered.", ctx.channel().id().asLongText());
                                    }

                                    @Override
                                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                        channelMap.remove(ctx.channel().id().asLongText());
                                        log.info("{} is unregistered.", ctx.channel().id().asLongText());
                                    }
                                });
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true)// 立即写出
                .childOption(ChannelOption.SO_KEEPALIVE, true);// 长连接
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);// 内存泄漏检测 开发推荐PARANOID 线上SIMPLE
        start();
    }

    public void send(String id, byte[] bs) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(bs);
        channelMap.get(id).writeAndFlush(buf);
    }

    public void start() {
        if (this.channelFuture == null) {
            try {
                this.channelFuture = this.serverBootstrap.bind(port).sync();
                if (this.channelFuture.isSuccess()) {
                    log.info("netty_tcp服务启动,port={}", port);
                    this.run = true;
                }
            } catch (NumberFormatException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (this.channelFuture != null) {
            this.channelFuture.channel().write("").addListener(ChannelFutureListener.CLOSE);
            this.channelFuture = null;
            log.info("netty_tcp服务停止");
            this.run = false;
        }
    }

    /**
     * 销毁资源
     */
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        businessGroup.shutdownGracefully().syncUninterruptibly();
        log.info("netty_tcp服务终止");
    }

    @Data
    public static class TcpResponseEvent {
        private String id;
        private String msg;

        public TcpResponseEvent() {
        }

        public TcpResponseEvent(String id, String msg) {
            this.id = id;
            this.msg = msg;
        }
    }
}
