package com.nhnacademy.department.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "department_id", length = 50)
    private String departmentId;

    @Column(name = "department_name", length = 100, nullable = false)
    private String departmentName;
}
