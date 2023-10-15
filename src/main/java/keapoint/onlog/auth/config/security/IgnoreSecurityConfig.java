package keapoint.onlog.auth.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class IgnoreSecurityConfig {

    @Bean
    public SecurityFilterChain IgnoreFilterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable) // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
                .csrf(AbstractHttpConfigurer::disable) // rest api이므로 csrf 보안이 필요없으므로 disable처리.
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증 -> 세션은 필요없으므로 생성안함.
                )
                .cors(AbstractHttpConfigurer::disable) // CORS(Cross-Origin Resource Sharing) 설정 비활성화.
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // X-Frame-Options 비활성화 (IFrame 사용 가능하도록).
                )
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 대신 Jwt를 사용하기 때문에 disable로 설정
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 기능 비활성화.
                .authorizeHttpRequests(request ->
                        request.requestMatchers("auth/kakao/login").permitAll() // "/auth/kakao/login" 경로에 대한 요청은 인증 없이 허용
                                .requestMatchers("/v3/api-docs/**").permitAll() // Swagger는 인증 없이 허용
                                .requestMatchers("/swagger-ui/**").permitAll() // Swagger는 인증 없이 허용
                );

        return http.build();
    }
}
