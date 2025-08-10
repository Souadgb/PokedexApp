package com.example.pokedexapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PokemonDao {
    @Insert
    void insert(PokemonEntity pokemon);

    @Delete
    void delete(PokemonEntity pokemon);

    @Query("SELECT * FROM favorites")
    List<PokemonEntity> getAllFavorites();

    @Query("DELETE FROM favorites WHERE name = :pokemonName")
    void deleteByName(String pokemonName);
}
