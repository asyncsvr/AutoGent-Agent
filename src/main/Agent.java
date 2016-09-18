package main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.Conf;
import util.ServerHandler;



public class Agent {
	private static final Logger LOG = LogManager.getLogger(Agent.class);
	public static String VERSION = "0.0";

	private int port;
	 
    public Agent(int port) {
        this.port = port;
    }
 
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new ServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
 
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
 
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

	public void init(Conf cf, String[] args) {
		if (args.length < 1) {
			LOG.error("please input config.xml");
			System.exit(0);
		}
		cf.setConfFile(args[0]);
	}

	public static void main(String[] args) {
		Conf cf = new Conf();
		cf.setConfFile(args[0]);
		Agent agent = new Agent(cf.getSinglefValue("agent_port"));
		agent.init(cf, args);
			
		String host;
		
		try {
			host = InetAddress.getLocalHost().getHostName();
			if(host.matches("localhost.localdomain")){//TEST
				host = "192.168.15.133";
			}
			agent.run();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	} 
		
}