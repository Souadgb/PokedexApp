package com.example.pokedexapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pokemon_favorites")
public class FavoritePokemon {

    @PrimaryKey
    @NonNull
    public String name;   // use name as PK for quick toggle

    public int id;        // Pokémon ID from API
    public String imageUrl;

    public FavoritePokemon(@NonNull String name, int id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
