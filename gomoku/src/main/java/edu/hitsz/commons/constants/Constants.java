package edu.hitsz.commons.constants;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public interface Constants {
	
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
	
    //启动方式: 0为立即启动，1为手动启动，2为定时启动
	int PROJECTSTARTATONCE = 0;
	
	int PROJECTSTARTBYUSER = 1;
	
	int PROJECTSTARTDELAY = 2;

	/**
	 * 注册中心失败事件重试事件
	 */
	String REGISTRY_RETRY_PERIOD_KEY = "retry.period";

	/**
	 * 重试周期
	 */
	int DEFAULT_REGISTRY_RETRY_PERIOD = 5 * 1000;

	Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

	/**
	 * 注册中心自动重连时间
	 */
	String REGISTRY_RECONNECT_PERIOD_KEY = "reconnect.period";

	int DEFAULT_REGISTRY_RECONNECT_PERIOD = 3 * 1000;

	// 客户端提交并发请求size
	String JOB_SUBMIT_CONCURRENCY_SIZE = "job.submit.concurrency.size";
	int DEFAULT_JOB_SUBMIT_CONCURRENCY_SIZE = 100;

	String PROCESSOR_THREAD = "job.processor.thread";

	int LATCH_TIMEOUT_MILLIS = 10 * 60 * 1000; // 10分钟

	// 任务最多重试次数
	String JOB_MAX_RETRY_TIMES = "job.max.retry.times";
	int DEFAULT_JOB_MAX_RETRY_TIMES = 10;

	Charset UTF_8 = Charset.forName("UTF-8");

	String MONITOR_DATA_ADD_URL = "/api/monitor/monitor-data-add.do";

	String MONITOR_JVM_INFO_DATA_ADD_URL = "/api/monitor/jvm-info-data-add.do";

	String MONITOR_COMMAND_INFO_ADD_URL = "/api/monitor/command-info-add.do";

	String JOB_PULL_FREQUENCY = "job.pull.frequency";
	int DEFAULT_JOB_PULL_FREQUENCY = 30;

	// TaskTracker 离线(网络隔离)时间 2 分钟，超过两分钟，自动停止当前执行任务
	long TASK_TRACKER_OFFLINE_LIMIT_MILLIS = 2 * 60 * 1000;
	// TaskTracker超过一定时间断线JobTracker，自动停止当前的所有任务
	String TASK_TRACKER_STOP_WORKING_SWITCH = "stop.working";

	String ADMIN_ID_PREFIX = "LTS_admin_";

	// 是否延迟批量刷盘日志, 如果启用，采用队列的方式批量将日志刷盘(在应用关闭的时候，可能会造成日志丢失)
	String LAZY_JOB_LOGGER = "lazy.job.logger";
	// 延迟批量刷盘日志 内存中的最大日志量阀值
	String LAZY_JOB_LOGGER_MEM_SIZE = "lazy.job.logger.mem.size";
	// 延迟批量刷盘日志 检查频率
	String LAZY_JOB_LOGGER_CHECK_PERIOD = "lazy.job.logger.check.period";

	String ADAPTIVE = "adaptive";


}