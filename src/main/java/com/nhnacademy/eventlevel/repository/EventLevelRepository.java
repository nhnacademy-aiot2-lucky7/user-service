package com.nhnacademy.eventlevel.repository;

import com.nhnacademy.eventlevel.domain.EventLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLevelRepository extends JpaRepository<EventLevel, String>, CustomEventLevelRepository {
}
