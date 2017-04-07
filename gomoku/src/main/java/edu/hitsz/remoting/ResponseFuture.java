package edu.hitsz.remoting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.commons.utils.SemaphoreReleaseOnlyOnce;
import edu.hitsz.commons.utils.SystemClock;

/**
 * @author Neuclil
 * 对异步请求应答的封装
 */
public class ResponseFuture {

    /* 用于匹配响应 */
    private final int opaque;

    /* 响应超时时间 */
    private final long timeoutMillis;

    /* 基于异步响应的回调类 */
    private final AsyncCallback asyncCallback;

    /* 请求开始时间 */
    private final long beginTimestamp = SystemClock.now();

    /* 信号量，用于同步阻塞，等待的事件是收到对应的response响应 */
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    /* 信号量工具类 用于保证信号量只释放一次*/
    private final SemaphoreReleaseOnlyOnce once;

    /* 用于保证回调函数只执行一次 */
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    /*存放接收到的响应*/
    private volatile RemotingCommand responseCommand;

    /*标识字段，判断与该响应匹配的请求是否发送成功*/
    private volatile boolean sendRequestOK = true;

    /*存放异常*/
    private volatile Throwable cause;

    /**
     * 构造函数
     *
     * @param opaque
     * @param timeoutMillis
     * @param asyncCallback
     * @param once
     */
    public ResponseFuture(int opaque, long timeoutMillis, AsyncCallback asyncCallback, SemaphoreReleaseOnlyOnce once) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
        this.asyncCallback = asyncCallback;
        this.once = once;
    }

    /**
     * 回调函数
     */
    public void executeInvokeCallback() {
        if (asyncCallback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                asyncCallback.operationComplete(this);
            }
        }
    }

    /**
     * 释放信号量
     * 当要发送请求时，先要获取信号量
     */
    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    /**
     * 判断是否超时
     *
     * @return
     */
    public boolean isTimeout() {
        long diff = SystemClock.now() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    /**
     * 利用CountDownLatch来挂起线程，用于阻塞同步请求
     *
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    /**
     * 同步请求得到响应后，将响应消息放入ResponseFuture中，同时CountDownLatch减1，让线程继续执行
     *
     * @param responseCommand
     */
    public void putResponseAndReleaseSync(final RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    /************************* 	Getter & Setter	*************************/
    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public AsyncCallback getAsyncCallback() {
        return asyncCallback;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RemotingCommand getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
    }

    public int getOpaque() {
        return opaque;
    }

    @Override
    public String toString() {
        return "ResponseFuture [responseCommand=" + responseCommand + ", sendRequestOK=" + sendRequestOK + ", cause="
                + cause + ", opaque=" + opaque + ", timeoutMillis=" + timeoutMillis + ", invokeCallback="
                + asyncCallback + ", beginTimestamp=" + beginTimestamp + ", countDownLatch=" + countDownLatch + "]";
    }
}