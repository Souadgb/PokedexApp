package com.example.pokedexapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoritePokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoritePokemon entity);

    @Query("DELETE FROM pokemon_favorites WHERE name = :name")
    void deleteByName(String name);

    @Query("SELECT COUNT(*) FROM pokemon_favorites WHERE name = :name")
    int isFavorite(String name);

    // >>> Add this <<<
    @Query("SELECT * FROM pokemon_favorites ORDER BY name ASC")
    List<FavoritePokemon> getAllFavorites();
}
