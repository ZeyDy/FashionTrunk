package com.example.backend.services;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final DynamoDbClient dynamoDbClient;

    public UserService(UserRepository userRepository, DynamoDbClient dynamoDbClient) {
        this.userRepository = userRepository;
        this.dynamoDbClient = dynamoDbClient;
        this.encoder = new BCryptPasswordEncoder();
    }

    public void registerUser(String username, String rawPassword) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString()); // Generuojame unikalų ID
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword)); // Užkoduojame slaptažodį
        userRepository.save(user); // Išsaugome vartotoją
    }


    public boolean authenticateUser(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return false; // Vartotojas nerastas
        }

        User user = userOpt.get();

        // Tikriname slaptažodį
        return encoder.matches(rawPassword, user.getPassword());
    }

    public String getUserIDByUsername(String username) {
        // Užklausa pagal GSI
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users") // Lentelės pavadinimas
                .indexName("UsernameIndex") // Indekso pavadinimas
                .keyConditionExpression("username = :username") // Užklausos sąlyga
                .expressionAttributeValues(Map.of(
                        ":username", AttributeValue.builder().s(username).build()
                )) // Užklausos reikšmė
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);

        if (response.items().isEmpty()) {
            throw new RuntimeException("Vartotojas su username " + username + " nerastas.");
        }

        // Grąžiname userId
        return response.items().get(0).get("userId").s();
    }

}
