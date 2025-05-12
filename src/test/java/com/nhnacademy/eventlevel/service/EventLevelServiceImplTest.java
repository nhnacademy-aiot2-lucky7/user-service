package com.nhnacademy.eventlevel.service;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.repository.EventLevelRepository;
import com.nhnacademy.eventlevel.service.impl.EventLevelServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EventLevelServiceImplTest {
    @Mock
    EventLevelRepository eventLevelRepository;

    @InjectMocks
    EventLevelServiceImpl eventLevelService;

    @Test
    @DisplayName("모든 이벤트 레벨 조회")
    void getAllEventLevel() {
        List<EventLevelResponse> eventLevelResponses = IntStream.range(1, 6)
                .mapToObj(i -> new EventLevelResponse("L" + i, "레벨 설명 " + i, i))
                .toList();

        when(eventLevelRepository.findAllEventLevel()).thenReturn(Optional.of(eventLevelResponses));

        List<EventLevelResponse> result = eventLevelService.getAllEventLevel();

        verify(eventLevelRepository, times(1)).findAllEventLevel();
        Assertions.assertEquals(5, result.size());
    }

    @Test
    @DisplayName("레벨명으로 이벤트 레벨 조회")
    void getEventLevelByLevelName() {
        EventLevelResponse eventLevelResponse = new EventLevelResponse("CRITICAL", "심각한 이벤트", 4);

        when(eventLevelRepository.findEventLevelByLevelName(anyString())).thenReturn(Optional.of(eventLevelResponse));

        EventLevelResponse result = eventLevelService.getEventLevelByLevelName("CRITICAL");

        verify(eventLevelRepository, times(1)).findEventLevelByLevelName(anyString());
        Assertions.assertEquals("CRITICAL", result.getEventLevelName());
        Assertions.assertEquals("심각한 이벤트", result.getEventLevelDetails());
    }

    @Test
    @DisplayName("레벨명으로 이벤트 레벨 조회 - 존재하지 않음")
    void getEventLevelByLevelName_exception() {
        when(eventLevelRepository.findEventLevelByLevelName(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> eventLevelService.getEventLevelByLevelName("UNKNOWN"));

        verify(eventLevelRepository, times(1)).findEventLevelByLevelName(anyString());
    }

    @Test
    @DisplayName("이벤트 레벨 생성")
    void createEventLevel() {
        when(eventLevelRepository.existsById(anyString())).thenReturn(false);

        eventLevelService.createEventLevel(new EventLevelRequest("HIGH", "높은 위험 수준", 3));

        verify(eventLevelRepository, times(1)).existsById(anyString());
        verify(eventLevelRepository, times(1)).save(any(EventLevel.class));
    }

    @Test
    @DisplayName("이벤트 레벨 생성 - 이미 존재하는 레벨명")
    void createEventLevel_exception() {
        EventLevelRequest eventLevelRequest = new EventLevelRequest("HIGH", "높은 위험 수준", 3);
        when(eventLevelRepository.existsById(anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () ->
                eventLevelService.createEventLevel(eventLevelRequest));

        verify(eventLevelRepository, times(1)).existsById(anyString());
        verify(eventLevelRepository, never()).save(any(EventLevel.class));
    }

    @Test
    @DisplayName("이벤트 레벨 수정")
    void updateEventLevel() {
        EventLevel eventLevel = new EventLevel("LOW", "기존 설명", 1);

        when(eventLevelRepository.findById(anyString())).thenReturn(Optional.of(eventLevel));

        eventLevelService.updateEventLevel(new EventLevelRequest("LOW", "수정된 설명", 1));

        verify(eventLevelRepository, times(1)).findById(anyString());
        verify(eventLevelRepository, times(1)).save(any(EventLevel.class));
    }

    @Test
    @DisplayName("이벤트 레벨 수정 - 존재하지 않음")
    void updateEventLevel_exception() {
        EventLevelRequest eventLevelRequest = new EventLevelRequest("LOW", "수정된 설명", 1);
        when(eventLevelRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                eventLevelService.updateEventLevel(eventLevelRequest));

        verify(eventLevelRepository, times(1)).findById(anyString());
        verify(eventLevelRepository, never()).save(any(EventLevel.class));
    }

    @Test
    @DisplayName("이벤트 레벨 삭제")
    void deleteEventLevel() {
        when(eventLevelRepository.existsById(anyString())).thenReturn(true);

        eventLevelService.deleteEventLevel("LOW");

        verify(eventLevelRepository, times(1)).existsById(anyString());
        verify(eventLevelRepository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("이벤트 레벨 삭제 - 존재하지 않음")
    void deleteEventLevel_exception() {
        when(eventLevelRepository.existsById(anyString())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> eventLevelService.deleteEventLevel("UNKNOWN"));

        verify(eventLevelRepository, times(1)).existsById(anyString());
        verify(eventLevelRepository, never()).deleteById(anyString());
    }
}
