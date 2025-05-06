package com.nhnacademy.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class RoleRequest {
    @NotBlank(message = "권한ID를 입력해주세요.")
    @Size(max = 50, message = "권한 아이디는 최대 50자까지 입력할 수 있습니다.")
    private String roleId;

    @NotBlank(message = "권한 이름을 입력해주세요")
    @Size(max = 100, message = "권한 이름은 최대 100자까지 입력할 수 있습니다.")
    private String roleName;
}
