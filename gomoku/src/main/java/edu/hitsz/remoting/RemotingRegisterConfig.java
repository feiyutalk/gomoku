package edu.hitsz.remoting;

import edu.hitsz.commons.constants.Constants;

public class RemotingRegisterConfig {
    /* Netty端口 */
    private int nettyPort = 9000;
    /* Socket端口*/
    private int socketPort = 2181;
    /* Selector线程数量*/
    private int registerSelectorThreads = Constants.AVAILABLE_PROCESSOR * 2;

    /* 通信读事件未发生时，出现事件的时间*/
    private int readerIdleTimeSeconds = 0;
    /* 通信写事件未发生时，出现事件的时间*/
    private int writerIdleTimeSeconds = 0;
    /* 通信读写事件未发生时，出现事件的时间*/
    private int serverChannelMaxIdleTimeSeconds = 30;
    /* 请求后未获得响应的超时时间 */
    private int invokeTimeoutMillis = Constants.DEFAULT_INVOKE_TIMEOUT_MILLIS;

    /************************* 	Getter & Setter	*************************/
    public int getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }

    public int getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(int nettyPort) {
        this.nettyPort = nettyPort;
    }


    public int getRegisterSelectorThreads() {
        return registerSelectorThreads;
    }

    public void setRegisterSelectorThreads(int registerSelectorThreads) {
        this.registerSelectorThreads = registerSelectorThreads;
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }

    public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }
}