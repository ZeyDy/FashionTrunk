package com.example.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class User {

    private String userId; // UUID kaip unikalus ID

    private String username; // Vartotojo vardas

    private String password; // Užšifruotas slaptažodis

    private boolean enabled; // Ar vartotojas aktyvuotas

    private Set<String> roles; // Vartotojo vaidmenys (pvz., ROLE_USER, ROLE_ADMIN)

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }
}
