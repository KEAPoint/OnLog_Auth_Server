package keapoint.onlog.auth.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import keapoint.onlog.auth.base.BaseException;
import keapoint.onlog.auth.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Token 추출
        String token = extractToken((HttpServletRequest) request);

        // Swagger UI 요청은 필터링하지 않고 바로 통과시킴
        if (((HttpServletRequest) request).getRequestURI().equalsIgnoreCase("/swagger-ui/index.html")) return;

        // 로깅
        log.info("request uri: " + ((HttpServletRequest) request).getRequestURI());

        // 토큰 유효성 검사
        if (token != null && jwtTokenProvider.vallidateToken(token)) {
            try {
                // Authentication 객체를 가지고 와서 SecurityContext 에 추가
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (BaseException exception) {
                log.error(exception.getMessage());
                throw new RuntimeException(exception);
            }
        }

        chain.doFilter(request, response); // 다음 필터로 넘기거나 요청 처리 진행
    }

    /**
     * Request에서 토큰 추출하는 메소드
     *
     * @param request HttpServletRequest 객체
     * @return access token 문자열 또는 null (추출 실패 시)
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION); // Authorization 헤더에서 Bearer 토큰을 가져옴
        String[] parsedToken = bearerToken.split(" ");

        if (parsedToken[0].equalsIgnoreCase("Bearer")) { // Bearer 시작 형식인지 확인
            return parsedToken[1]; // "Bearer " 부분을 제외한 실제 토큰 문자열 반환
        }

        return null;
    }
}

