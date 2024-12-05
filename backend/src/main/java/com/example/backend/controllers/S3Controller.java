package com.example.backend.controllers;

import com.example.backend.services.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.debug("Gauta užklausa įkelti failą");
        if (file == null) {
            logger.error("Parametras 'file' nerastas užklausoje");
            return ResponseEntity.badRequest().body("Failo nerasta");
        }

        try {
            if (file.isEmpty()) {
                logger.error("Failas yra tuščias");
                return ResponseEntity.badRequest().body("Failas nėra pasirinktas!");
            }

            String key = file.getOriginalFilename();
            String fileUrl = s3Service.uploadFile(file, key);
            logger.debug("Failas įkeltas sėkmingai, URL: {}", fileUrl);

            return ResponseEntity.ok("Failas įkeltas sėkmingai: " + fileUrl);
        } catch (Exception e) {
            logger.error("Klaida įkeliant failą: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Nepavyko įkelti failo: " + e.getMessage());
        }
    }




}
