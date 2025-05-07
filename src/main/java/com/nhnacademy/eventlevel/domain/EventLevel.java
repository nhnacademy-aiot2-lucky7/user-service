package com.nhnacademy.eventlevel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_levels")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventLevel {
    @Id
    @Column(name = "event_level_name", length = 50)
    private String eventLevelName;

    @Column(name = "event_level_details", length = 300)
    private String eventLevelDetails;

    @Column(name = "priority")
    private Integer priority;

    public void updateEventLevel(String levelDetails, int priority) {
        this.eventLevelDetails = levelDetails;
        this.priority = priority;
    }
}
