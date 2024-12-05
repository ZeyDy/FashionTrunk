package com.example.backend.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CategoryService {

    // Dinaminės draudžiamų kategorijų lentelės
    private final Map<String, Set<String>> prohibitedCategoryLabels = new ConcurrentHashMap<>();

    // Dinaminės leistinų kategorijų lentelės
    private final Map<String, Set<String>> allowedCategoryLabels = new ConcurrentHashMap<>();

    public CategoryService() {
        // Pradiniai duomenys (draudžiamos kategorijos)
        prohibitedCategoryLabels.put("Food products", new HashSet<>(Set.of(
                "Food", "Snack", "Drink", "Beverage", "Meat", "Vegetable", "Fruit", "Candy", "Bread", "Cake"
        )));
        prohibitedCategoryLabels.put("Sports equipment", new HashSet<>(Set.of(
                "Ball", "Bat", "Racket", "Sport Gloves", "Helmet", "Skis", "Snowboard", "Treadmill", "Dumbbell", "Hockey Stick"
        )));
        prohibitedCategoryLabels.put("Tobacco products", new HashSet<>(Set.of(
                "Cigarette", "Cigar", "Tobacco", "Vape", "Hookah", "Nicotine", "Smoking", "Ashtray", "Pipe", "Chewing Tobacco"
        )));
        prohibitedCategoryLabels.put("Cleaning supplies", new HashSet<>(Set.of(
                "Detergent", "Soap", "Cleaner", "Bleach", "Wipes", "Scrub", "Disinfectant", "Polish", "Brush", "Mop"
        )));
        prohibitedCategoryLabels.put("Weapons and armory", new HashSet<>(Set.of(
                "Gun", "Rifle", "Weapon", "Firearm", "Knife", "Explosive", "Ammunition", "Machine Gun", "Bomb", "Crossbow"
        )));
        prohibitedCategoryLabels.put("Vehicles and automotive parts", new HashSet<>(Set.of(
                "Car", "Truck", "Motorcycle", "Bicycle", "Scooter", "Van", "Bus", "Trailer", "Tires", "Engine"
        )));
        prohibitedCategoryLabels.put("Natural fur products", new HashSet<>(Set.of(
                "Fur", "Pelt", "Animal Skin", "Natural Fur"
        )));

        // Pradiniai duomenys (leistinos kategorijos)
        allowedCategoryLabels.put("Fashion accessories", new HashSet<>(Set.of(
                "Accessory", "Hat", "Scarf", "Belt", "Watch", "Glasses", "Jewelry", "Ring", "Bracelet", "Necklace", "Gloves"
        )));
        allowedCategoryLabels.put("Clothes", new HashSet<>(Set.of(
                "Shirt", "Pants", "Jacket", "Dress", "Coat", "Suit", "Blouse", "Sweater", "T-Shirt", "Skirt"
        )));
        allowedCategoryLabels.put("Footwear", new HashSet<>(Set.of(
                "Shoes", "Boots", "Sneakers", "Sandals", "Slippers", "Heels", "Loafers", "Moccasins", "Running Shoes", "Flip-Flops"
        )));
        allowedCategoryLabels.put("Cosmetics", new HashSet<>(Set.of(
                "Makeup", "Lipstick", "Foundation", "Cream", "Perfume", "Lotion", "Mascara", "Eyeliner", "Powder", "Blush"
        )));
        allowedCategoryLabels.put("Children toys", new HashSet<>(Set.of(
                "Toy", "Doll", "Puzzle", "Blocks", "Teddy Bear", "Car Toy", "Ball", "Figure", "Playset", "Lego"
        )));
        allowedCategoryLabels.put("Tech accessories", new HashSet<>(Set.of(
                "Phone", "Laptop", "Tablet", "Headphones", "Charger", "USB Cable", "Keyboard", "Mouse", "Smartwatch", "Camera"
        )));
        allowedCategoryLabels.put("Pet care products", new HashSet<>(Set.of(
                "Pet", "Dog", "Cat", "Leash", "Collar", "Bowl", "Pet Toy", "Pet Food", "Cage", "Brush"
        )));
    }

    // ----------------------
    // Metodai darbui su draudžiamomis kategorijomis
    // ----------------------

    /**
     * Gauti visas draudžiamas kategorijas.
     *
     * @return Draudžiamų kategorijų lentelė.
     */
    public Map<String, Set<String>> getProhibitedCategories() {
        return prohibitedCategoryLabels;
    }

    /**
     * Pridėti naują draudžiamą kategoriją arba papildyti jos žymes.
     *
     * @param category Kategorijos pavadinimas.
     * @param labels   Žymių sąrašas.
     */
    public void addProhibitedCategory(String category, Set<String> labels) {
        prohibitedCategoryLabels.computeIfAbsent(category, k -> new HashSet<>()).addAll(labels);
    }

    /**
     * Pašalinti draudžiamą kategoriją.
     *
     * @param category Kategorijos pavadinimas.
     */
    public void removeProhibitedCategory(String category) {
        prohibitedCategoryLabels.remove(category);
    }

    // ----------------------
    // Metodai darbui su leistinomis kategorijomis
    // ----------------------

    /**
     * Gauti visas leidžiamas kategorijas.
     *
     * @return Leidžiamų kategorijų lentelė.
     */
    public Map<String, Set<String>> getAllowedCategories() {
        return allowedCategoryLabels;
    }

    /**
     * Pridėti naują leistiną kategoriją arba papildyti jos žymes.
     *
     * @param category Kategorijos pavadinimas.
     * @param labels   Žymių sąrašas.
     */
    public void addAllowedCategory(String category, Set<String> labels) {
        allowedCategoryLabels.computeIfAbsent(category, k -> new HashSet<>()).addAll(labels);
    }

    /**
     * Pašalinti leistiną kategoriją.
     *
     * @param category Kategorijos pavadinimas.
     */
    public void removeAllowedCategory(String category) {
        allowedCategoryLabels.remove(category);
    }

    // ----------------------
    // Bendra kategorijų logika
    // ----------------------

    /**
     * Priskiria kategoriją pagal AI žymes.
     *
     * @param labels AI atpažintos žymės
     * @return Pirmoji leistina kategorija arba "Prohibited", jei randama draudžiama žyma.
     */
    public String assignCategory(List<String> labels) {
        // Tikriname, ar yra draudžiamų kategorijų
        for (Map.Entry<String, Set<String>> entry : prohibitedCategoryLabels.entrySet()) {
            String category = entry.getKey();
            Set<String> prohibitedLabels = entry.getValue();

            for (String label : labels) {
                if (prohibitedLabels.contains(label)) {
                    return "Prohibited"; // Jei randama bent viena draudžiama žyma
                }
            }
        }

        // Tikriname leistinas kategorijas
        for (Map.Entry<String, Set<String>> entry : allowedCategoryLabels.entrySet()) {
            String category = entry.getKey();
            Set<String> allowedLabels = entry.getValue();

            for (String label : labels) {
                if (allowedLabels.contains(label)) {
                    return category; // Jei randama bent viena leistina žyma
                }
            }
        }

        // Jei neatpažinta kategorija
        return "Uncategorized";
    }

    /**
     * Patikrina, ar kategorija yra leidžiamų kategorijų sąraše.
     *
     * @param category Kategorija, kurią reikia patikrinti.
     * @return true, jei kategorija leidžiama; false, jei neleidžiama.
     */
    public boolean isAllowedCategory(String category) {
        return allowedCategoryLabels.containsKey(category);
    }

    /**
     * Pašalina vieną žymę iš draudžiamos kategorijos.
     *
     * @param category Kategorijos pavadinimas.
     * @param label    Žymė, kurią reikia pašalinti.
     */
    public void removeLabelFromProhibitedCategory(String category, String label) {
        Set<String> labels = prohibitedCategoryLabels.get(category);
        if (labels != null) {
            labels.remove(label);
        }
    }

    /**
     * Pašalina vieną žymę iš leidžiamos kategorijos.
     *
     * @param category Kategorijos pavadinimas.
     * @param label    Žymė, kurią reikia pašalinti.
     */
    public void removeLabelFromAllowedCategory(String category, String label) {
        Set<String> labels = allowedCategoryLabels.get(category);
        if (labels != null) {
            labels.remove(label);
        }
    }
}
