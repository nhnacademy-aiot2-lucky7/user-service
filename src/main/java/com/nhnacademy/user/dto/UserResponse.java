package com.nhnacademy.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {

    @JsonProperty("userRole")
    String userRole;

    @JsonProperty("userNo")
    Long userNo;

    @JsonProperty("userName")
    String userName;

    @JsonProperty("userEmail")
    String userEmail;

    @JsonProperty("userPhone")
    String userPhone;

    @JsonProperty("userDepartment")
    String userDepartment;
}
