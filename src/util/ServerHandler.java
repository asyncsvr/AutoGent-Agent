package util;

import java.net.InetAddress;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import main.CmdExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOG = LogManager.getLogger(ServerHandler.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
		ByteBuf byteBufMessage = (ByteBuf) msg;
		int size = byteBufMessage.readableBytes();
		byte [] byteMessage = new byte[size];
		for(int i = 0 ; i < size; i++){
			byteMessage[i] = byteBufMessage.getByte(i);
		}
		String str = new String(byteMessage);
		LOG.info("rev="+str);
		ctx.write(msg);
		String[] items =str.split("::");
		try{
			//String sendMsg = revAuthCode+"::"+resRcsvr+"::"+revMode+"::"+revCmdType+"::"+cmd;
			String recvAuthCode=items[0];
			String recvReceiver=items[1];
			String recvMode=items[2];//norm/lock
			String recvCmdType=items[3];//run
			String recvOpID=items[4];
			long timeLimt=Long.parseLong(items[5])*1000;
			String recvCmd=items[6];
			//String revOpID2=items[4];//run/kill

			//String sendMsg = revAuthCode+"::"+revMode+"::"+revCmdType+"::"+cmd;
		
			int tryCnt=0;
			Conf cf = new Conf();
			cf.setConfFile("AutoGent.conf");
			
			if (timeLimt<=0){
				timeLimt=Long.MAX_VALUE;
			}else{
				LOG.info("timeLimit="+timeLimt);
			}
			CmdExecutor ce=new CmdExecutor();

			//TODO // AutoFailed code
			//if recvAuthCode.match
			
			//kfu.sendMessage(recvReceiver, "AutoGent"+recvOpID+"_"+hostName+"_"+status, "run");
			String result=ce.runPluginCommonExec(recvCmd,timeLimt );
			//kfu.sendMessage(recvReceiver, "AutoGent"+recvOpID+"_"+hostName+"_"+status, "done");			
			LOG.info("result="+result);
			//TODO: SEND KAFKA SET JOBID-AGENT::RUN
			MassageUtil kfu = new MassageUtil();
			String hostName=InetAddress.getLocalHost().getHostName();

			kfu.sendMessage(recvReceiver, "AutoGentT1", recvOpID+"::"+hostName+"::"+result);
			
			
			
			//TODO: SEND KAFKA SET JOBID-AGENT::DONE
			
			
		}catch(ArrayIndexOutOfBoundsException e){
			LOG.info("Array Out");
		}
		

        //TODO forward cmd from Broker to Client
        //TODO make set of operation and send msg to broker's agent
        //send ?????                //password+resSvr+Mode(lock,force,norm)+cmd		
		
	}
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	};
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}