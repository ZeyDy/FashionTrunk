package com.example.backend.servicesTests;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testRegisterUser() {
        // Given
        String username = "testUser";
        String rawPassword = "testPassword";

        // When
        userService.registerUser(username, rawPassword);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(username, savedUser.getUsername());
        assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()));
        assertNotNull(savedUser.getUserId());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        String username = "testUser";
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.authenticateUser(username, rawPassword);

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testAuthenticateUser_Failure() {
        // Given
        String username = "testUser";
        String rawPassword = "testPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        boolean result = userService.authenticateUser(username, rawPassword);

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testGetUserIDByUsername_Success() {
        // Given
        String username = "testUser";
        String userId = UUID.randomUUID().toString();

        QueryResponse mockResponse = mock(QueryResponse.class);
        when(mockResponse.items()).thenReturn(
                List.of(Map.of("userId", AttributeValue.builder().s(userId).build()))
        );

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // When
        String result = userService.getUserIDByUsername(username);

        // Then
        assertEquals(userId, result);
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(dynamoDbClient).query(captor.capture());

        QueryRequest capturedRequest = captor.getValue();
        assertEquals("Users", capturedRequest.tableName());
        assertEquals("UsernameIndex", capturedRequest.indexName());
        assertEquals(":username", capturedRequest.expressionAttributeValues().keySet().iterator().next());
    }

    @Test
    void testGetUserIDByUsername_UserNotFound() {
        // Given
        String username = "testUser";

        QueryResponse mockResponse = mock(QueryResponse.class);
        when(mockResponse.items()).thenReturn(List.of());

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserIDByUsername(username);
        });

        assertEquals("Vartotojas su username testUser nerastas.", exception.getMessage());
    }
}
