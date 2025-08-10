package com.example.pokedexapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PokemonEntity.class}, version = 1)
public abstract class PokemonDatabase extends RoomDatabase {
    private static volatile PokemonDatabase INSTANCE;

    public abstract PokemonDao pokemonDao();

    public static PokemonDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (PokemonDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PokemonDatabase.class, "pokemon_db")
                            .allowMainThreadQueries() // For simplicity (remove later)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
