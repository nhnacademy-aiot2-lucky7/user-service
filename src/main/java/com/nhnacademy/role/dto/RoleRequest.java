package com.nhnacademy.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class RoleRequest {
    @NotBlank(message = "권한ID를 입력해주세요.")
    private String roleId;

    @NotBlank(message = "권한 이름을 입력해주세요")
    private String roleName;
}
