package com.lovedev.user.config;

import com.lovedev.user.security.JwtTokenProvider;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {
    private final JwtTokenProvider jwtTokenProvider;

    public RequestInterceptor serviceTokenInterceptor() {
        return request -> {
        //    String token = jwtTokenProvider.generateServiceToken();
        //    request.header("Authorization", "Bearer " + token);
        };
    }
}
