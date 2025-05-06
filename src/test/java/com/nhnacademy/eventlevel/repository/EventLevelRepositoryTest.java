package com.nhnacademy.eventlevel.repository;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class EventLevelRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EventLevelRepository eventLevelRepository;

    @BeforeEach
    void setUp() {
        eventLevelRepository.save(new EventLevel("INFO", "일반 정보 레벨"));
        eventLevelRepository.save(new EventLevel("WARN", "경고 레벨"));
        eventLevelRepository.save(new EventLevel("ERROR", "에러 레벨"));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("모든 이벤트 레벨 조회")
    void findAllEventLevel() {
        List<EventLevelResponse> eventLevels = eventLevelRepository.findAllEventLevel()
                .orElse(List.of());

        assertEquals(3, eventLevels.size());
    }

    @Test
    @DisplayName("레벨명으로 이벤트 레벨 조회")
    void findEventLevelByLevelName() {
        EventLevelResponse eventLevel = eventLevelRepository.findEventLevelByLevelName("WARN")
                .orElseThrow(() -> new NotFoundException("levelName is null"));

        assertEquals("WARN", eventLevel.getLevelName());
        assertEquals("경고 레벨", eventLevel.getLevelDetails());
    }
}
