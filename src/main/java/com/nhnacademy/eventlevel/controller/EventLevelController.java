package com.nhnacademy.eventlevel.controller;

import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.service.EventLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/event-levels")
@RequiredArgsConstructor
public class EventLevelController {
    private final EventLevelService eventLevelService;

    @GetMapping
    public ResponseEntity<List<EventLevelResponse>> getAllEventLevel() {
        return ResponseEntity
                .ok(eventLevelService.getAllEventLevel());
    }

    @GetMapping("/{levelName}")
    public ResponseEntity<EventLevelResponse> getEventLevel(@PathVariable String levelName) {
        return ResponseEntity
                .ok(eventLevelService.getEventLevelByLevelName(levelName));
    }
}
