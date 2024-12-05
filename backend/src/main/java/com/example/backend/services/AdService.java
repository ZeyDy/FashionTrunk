package com.example.backend.services;

import com.example.backend.models.Ad;
import com.example.backend.models.AdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Instant;
import java.util.*;

@Service
public class AdService {

    private static final Logger logger = LoggerFactory.getLogger(AdService.class);

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbService dynamoDbService;
    private final RekognitionService rekognitionService;
    private final S3Service s3Service;
    private final CategoryService categoryService; // Pridedame priklausomybę

    public AdService(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                     DynamoDbService dynamoDbService,
                     RekognitionService rekognitionService,
                     S3Service s3Service,
                     CategoryService categoryService) { // Pridedame į konstruktor
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoDbService = dynamoDbService;
        this.rekognitionService = rekognitionService;
        this.s3Service = s3Service;
        this.categoryService = categoryService; // Priskiriame priklausomybę
    }

    private DynamoDbTable<Ad> getAdTable() {
        return dynamoDbEnhancedClient.table("Ads", TableSchema.fromBean(Ad.class));
    }

    // senas metodas
    public List<Ad> getAdsByUserId(String userId) {
        try {
            DynamoDbTable<Ad> adTable = getAdTable();

            // Pasiekiame antrinį indeksą
            DynamoDbIndex<Ad> userIdIndex = adTable.index("UserIdIndex");

            // Užklausa pagal userId
            List<Ad> userAds = new ArrayList<>();
            SdkIterable<Page<Ad>> queryResults = userIdIndex.query(r ->
                    r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId)))
            );

            // Surenkame rezultatus
            for (Page<Ad> page : queryResults) {
                userAds.addAll(page.items());
            }

            return userAds;
        } catch (Exception e) {
            logger.error("Nepavyko gauti skelbimų pagal userId: {}", userId, e);
            throw new RuntimeException("Klaida gaunant skelbimus iš DynamoDB", e);
        }
    }



    // Pridėti naują skelbimą
    public void addAd(AdRequest adRequest) {
        // Konvertuojame AdRequest į Ad
        Ad ad = new Ad();
        ad.setAdId(UUID.randomUUID().toString());
        ad.setUserId(adRequest.getUserId());
        ad.setImageUrls(adRequest.getImageUrls());
        ad.setTitle(adRequest.getTitle());
        ad.setDescription(adRequest.getDescription());
        ad.setPrice(adRequest.getPrice());
        ad.setCategory(adRequest.getCategory());
        ad.setStatus("pending");
        ad.setCreatedAt(Instant.now().toString());

        try {
            logger.info("Pridedamas naujas skelbimas: {}", ad);
            getAdTable().putItem(ad);
        } catch (Exception e) {
            logger.error("Klaida pridedant skelbimą į DynamoDB", e);
            throw new RuntimeException("Nepavyko išsaugoti skelbimo į DynamoDB", e);
        }
    }

    // naujas po backup Gauname skelbimus kartu su nuotraukų URL
    public List<Ad> getAdsByUserIdWithImages(String userId) {
        try {
            // Gauname skelbimus iš DynamoDBS
            List<Ad> ads = getAdsByUserId(userId);

            // Pridėkime tikrus nuotraukų URL prie kiekvieno skelbimo
            for (Ad ad : ads) {
                if (ad.getImageUrls() != null && !ad.getImageUrls().isEmpty()) {
                    // Naudojame S3Service nuotraukų URL generavimui
                    List<String> fullImageUrls = s3Service.generateImageUrls(ad.getImageUrls());
                    ad.setImageUrls(fullImageUrls);
                }
            }

            return ads;
        } catch (Exception e) {
            logger.error("Nepavyko gauti skelbimų su nuotraukomis naudotojui: {}", userId, e);
            throw new RuntimeException("Klaida gaunant skelbimus su nuotraukomis", e);
        }
    }



    // Atnaujinti skelbimo statusą
