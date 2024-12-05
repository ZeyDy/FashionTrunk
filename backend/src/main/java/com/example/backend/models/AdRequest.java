package com.example.backend.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdRequest {
    private String userId;
    private String title;
    private String description;
    private double price;
    private String category;
    private List<String> imageUrls;
}
