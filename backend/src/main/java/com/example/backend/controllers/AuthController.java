package com.example.backend.controllers;

import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> payload) {
        // Iš payload paimame username ir password
        String username = payload.get("username");
        String password = payload.get("password");

        // Tikriname, ar pateikti username ir password nėra tušti
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is missing");
        }

        // Registruojame naudotoją su užšifruotu slaptažodžiu
        userService.registerUser(username, password);

        return ResponseEntity.ok("Registracija sėkminga!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username or password is missing"));
        }

        boolean authenticated = userService.authenticateUser(username, password);

        if (authenticated) {
            try {
                // Naudojame UserService metodą vartotojo ID gavimui
                String userId = userService.getUserIDByUsername(username);

                // Grąžiname JSON objektą su vartotojo informacija
                Map<String, Object> response = Map.of(
                        "userId", userId,
                        "username", username
                );
                return ResponseEntity.ok(response); // Čia grąžiname Map kaip JSON
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong. Please try again."));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password."));
    }

}


