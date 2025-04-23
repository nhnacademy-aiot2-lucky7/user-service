package com.nhnacademy.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    public UserResponse(String userRole, Long userNo, String userName, String userEmail, String userPhone, String userDepartment) {
        this.userRole = userRole;
        this.userNo = userNo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userDepartment = userDepartment;
    }

    /**
     * User 엔티티로부터 DTO 생성 (편의 메서드)
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getRole().getRoleName(),
                user.getUserNo(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserPhone(),
                user.getDepartment().getDepartmentId()
        );
    }
}
