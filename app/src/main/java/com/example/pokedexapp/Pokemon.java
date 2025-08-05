package com.example.pokedexapp;

public class Pokemon {
    private String name;
    private int imageResId;

    public Pokemon(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}
