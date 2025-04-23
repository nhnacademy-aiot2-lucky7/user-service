package com.nhnacademy.image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageNo;

    @Column(nullable = false, length = 50)
    @Comment("이미지 경로")
    private String imagePath;

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }
}