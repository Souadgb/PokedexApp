package com.example.pokedexapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PokemonEntity> items);

    @Query("SELECT * FROM pokemon_list")
    List<PokemonEntity> getAll();

    @Query("DELETE FROM pokemon_list")
    void clear();
}
