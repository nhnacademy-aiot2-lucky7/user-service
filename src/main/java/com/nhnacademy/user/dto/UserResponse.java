package com.nhnacademy.user.dto;

import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {

    String userRole;

    Long userNo;

    String userName;

    String userEmail;

    String userPhone;

    DepartmentResponse department;

    EventLevelResponse eventLevelResponse;
}
