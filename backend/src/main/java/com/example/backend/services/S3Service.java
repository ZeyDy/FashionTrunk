package com.example.backend.services;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    @Setter
    @Getter
    @Value("${aws.bucketName}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Value("${aws.s3.endpoint-url}")
    private String endpointUrl;

    // Generuoja pilnus nuotraukų URL iš DynamoDB įrašų
    public List<String> generateImageUrls(List<String> imageKeys) {
        return imageKeys.stream()
                .map(key -> endpointUrl + "/" + bucketName + "/" + key)
                .collect(Collectors.toList());
    }




    /**
     * Įkelia failą į S3 ir gražina failo raktą.
     *
     * @param file Multipart failas, kurį reikia įkelti.
     * @param key  Failo unikalus raktas S3 saugykloje.
     * @return Raktas, nurodantis failo vietą S3.
     */
    public String uploadFile(MultipartFile file, String key) {
        logger.info("Pradedamas failo įkėlimas į S3. Failo pavadinimas: {}, raktas: {}", file.getOriginalFilename(), key);

        try (InputStream inputStream = file.getInputStream()) {
            // Sukuriame ir siunčiame užklausą įkelti failą
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );

            logger.info("Failas sėkmingai įkeltas į S3. Raktas: {}", key);
            return key; // Grąžiname failo raktą
        } catch (Exception e) {
            logger.error("Klaida įkeliant failą į S3: {}", e.getMessage());
            throw new RuntimeException("Failo įkėlimas į S3 nepavyko", e);
        }
    }

    /**
     * Pašalina failą iš S3 pagal pateiktą raktą.
     *
     * @param key Failo raktas S3 saugykloje.
     */
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key).build());
            logger.info("Failas sėkmingai pašalintas iš S3. Raktas: {}", key);
        } catch (Exception e) {
            logger.error("Klaida šalinant failą iš S3: {}", e.getMessage());
            throw new RuntimeException("Failo pašalinimas iš S3 nepavyko", e);
        }
    }


}
