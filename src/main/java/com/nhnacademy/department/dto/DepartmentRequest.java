package com.nhnacademy.department.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class DepartmentRequest {
    @JsonProperty("departmentId")
    @NotBlank(message = "부서ID는 필수 입력 항목입니다.")
    @Size(max = 45, message = "부서ID는 최대 45자까지 입력할 수 있습니다.")
    private String departmentId;

    @JsonProperty("departmentName")
    @NotBlank(message = "부서명은 필수 입력 항목입니다.")
    @Size(max = 45, message = "부서명은 최대 45자까지 입력할 수 있습니다.")
    private String departmentName;
}
