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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트 레벨"));
    }

    @Override
    public void createEventLevel(EventLevelRequest eventLevelRequest) {
        if (eventLevelRepository.existsById(eventLevelRequest.getLevelName())) {
            throw new ConflictException("이미 존재하는 eventLevel");
        }

        EventLevel eventLevel = new EventLevel(eventLevelRequest.getLevelName(), eventLevelRequest.getLevelDetails());

        eventLevelRepository.save(eventLevel);
    }

    @Override
    public void updateEventLevel(EventLevelRequest eventLevelRequest) {
        EventLevel eventLevel = eventLevelRepository.findById(eventLevelRequest.getLevelName())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트 레벨"));

        eventLevel.updateEventLevelDetails(eventLevelRequest.getLevelDetails());

        eventLevelRepository.save(eventLevel);
    }

    @Override
    public void deleteEventLevel(String levelName) {
        if (!eventLevelRepository.existsById(levelName)) {
            throw new NotFoundException("존재하지 않는 이벤트 레벨");
        }

        eventLevelRepository.deleteById(levelName);
    }
}
