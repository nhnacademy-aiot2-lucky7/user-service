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
    @Column(name = "level_name", length = 50)
    private String levelName;

    @Column(name = "level_details", length = 300)
    private String levelDetails;

    public void updateEventLevelDetails(String levelDetails) {
        this.levelDetails = levelDetails;
    }
}
