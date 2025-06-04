package com.nhnacademy.user.dto;

import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
public class UserResponse {

    String userRole;

    Long userNo;

    String userName;

    String userEmail;

    String userPhone;

    DepartmentResponse department;

    EventLevelResponse eventLevelResponse;

    @QueryProjection
    public UserResponse(String userRole, Long userNo, String userName, String userEmail, String userPhone, DepartmentResponse department, EventLevelResponse eventLevelResponse) {
        this.userRole = userRole;
        this.userNo = userNo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.department = department;
        this.eventLevelResponse = eventLevelResponse;
    }
}
