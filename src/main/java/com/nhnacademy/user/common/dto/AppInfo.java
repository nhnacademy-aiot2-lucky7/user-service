package com.nhnacademy.user.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * API Service의 세부 정보를 담은 DTO 클래스입니다.
 */
@Getter
@ToString
public class AppInfo {

    /**
     * API Service에 대한 간단한 이름(혹은, 명칭)입니다.
     */
    private final String name;

    /**
     * API Service에 대한 설명입니다.
     */
    private final String description;

    /**
     * API Service가 응답할 수 있는 Content-Type 입니다.
     */
    private final List<String> contentType;

    /**
     * API Service의 Version을 명시합니다.
     */
    private final String version;

    @JsonCreator
    public AppInfo(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("content-type") List<String> contentType,
            @JsonProperty("version") String version
    ) {
        this.name = name;
        this.description = description;
        this.contentType = contentType;
        this.version = version;
    }
}
