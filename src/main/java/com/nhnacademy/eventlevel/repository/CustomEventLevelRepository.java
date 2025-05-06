package com.nhnacademy.eventlevel.repository;

import com.nhnacademy.eventlevel.dto.EventLevelResponse;

import java.util.List;
import java.util.Optional;

public interface CustomEventLevelRepository {
    Optional<List<EventLevelResponse>> findAllEventLevel();

    Optional<EventLevelResponse> findEventLevelByLevelName(String levelName);
}
