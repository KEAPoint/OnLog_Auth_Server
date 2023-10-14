package com.service.ttucktak.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * JWT 필요 없는 API 시큐리티 체인
 * @author LEE JIHO
 * */
@EnableWebSecurity(debug = true)
@Configuration
public class IgnoreSecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain IgnoreFilterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 토큰으로 인증하므로 stateless
                .and()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()//Form Based Authentication 을 사용하지 않음
                .httpBasic().disable()
                .logout().disable()
                .csrf().disable()// HTTP Basic Authentication 을 사용하지 않음
                .cors().disable()
                .authorizeHttpRequests() //Http Request를 인가하라
                .requestMatchers("/api/auths/signup").permitAll()
                .requestMatchers("/api/auths/login").permitAll()
                .requestMatchers("/api/auths/email-confirm").permitAll()
                .requestMatchers("/api/auths/oauth2/kakao").permitAll()
                .requestMatchers("/api/auths/oauth2/kakao/test").permitAll()
                .requestMatchers("/api/auths/oauth2/login/kakao").permitAll()
                .requestMatchers("/api/auths/oauth2/google").permitAll()
                .requestMatchers("/api/auths/oauth2/login/google").permitAll()
                .requestMatchers("/api/members/nickname/**").permitAll()
                .requestMatchers("/oauth2/authorization/kakao").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/index.html").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/page/leafs/**").permitAll()
                .requestMatchers("/api/members/password/lost").permitAll()
                .requestMatchers("/api/members/password/email").permitAll()
                .requestMatchers("/api/auths/token/refresh").permitAll()
                .anyRequest().permitAll();

        return http.build();
    }
}
