package com.example.pokedexapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class PokemonEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String imageUrl;

    public PokemonEntity(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
