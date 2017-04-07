package edu.hitsz.remoting;

import edu.hitsz.commons.constants.Constants;

/**
 * 远程通信Client配置信息
 * @author  Neuclil
 */
public class RemotingClientConfig {
	/* 客户端执行线程数 */
	private int clientWorkerThreads = 4;
	/* 调用回调函数的线程池的线程数 */
    private int clientCallbackExecutorThreads = Constants.AVAILABLE_PROCESSOR * 2;
    /* Netty框架使用的线程池的线程个数 */
    private int clientSelectorThreads = 1;
    /* 控制同时的单向请求个数的信号量 */
    private int clientOnewaySemaphoreValue = 256;
    /* 控制同时的异步请求个数的信号量 */
    private int clientAsyncSemaphoreValue = 128;
    /* 连接超时时间 */
    private long connectTimeoutMillis = 3000;
    /* netty通信异常捕获相关 */
    private int readerIdleTimeSeconds = 0;
    private int writerIdleTimeSeconds = 0;
    private int clientChannelMaxIdleTimeSeconds = 120;
    /* 请求的超时时间 */
    private int invokeTimeoutMillis = 20000;

    /************************* 	Getter & Setter	*************************/
    public int getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }

    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }

    public void setClientWorkerThreads(int clientWorkerThreads) {
        this.clientWorkerThreads = clientWorkerThreads;
    }

    public int getClientSelectorThreads() {
        return clientSelectorThreads;
    }

    public void setClientSelectorThreads(int clientSelectorThreads) {
        this.clientSelectorThreads = clientSelectorThreads;
    }

    public int getClientOnewaySemaphoreValue() {
        return clientOnewaySemaphoreValue;
    }

    public void setClientOnewaySemaphoreValue(int clientOnewaySemaphoreValue) {
        this.clientOnewaySemaphoreValue = clientOnewaySemaphoreValue;
    }

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getClientCallbackExecutorThreads() {
        return clientCallbackExecutorThreads;
    }

    public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
        this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
    }

    public int getClientAsyncSemaphoreValue() {
        return clientAsyncSemaphoreValue;
    }

    public void setClientAsyncSemaphoreValue(int clientAsyncSemaphoreValue) {
        this.clientAsyncSemaphoreValue = clientAsyncSemaphoreValue;
    }

    public int getClientChannelMaxIdleTimeSeconds() {
        return clientChannelMaxIdleTimeSeconds;
    }

    public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
        this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }
}