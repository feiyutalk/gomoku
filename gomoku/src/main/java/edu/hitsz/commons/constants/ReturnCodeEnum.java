package edu.hitsz.commons.constants;

/**
 * Created by loggerhead on 3/20/17.
 */
public enum ReturnCodeEnum {
	/**
	 * 未知错误类型
	 */
	RETURNCODE_UNKNOWNERROR(-1),
	/**
	 * 操作成功
	 */
	RETURNCODE_SUCCESS(0),
	/**
	 * 操作失败
	 */
	RETURNCODE_FAILED(1),

	/*=======================RMI 专用返回枚举类型=======================*/
	/**
	 * 未登陆 身份验证错误
	 */
	RMIRETURNCODE_HASLOGOUT(401),
	/**
	 * 权限问题
	 */
	RMIRETURNCODE_ROLELIMIT(402),
	/**
	 * 登陆超时
	 */
	RMIRETURNCODE_LOGINTIMEOUT(403),
	/**
	 * IO错误
	 */
	RMIRETURNCODE_IOEXCEPTION(404),

	/*=================Worker 与 server 通讯返回枚举类型===================*/
	/**
	 * Worker数量超出限制
	 */
	WORKERNUM_OVERLIMIT(501),

	/*=================User Login and logout Return Code===================*/

	LOGIN_SUCCESS(300),

	LOGIN_USERINFOERROR(301),

	SESSIONMANGE_SUCCESS(310),

	SESSIONMANGE_HASLOGOUT(311),

	/*====================Project status return code======================*/
	PROJECTSTATUS_WAIT(601),

	PROJECTSTATUS_RUN(602),

	PROJECTSTATUS_STOP(600),

	PROJECTSTATUS_FINISH(603),

	PROJECTSTATUS_DELETE(604),

	PROJECTSTATUS_DISCONNECTION(605),

	PROJECTSTATUS_PAUSE(606),

	/*====================Project Deploy Return Code======================*/

	PROJECTDEPLOY_SUCCESS(200),

	PROJECTDEPLOY_INVALIDUSERINFO(201),

	PROJECTDEPLOY_TRANSMISSIONERROR(202),

	PROJECTDEPLOY_AREADYEXIST(203),

	PROJECTDEPLOY_NONEXITENTPROJECT(204),

	PROJECTDEPLOY_RUNNING(205),

	PROJECTDEPLOY_DISCONNECTEDWORKER(206),

	PROJECTDEPLOY_FAIL(207),

	PROJECTDEPLOY_EXITENTPROJECT(208),

	/*====================Project Update Return Code======================*/
	PROJECTUPDATE_SUCCESS(700),

	/*=====================Project Monitor Return Code======================*/
	PROJECTMONITOR_SUCCESS(800),

	PROJECTMONITOR_NONEXITENTPROJECT(801),

	PROJECTMONITOR_DAMAGEDCONFIGFILE(802),

	PROJECTMONITOR_ILLEGALWORKERSTATION(803),


	/**
	 * 用户已经存在
	 */
	USERDATABASE_USEREXIST(101),
	/**
	 * 用户数据库IO错误
	 */
	USERDATABASE_IOEXCEPTION(102),
	/**
	 * 用户数据库SQL错误
	 */
	USERDATABASE_SQLEXCEPTION(103),
	/**
	 * 数据库驱动未找到
	 */
	USERDATABASE_CLASSNOTFOUNDEXCEPTION(104),

	/**
	 * 没有用户存在
	 */
	USERDATABASE_NOUSEREXIST(105),

	/**
	 * 用户权限错误
	 */
	USERDATABASE_ROLELIMIT(106),

	/**
	 * 用户名过长
	 */
	USERDATABASE_MANETOOLONG(107),

	/**
	 * 密码过长
	 */
	USERDATABASE_PWDTOOLONG(108),

	/**
	 * 用户名为空
	 */
	USERDATABASE_USERNAMENULL(108),

	/**
	 * 密码为空
	 */
	USERDATABASE_PWDNULL(109);
	//The End


	private  final int mValue;

	private ReturnCodeEnum(int value) {
		mValue = value;
	}

	public int getValue() {
		return mValue;
	}

	// 等待projectEngine操作休眠间隔
	public static final long OP_MS_PER_WAIT = 5000;
}
