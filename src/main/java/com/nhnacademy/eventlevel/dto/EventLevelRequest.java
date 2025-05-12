package com.nhnacademy.eventlevel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class EventLevelRequest {
    @NotBlank(message = "이벤트 레벨을 입력해주세요.")
    @Size(max = 50, message = "이벤트 레벨은 최대 50자까지 입력할 수 있습니다.")
    private String eventLevelName;

    @Size(max = 300, message = "이벤트 레벨 설명은 최대 300자까지 입력할 수 있습니다.")
    private String eventLevelDetails;

    @NotNull(message = "이벤트 우선순위를 입력해주세요.")
    private Integer priority;
}
