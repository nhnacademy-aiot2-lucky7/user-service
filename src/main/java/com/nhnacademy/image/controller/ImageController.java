package com.nhnacademy.image.controller;

import com.nhnacademy.image.dto.ImageResponse;
import com.nhnacademy.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    /**
     * 사용자의 프로필 이미지를 조회합니다.
     *
     * @param userEmail 사용자 이메일
     * @return 프로필 이미지 경로
     */
    @GetMapping("/{userEmail}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable String userEmail) {
        ImageResponse imageResponse = imageService.getImage(userEmail);

        return ResponseEntity
                .ok(imageResponse);
    }

    /**
     * 사용자의 프로필 이미지를 등록합니다.
     *
     * @param userEmail 사용자 이메일
     * @param imagePath 이미지 경로
     * @return 201 Created
     */
    @PostMapping
    public ResponseEntity<Void> createImage(@RequestParam String userEmail, @RequestParam String imagePath) {
        imageService.createImage(userEmail, imagePath);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * 사용자의 프로필 이미지를 수정합니다.
     *
     * @param userEmail 사용자 이메일
     * @param imagePath 수정할 이미지 경로
     * @return 204 No Content
     */
    @PutMapping("/{user-email}")
    @HasRole(value={"admin"})
    public ResponseEntity<Void> updateImage(@PathVariable("user-email") String userEmail, @RequestParam String imagePath) {
        imageService.updateImage(userEmail, imagePath);

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * 사용자의 프로필 이미지를 삭제합니다.
     *
     * @param userEmail 사용자 이메일
     * @return 204 No Content
     */
    @DeleteMapping("/{userEmail}")
    public ResponseEntity<Void> deleteImage(@PathVariable String userEmail) {
        imageService.deleteImage(userEmail);

        return ResponseEntity
                .noContent()
                .build();
    }
}
