package com.nhnacademy.user.common.config;

import com.nhnacademy.user.common.dto.AppInfo;
import com.nhnacademy.user.common.dto.InfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Actuator Info를 통하여, API Service의 정보를 반환하는 Config 입니다.
 */
@Configuration
@PropertySource("classpath:application-actuator-info.properties")
public class ActuatorInfoConfig {

    /**
     * API Service에 대한 간단한 이름(혹은, 명칭)입니다.
     */
    @Value("${management.info.app.name}")
    private String name;

    /**
     * API Service에 대한 설명입니다.
     */
    @Value("${management.info.app.description}")
    private String description;

    /**
     * API Service가 응답할 수 있는 Content-Type 입니다.
     */
    @Value("${management.info.app.content-type}")
    private String contents;

    /**
     * API Service의 Version을 명시합니다.
     */
    @Value("${management.info.app.version}")
    private String version;

    /**
     * API Service를 제작한 조직을 나타냅니다.
     */
    @Value("${management.info.company}")
    private String company;

    @Bean
    public InfoContributor customInfoContributor() {
        return builder -> builder
                .withDetail("info",
                        new InfoResponse(
                                new AppInfo(
                                        name,
                                        description,
                                        List.of(contents.split(",")),
                                        version
                                ),
                                company
                        )
                );
    }
}
