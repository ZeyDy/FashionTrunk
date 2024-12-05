package com.example.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RekognitionService {

    private static final Logger logger = LoggerFactory.getLogger(RekognitionService.class);

    private final RekognitionClient rekognitionClient;

    public RekognitionService(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    public List<String> analyzeImage(String bucketName, String imageName) {
        logger.info("Analyzing image. Bucket: {}, Image Name: {}", bucketName, imageName);

        S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(imageName)
                .build();

        Image image = Image.builder().s3Object(s3Object).build();

        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(image)
                .maxLabels(10)
                .minConfidence(70F)  // Minimali pasitikėjimo riba
                .build();

        try {
            logger.debug("Sending Rekognition request: {}", detectLabelsRequest);

            DetectLabelsResponse detectLabelsResponse = rekognitionClient.detectLabels(detectLabelsRequest);

            logger.info("Rekognition Response: {}", detectLabelsResponse);

            return detectLabelsResponse.labels().stream()
                    .map(Label::name)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error during Rekognition request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze image with Rekognition", e);
        }
    }
}
