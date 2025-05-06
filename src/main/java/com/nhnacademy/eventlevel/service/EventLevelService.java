package com.nhnacademy.eventlevel.service;

import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;

import java.util.List;

public interface EventLevelService {
    EventLevelResponse getEventLevelByLevelName(String levelName);

    List<EventLevelResponse> getAllEventLevel();

    void createEventLevel(EventLevelRequest eventLevelRequest);

    void updateEventLevel(EventLevelRequest eventLevelRequest);

    void deleteEventLevel(String levelName);
}
