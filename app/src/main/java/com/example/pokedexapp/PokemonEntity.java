package com.example.pokedexapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pokemon_list")
public class PokemonEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;          // local row id (not the API id)

    public String name;
    public String imageUrl;

    public PokemonEntity(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
