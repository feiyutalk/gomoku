package edu.hitsz.remoting;

import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

/**
 * @author Neuclil
 */
public interface RemotingClient extends Remoting {
    /**
     * 注册Client处理器
     *
     * @param requestCode
     * @param processor
     * @param executor
     */
    public void registerClientProcessor(final int requestCode, final RemotingProcessor processor,
                                        final ExecutorService executor);

    /**
     * 通过ip地址 发送单向请求
     *
     * @param addr
     * @param request
     * @param timeoutMillis
     * @throws Exception
     */
    void invokeOneway(String addr, RemotingCommand request, long timeoutMillis) throws Exception;

    /**
     * 通过ip地址 发送同步请求
     *
     * @param addr
     * @param request
     * @param timeoutMillis
     * @return
     * @throws Exception
     */
    RemotingCommand invokeSync(String addr, RemotingCommand request, long timeoutMillis) throws Exception;

    /**
     * 通过ip地址 发送异步请求
     *
     * @param addr
     * @param request
     * @param timeoutMillis
     * @param asyncCallback
     * @throws Exception
     */
    void invokeAsync(String addr, RemotingCommand request, long timeoutMillis, AsyncCallback asyncCallback)
            throws Exception;

}
