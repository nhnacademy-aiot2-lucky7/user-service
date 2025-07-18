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

    @Column(name = "main_dashboard_uid", length = 200)
    private String mainDashboardUid;

    @Column(name = "main_dashboard_title", length = 200)
    private String mainDashboardTitle;

    public void updateDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void updateMainDashboard(String mainDashboardUid, String mainDashboardTitle) {
        this.mainDashboardUid = mainDashboardUid;
        this.mainDashboardTitle = mainDashboardTitle;
    }
}
