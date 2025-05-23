package com.quickstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许来自前端开发服务器的请求
        config.addAllowedOrigin("http://localhost:3000");
        
        // 允许的HTTP方法
        config.addAllowedMethod("*");
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许发送认证信息（cookies等）
        config.setAllowCredentials(true);
        
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
} 