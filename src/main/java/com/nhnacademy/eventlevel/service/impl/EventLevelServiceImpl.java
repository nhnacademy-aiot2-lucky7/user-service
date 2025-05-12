package com.nhnacademy.eventlevel.service.impl;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.repository.EventLevelRepository;
import com.nhnacademy.eventlevel.service.EventLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventLevelServiceImpl implements EventLevelService {
    private static final String EVENT_LEVEL_NOT_FOUND = "존재하지 않는 이벤트 레벨";
    private static final String EVENT_LEVEL_ALREADY_EXISTS = "이미 존재하는 eventLevel";

    private final EventLevelRepository eventLevelRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventLevelResponse> getAllEventLevel() {
        return eventLevelRepository
                .findAllEventLevel()
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    @Override
    public EventLevelResponse getEventLevelByLevelName(String levelName) {
        return eventLevelRepository.findEventLevelByLevelName(levelName)
                .orElseThrow(() -> new NotFoundException(EVENT_LEVEL_NOT_FOUND));
    }

    @Override
    public void createEventLevel(EventLevelRequest eventLevelRequest) {
        if (eventLevelRepository.existsById(eventLevelRequest.getEventLevelName())) {
            throw new ConflictException(EVENT_LEVEL_ALREADY_EXISTS);
        }

        EventLevel eventLevel = new EventLevel(
                eventLevelRequest.getEventLevelName(),
                eventLevelRequest.getEventLevelDetails(),
                eventLevelRequest.getPriority()
        );
        eventLevelRepository.save(eventLevel);
    }

    @Override
    public void updateEventLevel(EventLevelRequest eventLevelRequest) {
        EventLevel eventLevel = eventLevelRepository.findById(eventLevelRequest.getEventLevelName())
                .orElseThrow(() -> new NotFoundException(EVENT_LEVEL_NOT_FOUND));

        eventLevel.updateEventLevel(eventLevelRequest.getEventLevelDetails(), eventLevelRequest.getPriority());
        eventLevelRepository.save(eventLevel);
    }

    @Override
    public void deleteEventLevel(String levelName) {
        if (!eventLevelRepository.existsById(levelName)) {
            throw new NotFoundException(EVENT_LEVEL_NOT_FOUND);
        }

        eventLevelRepository.deleteById(levelName);
    }
}
