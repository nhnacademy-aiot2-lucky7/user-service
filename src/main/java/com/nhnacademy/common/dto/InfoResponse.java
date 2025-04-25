package com.nhnacademy.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * Actuator Info를 통하여, API Service의 정보를 보내기 위한 DTO 클래스입니다.
 */
@Getter
@ToString
public class InfoResponse {

    /**
     * API Service 정보
     */
    private final AppInfo appInfo;

    /**
     * API Service를 제작한 조직을 나타냅니다.
     */
    private final String company;

    @JsonCreator
    public InfoResponse(
            @JsonProperty("app") AppInfo appInfo,
            @JsonProperty("company") String company
    ) {
        this.appInfo = appInfo;
        this.company = company;
    }
}
