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
        LOSE(5),
        RESTART(6),
        OPPONENT_RESTART(7),
        UNDO(8),
        OPPONENT_UNDO(9),
        OPPONENT_EXIT(10),
        CHALLENGE(11),
        RANDOM_MATCH(12),
        PUSH_WAIT_USERINFO(13),
        START(14),
        OPPONENT_START(15),
        SEND_TEXT(16);
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
        NULL(-1),
		CONNECT_SUCCESS(0),
        MATCH_SUCCESS(1),
        CHANGE_BOARD_SUCCESS(2),
        CHANGE_BOARD_FAILURE(3),
        CHANGE_OPPONENT_BOARD_SUCCESS(4),
        CHANGE_OPPONENT_BOARD_FAILURE(5),
        RESTART_SUCCESS(6),
        RESTART_FAILED(7),
        UNDO_SUCCESS(8),
        CHANLLENGE_SUCCESS(9),
        RANDOM_MATCH_SUCCESS(10),
        SEND_TEXT_SUCCESS(11),
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