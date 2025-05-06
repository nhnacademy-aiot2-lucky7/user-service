package com.nhnacademy.eventlevel.controller;

import com.common.AESUtil;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.service.EventLevelService;
import com.nhnacademy.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventLevelController.class)
@AutoConfigureMockMvc
class EventLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventLevelService eventLevelService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AESUtil aesUtil;

    @Test
    @DisplayName("모든 이벤트 레벨 조회 - 200 반환")
    void getAllEventLevel_200() throws Exception {
        List<EventLevelResponse> responses = IntStream.range(1, 6)
                .mapToObj(i -> new EventLevelResponse("LEVEL" + i, "설명" + i))
                .toList();

        when(eventLevelService.getAllEventLevel()).thenReturn(responses);

        mockMvc.perform(get("/users/event-levels")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(eventLevelService, times(1)).getAllEventLevel();
    }

    @Test
    @DisplayName("이벤트 레벨명으로 조회 - 200 반환")
    void getEventLevelByLevelName_200() throws Exception {
        EventLevelResponse response = new EventLevelResponse("CRITICAL", "치명적 오류");

        when(eventLevelService.getEventLevelByLevelName("CRITICAL")).thenReturn(response);

        mockMvc.perform(get("/users/event-levels/CRITICAL")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelName").value("CRITICAL"))
                .andExpect(jsonPath("$.levelDetails").value("치명적 오류"));

        verify(eventLevelService, times(1)).getEventLevelByLevelName("CRITICAL");
    }
}
