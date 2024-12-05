package com.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Išjungiame CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register",
                                        "/auth/login",
                                        "/api/items/add",
                                        "/api/s3/upload",
                                        "/api/items/analyze",
                                        "/api/items/userads",
                                        "/api/items/userads-with-urls",
                                        "/api/items/{adId}",
                                "/api/categories/prohibited",
                                "/api/categories/allowed",
                                "/api/categories/prohibited/{category}",
                                "/api/categories/allowed/{category}",
                                "/prohibited/{category}/remove-label",
                                "/allowed/{category}/remove-label"

                                ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .cors(withDefaults()); // Integruojame CORS konfigūraciją

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Nurodome React kilmę
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Leidžiame visas antraštes
        configuration.setAllowCredentials(true); // Leidžiame autentifikuotas užklausas

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source; // `UrlBasedCorsConfigurationSource` paveldi iš `CorsConfigurationSource`
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
