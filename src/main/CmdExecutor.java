package main;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.Conf;
//import util.Dao;

public class CmdExecutor {
	private static final Logger LOG = LogManager
			.getLogger(CmdExecutor.class);
	Conf cf = null;
	//private Connection conn;
	//private Dao dao;


	public String runPluginCommonExec(String cmdPath,long timeLimit) {
		LOG.info("running=" + cmdPath);
		if (cmdPath == null) {
			LOG.error("cmd is null:" + cmdPath);
			return "cmd is null";
		}
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DefaultExecutor executor = new DefaultExecutor();
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeLimit);
			executor.setWatchdog(watchdog);
			CommandLine cmdLine = CommandLine.parse(cmdPath);
			PumpStreamHandler streamHandler = new PumpStreamHandler(
					outputStream);
			executor.setStreamHandler(streamHandler);
			if (executor.execute(cmdLine) == 0) {
				LOG.debug("succeed");
			} else if (watchdog.killedProcess()) {
				LOG.error("process timeout");
				return "process timeout";
			} else {
				LOG.error("execution error");
				return "execution error";
			}
			String stdOut = outputStream.toString();
			return "DONE<:-:>"+stdOut;
		} catch (IOException e) {
			LOG.error("IOException", e);
			return "EXIT<:-:>"+e.toString();
		} catch (NullPointerException e) {
			LOG.error("NullPointerException", e);
			return "EXIT<:-:>"+e.toString();
		} 
	}
}
