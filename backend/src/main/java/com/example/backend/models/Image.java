package com.example.backend.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class Image {

    private Long id;

    private String fileName;
    private String fileUrl; // Nuoroda į S3 arba kitą saugyklą

    private String status; // e.g., "APPROVED", "REJECTED", "PENDING"
    private String detectedLabels; // JSON arba string su AWS Rekognition rezultatais

    private LocalDateTime uploadedAt;

    // Getteriai, Setteriai, Konstruktoriai
}

