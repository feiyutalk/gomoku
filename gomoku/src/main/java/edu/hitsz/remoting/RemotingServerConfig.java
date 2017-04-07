package edu.hitsz.remoting;

import edu.hitsz.commons.constants.Constants;

public class RemotingServerConfig {
    /* 配置信息 */
    private int listenPort = 9000;
    /* 工作线程数量 未使用*/
    private int serverWorkerThreads = 32;
    /* 执行回调线程池线程数量 未使用*/
    private int serverCallbackExecutorThreads = Constants.AVAILABLE_PROCESSOR * 2;
    /* Selector线程数量*/
    private int serverSelectorThreads = Constants.AVAILABLE_PROCESSOR * 2;
    /* 单向请求的信号量 许可数目*/
    private int serverOnewaySemaphoreValue = 32;
    /* 异步请求的信号量 许可数目*/
    private int serverAsyncSemaphoreValue = 64;

    /* 通信读事件未发生时，出现事件的时间*/
    private int readerIdleTimeSeconds = 0;
    /* 通信写事件未发生时，出现事件的时间*/
    private int writerIdleTimeSeconds = 0;
    /* 通信读写事件未发生时，出现事件的时间*/
    private int serverChannelMaxIdleTimeSeconds = 120;
    /* 请求后未获得响应的超时时间 */
    private int invokeTimeoutMillis = Constants.DEFAULT_INVOKE_TIMEOUT_MILLIS;

    /************************* 	Getter & Setter	*************************/
    public int getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }

    public void setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
    }

    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }

    public void setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
    }

    public int getServerOnewaySemaphoreValue() {
        return serverOnewaySemaphoreValue;
    }

    public void setServerOnewaySemaphoreValue(int serverOnewaySemaphoreValue) {
        this.serverOnewaySemaphoreValue = serverOnewaySemaphoreValue;
    }

    public int getServerCallbackExecutorThreads() {
        return serverCallbackExecutorThreads;
    }

    public void setServerCallbackExecutorThreads(int serverCallbackExecutorThreads) {
        this.serverCallbackExecutorThreads = serverCallbackExecutorThreads;
    }

    public int getServerAsyncSemaphoreValue() {
        return serverAsyncSemaphoreValue;
    }

    public void setServerAsyncSemaphoreValue(int serverAsyncSemaphoreValue) {
        this.serverAsyncSemaphoreValue = serverAsyncSemaphoreValue;
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

    @Override
    public String toString() {
        return "RemotingServerConfig [listenPort=" + listenPort + ", serverWorkerThreads=" + serverWorkerThreads
                + ", serverCallbackExecutorThreads=" + serverCallbackExecutorThreads + ", serverSelectorThreads="
                + serverSelectorThreads + ", serverOnewaySemaphoreValue=" + serverOnewaySemaphoreValue
                + ", serverAsyncSemaphoreValue=" + serverAsyncSemaphoreValue + ", readerIdleTimeSeconds="
                + readerIdleTimeSeconds + ", writerIdleTimeSeconds=" + writerIdleTimeSeconds
                + ", serverChannelMaxIdleTimeSeconds=" + serverChannelMaxIdleTimeSeconds + ", invokeTimeoutMillis="
                + invokeTimeoutMillis + "]";
    }

}