package keapoint.onlog.auth.config.security;

import org.springframework.http.HttpHeaders;

public class CustomHttpHeaders extends HttpHeaders {
    public static final String KAKAO_AUTH = "Kakao-auth-code";
}
