package com.example.estudiarv1.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.estudiarv1.entities.Pokemon;

import java.util.List;

@Dao
public interface PokemonRepository {
    @Query("SELECT * FROM Pokemones")
    List<Pokemon> getAllUser();
    @Insert
    void create(Pokemon pokemon);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Pokemon> pokemones);
    @Update
    void updateCuenta(Pokemon pokemon);

    @Query("SELECT MAX(id) FROM Pokemones")
    int getLastId();
    @Query("SELECT * FROM Pokemones WHERE id = :pokemonId")
    Pokemon findPokemonById(int pokemonId);

    @Query("SELECT * FROM Pokemones WHERE synced = 0")
    List<Pokemon> getUnsyncedPokemones();
}


