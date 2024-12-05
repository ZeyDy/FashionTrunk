package com.example.backend.servicesTests;

import com.example.backend.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService();
    }

    @Test
    void testAddProhibitedCategory() {
        // Pridedame naują kategoriją
        categoryService.addProhibitedCategory("New Prohibited", Set.of("TestLabel"));

        // Tikriname, ar kategorija buvo pridėta
        Map<String, Set<String>> prohibitedCategories = categoryService.getProhibitedCategories();
        assertTrue(prohibitedCategories.containsKey("New Prohibited"));
        assertEquals(1, prohibitedCategories.get("New Prohibited").size());
        assertTrue(prohibitedCategories.get("New Prohibited").contains("TestLabel"));
    }

    @Test
    void testRemoveProhibitedLabel() {
        // Pridedame kategoriją su žymėmis
        categoryService.addProhibitedCategory("Test Category", new HashSet<>(Set.of("Label1", "Label2")));

        // Pašaliname vieną žymę
        categoryService.removeLabelFromProhibitedCategory("Test Category", "Label1");

        // Tikriname, ar žymė buvo pašalinta
        Set<String> labels = categoryService.getProhibitedCategories().get("Test Category");
        assertFalse(labels.contains("Label1"));
        assertTrue(labels.contains("Label2"));
    }

    @Test
    void testAddAllowedCategory() {
        // Pridedame naują leidžiamą kategoriją
        categoryService.addAllowedCategory("New Allowed", Set.of("AllowedLabel"));

        // Tikriname, ar kategorija buvo pridėta
        Map<String, Set<String>> allowedCategories = categoryService.getAllowedCategories();
        assertTrue(allowedCategories.containsKey("New Allowed"));
        assertEquals(1, allowedCategories.get("New Allowed").size());
        assertTrue(allowedCategories.get("New Allowed").contains("AllowedLabel"));
    }

    @Test
    void testRemoveAllowedLabel() {
        // Pridedame kategoriją su žymėmis
        categoryService.addAllowedCategory("Test Allowed", new HashSet<>(Set.of("Label1", "Label2")));

        // Pašaliname vieną žymę
        categoryService.removeLabelFromAllowedCategory("Test Allowed", "Label1");

        // Tikriname, ar žymė buvo pašalinta
        Set<String> labels = categoryService.getAllowedCategories().get("Test Allowed");
        assertFalse(labels.contains("Label1"));
        assertTrue(labels.contains("Label2"));
    }
}
