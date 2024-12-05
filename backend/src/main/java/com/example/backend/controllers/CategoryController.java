package com.example.backend.controllers;

import com.example.backend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/prohibited")
    public Map<String, Set<String>> getProhibitedCategories() {
        return categoryService.getProhibitedCategories();
    }

    @GetMapping("/allowed")
    public Map<String, Set<String>> getAllowedCategories() {
        return categoryService.getAllowedCategories();
    }

    @PostMapping("/prohibited")
    public void addProhibitedCategory(@RequestBody Map<String, Set<String>> category) {
        category.forEach(categoryService::addProhibitedCategory);
    }

    @PostMapping("/allowed")
    public void addAllowedCategory(@RequestBody Map<String, Set<String>> category) {
        category.forEach(categoryService::addAllowedCategory);
    }

    @DeleteMapping("/prohibited/{category}")
    public void removeProhibitedCategory(@PathVariable String category) {
        categoryService.removeProhibitedCategory(category);
    }

    @DeleteMapping("/allowed/{category}")
    public void removeAllowedCategory(@PathVariable String category) {
        categoryService.removeAllowedCategory(category);
    }

    /**
     * Pašalina vieną žymę iš konkrečios draudžiamos kategorijos.
     */
    @DeleteMapping("/prohibited/{category}/remove-label")
    public void removeLabelFromProhibitedCategory(
            @PathVariable String category,
            @RequestParam String label) {
        categoryService.removeLabelFromProhibitedCategory(category, label);
    }

    /**
     * Pašalina vieną žymę iš konkrečios leidžiamos kategorijos.
     */
    @DeleteMapping("/allowed/{category}/remove-label")
    public void removeLabelFromAllowedCategory(
            @PathVariable String category,
            @RequestParam String label) {
        categoryService.removeLabelFromAllowedCategory(category, label);
    }
}
