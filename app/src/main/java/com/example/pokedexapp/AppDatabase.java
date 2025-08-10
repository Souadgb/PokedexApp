package com.example.pokedexapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PokemonEntity.class}, version = 2, exportSchema = false) // bump version if needed
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Single background thread for DB writes
    public static final ExecutorService databaseWriteExecutor =
            Executors.newSingleThreadExecutor();

    public abstract PokemonDao pokemonDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "pokemon_db"
                            )
                            .fallbackToDestructiveMigration() // ok for class project
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
