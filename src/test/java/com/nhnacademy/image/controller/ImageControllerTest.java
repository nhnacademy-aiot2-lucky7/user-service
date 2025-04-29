package com.nhnacademy.image.controller;

import com.common.AESUtil;
import com.nhnacademy.image.dto.ImageResponse;
import com.nhnacademy.image.service.ImageService;
import com.nhnacademy.user.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc
class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AESUtil aesUtil;

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 200 반환")
    void getImage_200() throws Exception {
        ImageResponse imageResponse = new ImageResponse("images/path");

        when(imageService.getImage(anyString())).thenReturn(imageResponse);

        mockMvc.perform(get("/images/test@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagePath").value("images/path"));

        verify(imageService, times(1)).getImage(anyString());
    }

    @Test
    @DisplayName("사용자 이미지 경로 생성 - 201 반환")
    void createImage_201() throws Exception {
        mockMvc.perform(post("/images")
                        .param("userEmail", "test@email.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(imageService, times(1)).createImage(anyString(), anyString());
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 204 반환")
    void updateImage_204() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "test@email.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());


        verify(imageService, times(1)).updateImage(anyString(), anyString());
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 204 반환")
    void deleteImage_204() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "test@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(imageService, times(1)).deleteImage(anyString());
    }
}