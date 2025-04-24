package com.nhnacademy.image.service;

import com.nhnacademy.image.dto.ImageResponse;

public interface ImageService {
    void createImage(String userEmail, String imagePath);

    void updateImage(String userEmail, String imagePath);

    void deleteImage(String userEmail);

    ImageResponse getImage(String userEmail);
}
