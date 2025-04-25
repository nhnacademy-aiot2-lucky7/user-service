package com.nhnacademy.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserRoleUpdateRequest {
    @JsonProperty("userId")
    @NotBlank(message = "userId는 필수 입력 항목입니다.")
    @Email
    private String userId;

    @JsonProperty("roleId")
    @NotBlank(message = "roleId는 필수 입력 항목입니다.")
    private String roleId;
}