//    public void updateItemStatus(String itemId, String status) {
//        logger.info("Atnaujinamas skelbimo statusas, itemId: {}, naujas statusas: {}", itemId, status);
//        try {
//            DynamoDbTable<Ad> table = getAdTable();
//            Ad ad = table.getItem(r -> r.key(k -> k.partitionValue(itemId)));
//            if (ad != null) {
//                ad.setStatus(status);
//                table.updateItem(ad);
//                logger.info("Skelbimo statusas sėkmingai atnaujintas, itemId: {}", itemId);
//            } else {
//                logger.warn("Skelbimas su itemId: {} nerastas", itemId);
//                throw new RuntimeException("Skelbimas nerastas");
//            }
//        } catch (Exception e) {
//            logger.error("Klaida atnaujinant skelbimo statusą, itemId: {}", itemId, e);
//            throw new RuntimeException("Nepavyko atnaujinti skelbimo statuso", e);
//        }
//    }
    public void updateAd(String adId, AdRequest adRequest, List<MultipartFile> files) {
        // Gauname esamą skelbimą iš DynamoDB
        DynamoDbTable<Ad> adTable = getAdTable();
        Ad existingAd = adTable.getItem(r -> r.key(k -> k.partitionValue(adId)));

        if (existingAd == null) {
            throw new RuntimeException("Skelbimas nerastas su ID: " + adId);
        }

        // Jei yra naujų nuotraukų, atliekame analizę ir įkeliame jas į S3
        List<String> newImageUrls = existingAd.getImageUrls();
        if (files != null && !files.isEmpty()) {
            newImageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
                s3Service.uploadFile(file, key);

                // Atliekame AI analizę
                List<String> labels = rekognitionService.analyzeImage(s3Service.getBucketName(), key);
                String category = categoryService.assignCategory(labels);

                if (!categoryService.isAllowedCategory(category)) {
                    throw new IllegalArgumentException("Vienas iš naujų vaizdų neatitinka leistinų kategorijų.");
                }

                newImageUrls.add(key);
            }
        }

        // Atnaujiname skelbimo laukus
        existingAd.setTitle(adRequest.getTitle());
        existingAd.setDescription(adRequest.getDescription());
        existingAd.setPrice(adRequest.getPrice());
        existingAd.setImageUrls(newImageUrls);

        // Saugojame atnaujintą skelbimą į DynamoDB
        adTable.updateItem(existingAd);
    }


    public void processAdImage(String bucketName, String imageName, Ad ad) {
        // Analizuoti vaizdą
        List<String> labels = rekognitionService.analyzeImage(bucketName, imageName);

        // Tikriname kategorijas
        String matchedCategory = labels.stream()
                .filter(label -> isAllowedCategory(label)) // Patikrink, ar kategorija leidžiama
                .findFirst()
                .orElse("Not Allowed");

        if (matchedCategory.equals("Not Allowed")) {
            throw new IllegalArgumentException("Vaizdas neatitinka leistinų kategorijų.");
        }

        // Atnaujinti skelbimo informaciją
        ad.setCategory(matchedCategory);
        ad.setStatus("Approved");

        // Saugojame į DynamoDB
        dynamoDbService.saveAd(ad);
    }

    private boolean isAllowedCategory(String label) {
        // Leidžiamų kategorijų sąrašas
        List<String> allowedCategories = List.of("Clothing", "Footwear", "Fashion Accessories", "Cosmetics");
        return allowedCategories.contains(label);
    }

    public Map<String, List<String>> analyzeAdImages(String userId) {
        List<Ad> ads = getAdsByUserId(userId); // Gauname visus vartotojo skelbimus
        Map<String, List<String>> analysisResults = new HashMap<>();

        for (Ad ad : ads) {
            if (ad.getImageUrls() != null && !ad.getImageUrls().isEmpty()) {
                for (String imageUrl : ad.getImageUrls()) {
                    String bucketName = extractBucketName(imageUrl);
                    String imageName = extractImageName(imageUrl);
                    List<String> labels = rekognitionService.analyzeImage(bucketName, imageName);
                    analysisResults.put(imageUrl, labels);
                }
            }
        }

        return analysisResults;
    }

    private String extractBucketName(String imageUrl) {
        // Ištraukiame S3 bucket pavadinimą iš URL, pvz., "my-fashion-trunk-images"
        return "my-fashion-trunk-images"; // Tai galite dinamiškai implementuoti pagal poreikį
    }

    private String extractImageName(String imageUrl) {
        // Ištraukiame failo pavadinimą iš URL, pvz., "my-test-image.jpg"
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    public void deleteAd(String adId) {
        DynamoDbTable<Ad> adTable = getAdTable();
        Ad existingAd = adTable.getItem(r -> r.key(k -> k.partitionValue(adId)));

        if (existingAd == null) {
            throw new RuntimeException("Skelbimas nerastas su ID: " + adId);
        }

        // Galite taip pat ištrinti susijusias nuotraukas iš S3, jei reikia
        for (String imageUrl : existingAd.getImageUrls()) {
            String imageName = extractImageName(imageUrl);
            s3Service.deleteFile(imageName);
        }

        // Ištriname skelbimą iš DynamoDB
        adTable.deleteItem(r -> r.key(k -> k.partitionValue(adId)));
    }
}
