package com.example.pokedexapp;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    private String name;
    private String imageUrl;
    private List<String> types;    // e.g., ["fire","flying"]
    private double weightKg;       // PokeAPI weight (hectograms) / 10.0
    private double heightM;        // PokeAPI height (decimetres) / 10.0
    private String generation;     // "Gen I" .. "Gen IX"

    // Full constructor (used for filtering)
    public Pokemon(String name, String imageUrl, List<String> types,
                   double weightKg, double heightM, String generation) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.types = (types != null) ? types : new ArrayList<>();
        this.weightKg = weightKg;
        this.heightM = heightM;
        this.generation = (generation != null) ? generation : "Gen ?";
    }

    // Convenience constructor used by Favorites (name + image only)
    public Pokemon(String name, String imageUrl) {
        this(name, imageUrl, new ArrayList<>(), 0.0, 0.0, "Gen ?");
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getTypes() { return types; }
    public double getWeightKg() { return weightKg; }
    public double getHeightM() { return heightM; }
    public String getGeneration() { return generation; }
}
