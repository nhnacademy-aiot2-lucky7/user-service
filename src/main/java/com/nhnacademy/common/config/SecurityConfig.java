package com.nhnacademy.common.config;

import com.common.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${aes.secret.key}")
    private String secretKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AESUtil aesUtil() {
        AESUtil aesUtil = new AESUtil();
        aesUtil.setKey(secretKey);
        return aesUtil;
    }
}
