package com.example.pokedexapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokemonDetail {

    // Champs publics (compat avec DetailFragment qui fait detail.name, detail.id, etc.)
    public final int id;             // PokeAPI id
    public final String name;        // lowercase depuis l'API
    public final int height;         // decimetres (dm)
    public final int weight;         // hectograms (hg)
    public final List<String> types; // ex: ["fire","flying"]
    public final String imageUrl;    // peut être null

    public PokemonDetail(int id, String name, int height, int weight, List<String> types, String imageUrl) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        // immuable vers l'extérieur
        this.types = types != null ? Collections.unmodifiableList(new ArrayList<>(types)) : Collections.emptyList();
        this.imageUrl = imageUrl;
    }

    // Getters (compat si ailleurs tu fais detail.getId(), etc.)
    public int getId() { return id; }
    public String getName() { return name; }
    /** dm */ public int getHeight() { return height; }
    /** hg */ public int getWeight() { return weight; }
    public List<String> getTypes() { return types; }
    public String getImageUrl() { return imageUrl; }
}
