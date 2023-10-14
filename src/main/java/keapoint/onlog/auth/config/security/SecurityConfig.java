package com.service.ttucktak.config.security;


import com.service.ttucktak.config.CustomOAuthUserService;
import com.service.ttucktak.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT 필요한 api 필터 체인
 * @author  LEE JIHO
 * */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuthUserService customOAuthUserService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(CustomOAuthUserService customOAuthUserService, JwtUtil jwtUtil) {
        this.customOAuthUserService = customOAuthUserService;
        this.jwtUtil = jwtUtil;
    }

    @Order(10)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);

        http
                .csrf().disable()//csrf (사이트간 위조 요청) 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 토큰으로 인증하므로 stateless
                .and()
                .formLogin().disable()//Form Based Authentication 을 사용하지 않음
                .httpBasic().disable()// HTTP Basic Authentication 을 사용하지 않음
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests() //Http Request를 인가하라
                .requestMatchers("/api/members/**").permitAll()
                .requestMatchers("/api/views/**").permitAll()
                .requestMatchers("/api/solutions/**").permitAll()
                .anyRequest().authenticated()// 이외의 접근은 인증이 필요하다
                .and()//그리고
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Jwt 필터를 추가한다.
                .oauth2Login() //OAuth Login은 다음과 같다
                .userInfoEndpoint()
                .userService(customOAuthUserService);

        //exception Handling
        http.exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/api/auths/exception");
                });

        return http.build();
    }

    /**
     * 시큐리티 제공 패스워드 인코더
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
