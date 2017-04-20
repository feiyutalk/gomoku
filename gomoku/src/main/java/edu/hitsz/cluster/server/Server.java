package edu.hitsz.cluster.server;

import edu.hitsz.cluster.server.manager.GameManager;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.cluster.server.manager.UserManager;
import edu.hitsz.cluster.server.processor.ServerRemotingDispacher;
import edu.hitsz.commons.constants.Constants;
import edu.hitsz.commons.utils.Parser;
import edu.hitsz.remoting.RemotingServerConfig;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.MatchOpponentRequestBody;
import edu.hitsz.remoting.command.body.request.PushUserInfoRequestBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.delegate.RemotingServerDelegate;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Neuclil on 17-4-9.
 */
public class Server {
    private static final Logger LOG = Logger.getLogger(Server.class);
    private ServerConfig config;
    private RemotingServerDelegate remotingServer;
    private ServerApplication application;
    private Timer timer;
    private AtomicBoolean inited = new AtomicBoolean(false);
    private AtomicBoolean remotingStarted = new AtomicBoolean(false);

    public Server() {
        this.application = new ServerApplication();
        timer = new Timer();
    }

    public void start() {
        init();
        startRemoting();
        startTimer();
    }

    private void startTimer() {
        LOG.debug("启动定时器!");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<UserInfo> waitUsers =
                        application.getUserManager().getWaitUsers();
                if (waitUsers != null) {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           RemotingCommand request = RemotingCommand.createRequestCommand(
                                   RemotingProtos.RequestCode.PUSH_WAIT_USERINFO.code(),
                                   new PushUserInfoRequestBody(waitUsers));
                           List<UserInfo> all = application.getUserManager().getAll();
                           for(UserInfo userInfo : all){
                               application.getRemotingServer().invokeOneway(
                                       userInfo.getChannel(),
                                       request);
                           }
                       }
                   }).start();
                }
            }
        }, 1 * 1000, 3 * 1000);
    }

    private void startRemoting() {
        try {
            if (remotingStarted.compareAndSet(false, true)) {
                LOG.debug("正在开启通信服务....");
                remotingServer.start();
                RemotingProcessor defaultProcessor = getDefaultProcessor();
                if (defaultProcessor != null) {
                    remotingServer.registerDefaultProcessor(defaultProcessor,
                            Executors.newFixedThreadPool(Constants.DEFAULT_PROCESSOR_THREAD));
                }
                LOG.debug("通信服务开启成功!");
            }
        } catch (Exception e) {
            LOG.error("Server通信服务开启失败!", e);
        }
    }

    private RemotingProcessor getDefaultProcessor() {
        return new ServerRemotingDispacher(application);
    }

    private void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOG.debug("Server开始初始化....");
                initConfig();

                initRemoting();

                initApplication();
            }
        } catch (Exception e) {
            LOG.error("Server初始化失败!");
        }
    }

    private void initApplication() {
        this.application.setServerConfig(config);
        this.application.setRemotingServer(remotingServer);
        this.application.setGameManager(GameManager.getInstance(application));
        this.application.setUserManager(UserManager.getInstance());
    }

    private void initRemoting() {
        RemotingServerConfig remotingServerConfig = new RemotingServerConfig();
        if (config.getPort() == 0) {
            config.setPort(Constants.SERVER_DEFAULT_LISTEN_PORT);
        }
        remotingServerConfig.setListenPort(config.getPort());
        this.remotingServer = new RemotingServerDelegate(remotingServerConfig,
                new ServerChannelEventListener(application));
        LOG.debug("Server通信服务完成!");
    }

    private void initConfig() {
        Map<String, String> info =
                Parser.parserServerConfig("/home/gomoku/serverconfig.xml");
        config = new ServerConfig.Builder()
                .ip(info.get("ip"))
                .port(Integer.valueOf(info.get("port")))
                .matches(Integer.valueOf(info.get("matches")))
                .build();
        LOG.debug("Server初始化配置文件完成!" + config);
    }
}
