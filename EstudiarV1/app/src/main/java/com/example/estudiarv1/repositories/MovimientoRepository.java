package com.example.estudiarv1.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.estudiarv1.entities.Movimientos;
import com.example.estudiarv1.entities.Pokemon;

import java.util.List;

@Dao
public interface MovimientoRepository {

    @Query("SELECT * FROM Movimientos")
    List<Movimientos> getAllUser();
    @Insert
    void create(Movimientos movimientos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Movimientos> movimientos);
    @Update
    void updateCuenta(Movimientos movimientos);

    @Query("SELECT MAX(idMov) FROM Movimientos")
    int getLastId();
    @Query("SELECT * FROM Movimientos WHERE idMov = :movId")
    Movimientos findMovimientoById(int movId);

    @Query("SELECT * FROM Movimientos WHERE synced = 0")
    List<Movimientos> getUnsyncedPokemones();

    @Query("SELECT * FROM Movimientos WHERE idPokemon = :idPokemon")
    List<Movimientos> getMovimientosByPokemonId(int idPokemon);

}
