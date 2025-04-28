package com.nhnacademy.role.repository;

import com.nhnacademy.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String>, CustomRoleRepository {
}
