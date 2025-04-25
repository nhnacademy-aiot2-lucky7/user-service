package com.nhnacademy.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.common.dto.AppInfo;
import com.nhnacademy.common.dto.InfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:application-actuator-info.properties")
class ActuatorInfoConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int testPort;

    @Value("${management.info.app.name}")
    private String testName;

    @Value("${management.info.app.description}")
    private String testDescription;

    @Value("${management.info.app.content-type}")
    private String testContents;

    @Value("${management.info.app.version}")
    private String testVersion;

    @Value("${management.info.company}")
    private String testCompany;

    private InfoResponse expected;

    private Set<String> testContentType;

    private Object json;

    @BeforeEach
    void setUp() {
        testContentType = Set.of(testContents.split(","));
        expected = new InfoResponse(
                new AppInfo(
                        testName,
                        testDescription,
                        List.of(testContents.split(",")),
                        testVersion
                ),
                testCompany
        );

        ResponseEntity<Map<String, Object>> responseEntity =
                restTemplate.exchange(
                        "http://localhost:%d/info".formatted(testPort),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().get("info"));

        json = responseEntity.getBody().get("info");
    }

    /**
     * usingRecursiveComparison()   - 전체 객체 구조 & 값이 예상과 같을 때, 추천
     * ignoringCollectionOrder()    - 리스트의 순서는 무시하지만, 값이 동일하다면 `equal`으로 간주합니다.
     */
    @Test
    @DisplayName("Actuator Info Check")
    void testInfo() {
        InfoResponse actual = objectMapper.convertValue(json, InfoResponse.class);
        log.debug("infoResponse: {}", actual);

        actual.getAppInfo().getContentType().forEach(content ->
                Assertions.assertTrue(testContentType.contains(content))
        );

        assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
