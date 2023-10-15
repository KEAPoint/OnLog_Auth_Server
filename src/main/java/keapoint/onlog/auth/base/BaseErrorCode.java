package keapoint.onlog.auth.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    /**
     * 401 Unauthorized
     */
    TOKEN_DECODING_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다."),

    /**
     * 404 Not Found
     */
    EMAIL_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "이메일 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다."),

    /**
     * 500 : INTERNAL SERVER ERROR
     */
    Internal_Server_Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "처리 중에 오류가 발생하였습니다.");

    private final Integer status;
    private final String message;
}
