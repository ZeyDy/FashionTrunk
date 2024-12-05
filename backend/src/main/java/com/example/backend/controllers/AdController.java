package com.example.backend.controllers;

import com.example.backend.models.Ad;
import com.example.backend.models.AdRequest;
import com.example.backend.services.AdService;
import com.example.backend.services.CategoryService;
import com.example.backend.services.RekognitionService;
import com.example.backend.services.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/items")
public class AdController {

    private static final Logger logger = LoggerFactory.getLogger(AdController.class);

    private final AdService adService;
    private final S3Service s3Service;
    private final RekognitionService rekognitionService;
    private final CategoryService categoryService;

    @Autowired
    public AdController(AdService adService, S3Service s3Service, RekognitionService rekognitionService, CategoryService categoryService) {
        this.adService = adService;
        this.s3Service = s3Service;
        this.rekognitionService = rekognitionService;
        this.categoryService = categoryService;
    }


    @GetMapping("/userads")
    public ResponseEntity<List<Ad>> getAdsByUserId(@RequestParam String userId) {
        try {
            // Gauname skelbimus iš AdService
            List<Ad> ads = adService.getAdsByUserIdWithImages(userId);
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            logger.error("Klaida gaunant skelbimus naudotojui su ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    /**
     * Pridėti naują skelbimą su AI kategorijos nustatymu
     */
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<String> addAd(
            @RequestPart("adRequest") String rawAdRequest,
            @RequestPart("files") List<MultipartFile> files) {

        try {
            // Deserializuojame JSON į AdRequest objektą
            ObjectMapper objectMapper = new ObjectMapper();
            AdRequest adRequest = objectMapper.readValue(rawAdRequest, AdRequest.class);

            // Įkeliame failus į S3 ir priskiriame kategorijas
            List<String> imageUrls = new ArrayList<>();
            String overallCategory = null;

            for (MultipartFile file : files) {
                // 1. Įkeliame failą į S3
                String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
                s3Service.uploadFile(file, key);

                // 2. Analizuojame failą su AI ir priskiriame kategoriją
                List<String> labels = rekognitionService.analyzeImage(s3Service.getBucketName(), key);
                String category = categoryService.assignCategory(labels);

                // 3. Nustatome bendrą kategoriją
                if (overallCategory == null) {
                    overallCategory = category;
                }

                // 4. Įtraukiame failo URL
                imageUrls.add(key);

                // Logas kiekvienai nuotraukai
                logger.info("Failas {} priskirtas kategorijai: {}", file.getOriginalFilename(), category);
            }

            // Pridedame vaizdus ir kategoriją prie skelbimo
            adRequest.setImageUrls(imageUrls);
            adRequest.setCategory(overallCategory != null ? overallCategory : "Uncategorized");

            // Išsaugome skelbimą
            adService.addAd(adRequest);

            return ResponseEntity.ok("Skelbimas sėkmingai pridėtas ir automatiškai analizuotas!");

        } catch (Exception e) {
            logger.error("Klaida apdorojant užklausą: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Klaida: " + e.getMessage());
        }
    }

    /**
     * Atnaujinti skelbimo statusą
     */
//    @PutMapping("/{itemId}")
//    public ResponseEntity<String> updateItemStatus(@PathVariable String itemId, @RequestParam String status) {
//        try {
//            adService.updateItemStatus(itemId, status);
//            return ResponseEntity.ok("Statusas atnaujintas.");
//        } catch (Exception e) {
//            logger.error("Klaida atnaujinant skelbimo statusą: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Klaida atnaujinant statusą: " + e.getMessage());
//        }
//    }
    @PutMapping("/{adId}")
    public ResponseEntity<String> updateAd(
            @PathVariable String adId,
            @RequestPart("adRequest") String rawAdRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            // Deserializuojame JSON į AdRequest objektą
            ObjectMapper objectMapper = new ObjectMapper();
            AdRequest adRequest = objectMapper.readValue(rawAdRequest, AdRequest.class);

            // Atnaujiname skelbimą ir atliekame nuotraukų analizę, jei reikia
            adService.updateAd(adId, adRequest, files);

            return ResponseEntity.ok("Skelbimas sėkmingai atnaujintas.");
        } catch (Exception e) {
            logger.error("Klaida atnaujinant skelbimą su ID {}: {}", adId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Klaida: " + e.getMessage());
        }
    }
    /**
     * Analizuoti naudotojo skelbimų nuotraukas
     */
    @GetMapping("/analyze")
    public ResponseEntity<Map<String, List<String>>> analyzeAdImages(@RequestParam String userId) {
        try {
            Map<String, List<String>> results = adService.analyzeAdImages(userId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Klaida analizuojant naudotojo skelbimų nuotraukas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyMap());
        }
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<String> deleteAd(@PathVariable String adId) {
        try {
            adService.deleteAd(adId);
            return ResponseEntity.ok("Skelbimas sėkmingai ištrintas.");
        } catch (Exception e) {
            logger.error("Klaida ištrinant skelbimą su ID {}: {}", adId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Klaida: " + e.getMessage());
        }
    }
}
