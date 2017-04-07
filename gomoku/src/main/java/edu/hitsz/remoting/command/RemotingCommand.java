package edu.hitsz.remoting.command;

import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.body.RemotingCommandBody;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author allen 远程传输的类，通过调用createResponseCommand 和
 *         createRequestCommand创建对应的RemotingCommand
 *         <p>
 *         * * 协议格式 | <length> | <serializable id> | <header length> | <header
 *         data> | <body length> | <body data> | <body class> | | 1 | 2 | 3 | 4
 *         | 5 | 6 | 7 |
 *         <p>
 *         1、大端4个字节整数，等于2、3、4、5、6, 7长度总和 2、大端4个字节整数，等 serializable id 3、header
 *         信息长度 大端4个字节整数，等于4的长度 4、header 信息内容 5、body 信息长度 大端4个字节整数，等于6的长度 6、body
 *         信息内容 7、body 的class名称
 */
public class RemotingCommand implements Serializable {
	private static final long serialVersionUID = -6424506729433386206L;
	private static final AtomicInteger requestId = new AtomicInteger(0);
	/**
	 * 通信的命令
	 */
	private int code;
	/**
	 * 保留 未使用
	 */
	private int subCode;
	/**
	 * Serializable id 让序列化和反序列化相同的技术，默认使用Java自带的序列化方式
	 * id = 1 是Java自带的序列化方式
	 */
	private int sid = 1;
	/**
	 * 通信实体的类型  分为Request 和 Response
	 */
	private RemotingCommandType type;
	/**
	 * 标识是否是单向的通信实体
	 */
	private boolean isOneway;
	/**
	 * 用来匹配相应，除了单向通信指令，每一种请求都会对应一个响应，通过该字段进行匹配
	 */
	private int opaque;
	/**
	 * 通信实体的描述信息
	 */
	private String remark;

	private RemotingCommand() {

	}

	/**
	 * 通信实体的内容， transient是为了不被序列化
	 */
	private transient RemotingCommandBody body;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isOneway() {
		return isOneway;
	}

	public void setOneway(boolean isOneway) {
		this.isOneway = isOneway;
	}

	public int getSubCode() {
		return subCode;
	}

	public void setSubCode(int subCode) {
		this.subCode = subCode;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public RemotingCommandType getType() {
		return type;
	}

	public void setType(RemotingCommandType type) {
		this.type = type;
	}

	public int getOpaque() {
		return opaque;
	}

	public void setOpaque(int opaque) {
		this.opaque = opaque;
	}

	public static AtomicInteger getRequestid() {
		return requestId;
	}

	public RemotingCommandBody getBody() {
		return body;
	}

	public void setBody(RemotingCommandBody body) {
		this.body = body;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/**
	 * 创建通信请求实体，默认设置了以下值
	 * 1. 命令 调用者传入
	 * 2. 类型 RemotingCommandType.REQUEST_COMMAND
	 * 3. Opaque 用于匹配响应，目前用requestId
	 * @param code
	 * @return
	 */
	public static RemotingCommand createRequestCommand(int code, RemotingCommandBody body) {
		RemotingCommand cmd = new RemotingCommand();
		cmd.setType(RemotingCommandType.REQUEST_COMMAND);
		cmd.setCode(code);
		cmd.setBody(body);
		requestId.getAndIncrement();
		cmd.setOpaque(requestId.get());
		return cmd;
	}

	/**
	 * 创建通信响应实体，默认设置了以下值
	 * 1. 类型 RemotingCommandType.RESPONSE_COMMAND
	 * 2. 命令 调用者传入
	 * 3. 内容 调用者传入 
	 * @param code
	 * @param body
	 * @return
	 */
	public static RemotingCommand createResponseCommand(int code, RemotingCommandBody body) {
		RemotingCommand cmd = new RemotingCommand();
		cmd.setType(RemotingCommandType.RESPONSE_COMMAND);
		cmd.setCode(code);
		cmd.setBody(body);
		return cmd;
	}

	public static RemotingCommand createResponseCommand(int code, String remark) {
		RemotingCommand cmd = new RemotingCommand();
		cmd.setType(RemotingCommandType.RESPONSE_COMMAND);
		cmd.setCode(code);
		cmd.setRemark(remark);
		cmd.setBody(new NullResponseBody());
		return cmd;
	}
	
}
