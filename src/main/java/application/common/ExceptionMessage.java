package application.common;

public enum ExceptionMessage {
    VALUE_LENGTH_LOWERBOUND_EXCEPTION("길이가 0보다 커야합니다."),
    USER_ID_LENGTH_UPPERBOUND_EXCEPTION("유저 아이디 길이가 20보다 작아야합니다."),
    PASSWORD_LENGTH_UPPERBOUND_EXCEPTION("비밀번호 길이가 20보다 작아야합니다."),
    NAME_LENGTH_UPPERBOUND_EXCEPTION("이름 길이가 10보다 작아야합니다."),
    USER_ID_DUPLICATED_EXCEPTION("해당 id가 이미 존재합니다."),
    INVALID_EMAIL_FORM_EXCEPTION("유효하지 않은 이메일 형식입니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}