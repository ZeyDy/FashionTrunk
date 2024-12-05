package com.example.backend.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private Long id;

    private String name; // Pvz.: "Fashion accessories", "Weapons"

    private boolean allowed; // True: leidžiama, False: draudžiama
}
