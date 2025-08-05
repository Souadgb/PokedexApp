package com.example.pokedexapp;

import java.util.List;

public class PokemonDetail {
    public int id;
    public String name;
    public int height;
    public int weight;
    public List<String> types;
    public String imageUrl;

    public PokemonDetail(int id, String name, int height, int weight, List<String> types, String imageUrl) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.types = types;
        this.imageUrl = imageUrl;
    }
}
