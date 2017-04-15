package edu.hitsz.remoting.command.protocol;

public final class RemotingProtos {
    private RemotingProtos() {
    }

    public enum RequestCode {
        CONNECT(0),
        MATCH(1),
        CHANGE_BOARD(2),
        OPPONENT_CHANGE_BOARD(3),
        WIN(4),
        LOSE(5);
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
		CONNECT_SUCCESS(0),
        MATCH_SUCCESS(1),
        CHANGE_BOARD_SUCCESS(2),
        CHANGE_BOARD_FAILURE(3),
        CHANGE_OPPONENT_BOARD_SUCCESS(4),
        CHANGE_OPPONENT_BOARD_FAILURE(5),
        REQUEST_CODE_NOT_SUPPORTED(1000),
        COMMAND_PROCESS_ERROR(1001),
        SYSTEM_BUSY(1002),
        SYSTEM_ERROR(1003);
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