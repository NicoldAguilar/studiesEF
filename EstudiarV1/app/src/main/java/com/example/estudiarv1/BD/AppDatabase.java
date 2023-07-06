package com.example.estudiarv1.BD;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.estudiarv1.entities.Pokemon;
import com.example.estudiarv1.repositories.PokemonRepository;

@Database(entities = {Pokemon.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PokemonRepository pokemonRepository();

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "vj20231")
                .allowMainThreadQueries()
                .build();
    }
}
