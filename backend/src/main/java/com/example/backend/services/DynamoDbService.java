package com.example.backend.services;

import com.example.backend.models.Ad;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Service
public class DynamoDbService {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public DynamoDbService(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    private DynamoDbTable<Ad> getAdTable() {
        return dynamoDbEnhancedClient.table("Ads", TableSchema.fromBean(Ad.class));
    }

    // Metodas pridėti skelbimą į DynamoDB
    public void saveAd(Ad ad) {
        getAdTable().putItem(ad);
    }

    // Metodas gauti skelbimus pagal vartotojo ID
    public List<Ad> getAdsByUserId(String userId) {
        return getAdTable()
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId))))
                .items()
                .stream()
                .toList();
    }

    // Metodas gauti skelbimą pagal jo ID
    public Ad getAdById(String adId) {
        return getAdTable().getItem(r -> r.key(k -> k.partitionValue(adId)));
    }

    // Metodas ištrinti skelbimą pagal jo ID
    public void deleteAd(String adId) {
        getAdTable().deleteItem(r -> r.key(k -> k.partitionValue(adId)));
    }
}
