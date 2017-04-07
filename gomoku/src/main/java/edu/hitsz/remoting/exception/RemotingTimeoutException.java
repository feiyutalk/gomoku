package edu.hitsz.remoting.exception;

/**
 * RPC调用超时异常
 */
public class RemotingTimeoutException extends RemotingException {

	private static final long serialVersionUID = 4106899185095245979L;

	public RemotingTimeoutException(String message) {
		super(message);
	}

	public RemotingTimeoutException(String addr, long timeoutMillis) {
		this(addr, timeoutMillis, null);
	}

	public RemotingTimeoutException(String addr, long timeoutMillis, Throwable cause) {
		super("等待该地址 <" + addr + "> 的响应超时, " + timeoutMillis + "(ms)", cause);
	}
}
