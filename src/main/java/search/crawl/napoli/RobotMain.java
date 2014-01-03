package search.crawl.napoli;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.crawler.Crawler;
import search.crawl.napoli.util.ATPCommons;
import search.crawl.napoli.util.Commons;
import search.crawl.napoli.util.HashFunction;

import org.apache.commons.lang.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

class RobotMain implements SignalHandler {
	
	static final Logger LOGGER = LoggerFactory.getLogger(RobotMain.class);
	
	Crawler crawler;
	boolean runFlag;
	
	private Map<Signal, SignalHandler> handlers;
	
	RobotMain(Crawler crawler) {
		this.crawler = crawler;
		runFlag = true;
		handlers = new HashMap<Signal, SignalHandler>();
		
		Signal signal = new Signal("TERM");
		SignalHandler handler = Signal.handle(signal, this);
		handlers.put(signal, handler);
	}
	
	public void handle(Signal signal) {
		if(signal.getName() == "TERM") {
			LOGGER.info("시그널 받음");
			this.runFlag = false;
		}
	}
	
	boolean startRobot() {
		if (!crawler.readConfig()) {
			LOGGER.error("Config reading failed");
			return false;
		}
		
		if (!crawler.dbInit()) {
			LOGGER.error("DB init failed");
			return false;
		}
		
		return true;
	}
	
	void run() {
		LOGGER.info("=====================================================");
		LOGGER.info("robot is running");
		LOGGER.info("=====================================================");
		
		while(true) {
			this.crawler.work();
			
			if (!this.runFlag) {
				break;
			} else {
				LOGGER.info("running...");
				Commons.sleep(2000);
			}
			
		}
		
		LOGGER.info("=====================================================");
		LOGGER.info("robot is stopped");
		LOGGER.info("=====================================================");
	}
	
	public static void main(String[] args) throws Exception {
		String localPackageName = "search.crawl.napoli.crawler";
		String className = args[0];
		String runClassName = localPackageName + "." + className;
		
		Class<?> runClass;
		try {
			runClass = Class.forName(runClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Not Found Class : your first parameter is " + runClassName);
			LOGGER.error(e.getMessage());
			return;
		}
		
		Object obj;
		try {
			obj = runClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Instance Create Failed");
			LOGGER.error(e.getMessage());
			return;
		}
		
		RobotMain main = new RobotMain((Crawler)obj);
		if(!main.startRobot()) {
			LOGGER.error("Preparing failed");
			return;
		}
		
		main.run();
	}
}