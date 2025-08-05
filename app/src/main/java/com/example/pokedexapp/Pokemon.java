package com.example.pokedexapp;

public class Pokemon {
    private final String name;
    private final String imageUrl;

    public Pokemon(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
