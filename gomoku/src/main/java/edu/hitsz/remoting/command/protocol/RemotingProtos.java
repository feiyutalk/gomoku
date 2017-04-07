package edu.hitsz.remoting.command.protocol;

public final class RemotingProtos {
    private RemotingProtos() {
    }

    public enum RequestCode {
        // 连接
        CONNECT(0),

        REQUEST_MASTER_ADDR(1),
        // 任务
        HEART_BEAT(100),

        TASK_PULL(101),

        TASK_PUSH(102),

        TASK_COMPLETED(103),

        TASK_CANCLE(104),

        REPORT_STATUS(105),

        CHECK_STATUS(106),

        PROJECT_PULL(107),

        SUBMIT_PROJECT(108),
    	//系统
    	GET_WORKER_INFO(200);
        private int code;

        private RequestCode(int code) {
            this.code = code;
        }

        public static RequestCode valueOf(int code) {
            for (RequestCode requestCode : RequestCode.values()) {
                if (requestCode.code == code) {
                    return requestCode;
                }
            }
            throw new IllegalArgumentException("can't find the response code !");
        }

        public int code() {
            return this.code;
        }
    }

	public enum ResponseCode {
		/**
		 * 这些code是与通信相关的
		 */
		// 成功
		CONNECT_SUCCESS(0),
		// 发生了未捕获异常
		COMMAND_PROCESS_ERROR(1),
		// 由于线程池拥堵，系统繁忙
		SYSTEM_BUSY(2),
		// 请求代码不支持
		REQUEST_CODE_NOT_SUPPORTED(3),
		// 请求参数错误
		REQUEST_PARAM_ERROR(4),
		// 请求地址成功
		REQUEST_ADDR_SUCCESS(5),
		
		REQUEST_ADDR_FAILURE(6),

		/**
		 * 这些code是与任务相关的
		 */
		TASK_RECEIVE_SUCCESS(101),

        TASK_RECEIVE_FAILED(102),

        TASK_RUN_SUCCESS(103),

        TASK_RUN_ERROR(104),

        HEART_BEAT_SUCCESS(105),

        TASK_PULL_SUCCESS(106),

        STATUS_RECEIVE_SUCCESS(107),

        STATUS_RECEIVE_FAILED(108),

        CHECK_STATUS_SUCCESS(109),

        CHECK_STATUS_FAILED(110),

        SUBMIT_PROJECT_SUCCESS(111),

        TASK_PUSH_SUCCESS(112),
		
        /*******************  系统相关  ***********************/
		GET_WORKER_INFO_SUCCESS(200),
		GET_WORKER_INFO_FAILURE(201);
        private int code;

        ResponseCode(int code) {
            this.code = code;
        }

        public static ResponseCode valueOf(int code) {
            for (ResponseCode responseCode : ResponseCode.values()) {
                if (responseCode.code == code) {
                    return responseCode;
                }
            }
            throw new IllegalArgumentException("can't find the response code !");
        }

        public int code() {
            return this.code;
        }
    }
}