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
public class SocialUserLoginRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @JsonProperty("userEmail")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String userEmail;

    @NotBlank(message = "토큰은 필수 입력 항목입니다.")
    @JsonProperty("accessToken")
    private String accessToken;
}