package com.example.pokedexapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoritePokemonDao {
    @Insert
    void insert(FavoritePokemon pokemon);

    @Delete
    void delete(FavoritePokemon pokemon);

    @Query("SELECT * FROM favorites")
    List<FavoritePokemon> getAll();
}
