package keapoint.onlog.auth.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * JWT 필요 없는 API 시큐리티 체인
 * */
@EnableWebSecurity(debug = true)
@Configuration
public class IgnoreSecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain IgnoreFilterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰으로 인증하므로 stateless
                .and()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()//Form Based Authentication 을 사용하지 않음
                .httpBasic().disable()
                .logout().disable()
                .csrf().disable()// HTTP Basic Authentication 을 사용하지 않음
                .cors().disable()
                .authorizeHttpRequests() //Http Request를 인가하라
                .requestMatchers("/auth/kakao/callback").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/index.html").permitAll()
                .anyRequest().permitAll();

        return http.build();
    }
}
