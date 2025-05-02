package com.nhnacademy.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserUpdateRequest {
    @JsonProperty("userName")
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    private String userName;

    @JsonProperty("userPhone")
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. 예: 010-1234-5678")
    private String userPhone;

    @JsonProperty("userDepartmentId")
    @NotBlank(message = "부서ID는 필수 입력 항목입니다.")
    @Size(max = 45, message = "부서ID는 최대 45자까지 입력할 수 있습니다.")
    private String userDepartmentId;

    @JsonProperty("notificationLevel")
    @NotBlank(message = "알림 레벨은 필수 입력 항목입니다.")
    @Size(max = 45, message = "알림 레벨은 최대 45자까지 입력할 수 있습니다.")
    private String notificationLevel;
}
