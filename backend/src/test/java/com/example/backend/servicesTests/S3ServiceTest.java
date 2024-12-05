package com.example.backend.servicesTests;

import com.example.backend.services.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set default values for properties
        s3Service.setBucketName("test-bucket");
    }


    @Test
    void testGenerateImageUrls() {
        // Given
        List<String> imageKeys = List.of("image1.jpg", "image2.jpg");
        String bucketName = "test-bucket";
        String endpointUrl = "https://s3.amazonaws.com";
        s3Service.setBucketName(bucketName);
        ReflectionTestUtils.setField(s3Service, "endpointUrl", endpointUrl);

        // When
        List<String> urls = s3Service.generateImageUrls(imageKeys);

        // Then
        assertEquals(2, urls.size());
        assertEquals("https://s3.amazonaws.com/test-bucket/image1.jpg", urls.get(0));
        assertEquals("https://s3.amazonaws.com/test-bucket/image2.jpg", urls.get(1));
    }
}
