package edu.hitsz.commons.constants;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public interface Constants {

	/************************* 	界面相关	*************************/
	int FONT_SIZE = 18;
	int BOARD_WIDTH = 440;
	int BOARD_HIGHT = 440;
	int JLABLE_PLAYER_WIDTH = 110;
	int JLABLE_PLAYER_HEIGHT = 25;
	int TEXTFIELD_NAME_WIDTH = 100;
	int TEXTFIELD_NAME_HEIGHT = 25;
	int TEXTAREA_WIDTH = BOARD_WIDTH - JLABLE_PLAYER_WIDTH;
	int TEXTAREA_HEIGHT = JLABLE_PLAYER_HEIGHT;
	int DIMENSION = 10;
	int BUTTONS_LENGTH = BOARD_WIDTH;
	int BUTTON_LENGTH = BUTTONS_LENGTH / DIMENSION;
	int BUTTON_HEIGHT_OFF = JLABLE_PLAYER_HEIGHT * 2;

	int X_OFF = 0;
	int Y_OFF = 50;
	
	/********************   			 系统相关  				  ********************/
	int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();

	String OS_NAME = System.getProperty("os.name");

	String USER_HOME = System.getProperty("user.home") ;

	String LINE_SEPARATOR = System.getProperty("line.separator");

	int SERVER_DEFAULT_LISTEN_PORT = 8000;
	
	/********************    			 通信相关   				 ********************/
	String DUFAULT_MASTER_ADDRESS = "localhost";
	//通信处理器的线程池，默认的线程池大小
	int DEFAULT_PROCESSOR_THREAD = 32 + AVAILABLE_PROCESSOR * 5;
	//同步阻塞的最大时间
	int DEFAULT_INVOKE_TIMEOUT_MILLIS = 20000;
	//默认最大的Buffer大小，Netty解码时需要使用
	int DEFAULT_BUFFER_SIZE = 16 * 1024;
	
	/********************    			 Project相关   				 ********************/
	
	String DEFAULT_PROJECT_ENGINE_PATH = USER_HOME + File.separator
			+ "frobot_master" + File.separator + "lib" + File.separator;
	
	String DEFAULT_PROJECT_ENGINE_PACKAGE_NAME = "org.iceslab.frobot.cluster.master.manager.project.";
	
	String DEFAULT_WORKERSPACE_PATH = USER_HOME + "/frobot_master/workspace/";
	
	int DEFAULT_PROJECT_SCHEDULER_THREAD = 32;
	// 默认集群名字
	String DEFAULT_CLUSTER_NAME = "FRobot-Cluster";

	String CHARSET = "UTF-8";

	int DEFAULT_TIMEOUT = 1000;

	String TIMEOUT_KEY = "timeout";

	String SESSION_TIMEOUT_KEY = "session";

	int DEFAULT_SESSION_TIMEOUT = 60 * 1000;

	String REGISTER = "register";

	String UNREGISTER = "unregister";

	String SUBSCRIBE = "subscribe";

	String UNSUBSCRIBE = "unsubscribe";

}