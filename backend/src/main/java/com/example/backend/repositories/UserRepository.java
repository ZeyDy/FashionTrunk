package com.example.backend.repositories;

import com.example.backend.models.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

@Repository
public class UserRepository {

    private final DynamoDbClient dynamoDbClient;

    public UserRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(User user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.builder().s(user.getUserId()).build());
        item.put("username", AttributeValue.builder().s(user.getUsername()).build());
        item.put("password", AttributeValue.builder().s(user.getPassword()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Users") // Pakeiskite į savo lentelės pavadinimą, jei jis kitoks
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public Optional<User> findByUsername(String username) {
        // Log'as prieš užklausą
        System.out.println("Atliekama užklausa su username: " + username);

        // DynamoDB užklausos sudarymas
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")
                .indexName("UsernameIndex") // Naudojamas indeksas
                .keyConditionExpression("username = :username") // Sąlyga
                .expressionAttributeValues(Map.of(
                        ":username", AttributeValue.builder().s(username).build() // Užklausos parametras
                ))
                .build();

        // DynamoDB užklausos vykdymas
        QueryResponse response = dynamoDbClient.query(queryRequest);

        // Log'as po užklausos
        System.out.println("Gautas atsakas: " + response);

        // Tikriname atsakymą
        if (response.hasItems() && !response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().get(0);
            System.out.println("Gautas vartotojo įrašas: " + item);

            // Sukuriame vartotojo objektą iš gauto įrašo
            User user = new User();
            user.setUserId(item.get("userId").s());
            user.setUsername(item.get("username").s());
            user.setPassword(item.get("password") != null ? item.get("password").s() : null);

            return Optional.of(user);
        } else {
            // Jei vartotojas nerastas
            System.out.println("Vartotojas su username '" + username + "' nerastas.");
            return Optional.empty();
        }
    }






}
