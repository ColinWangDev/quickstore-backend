package com.quickstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // 暂时禁用CSRF，实际项目中应该启用
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/test/**").permitAll()  // 允许测试接口的访问
                .anyRequest().authenticated()  // 其他所有请求需要认证
            );
        
        return http.build();
    }
} 