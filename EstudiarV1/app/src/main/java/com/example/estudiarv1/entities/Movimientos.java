package com.example.estudiarv1.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Movimientos")
public class Movimientos {
    @PrimaryKey()
    public int idMov;

    //para sincronizar
    @ColumnInfo(name = "synced")
    private boolean syncro; //true en el mockapi - false no esta en mockapi

    public String comentario;
    public String idPokemon;

    public int getIdMov() {
        return idMov;
    }

    public void setIdMov(int idMov) {
        this.idMov = idMov;
    }

    public boolean isSyncro() {
        return syncro;
    }

    public void setSyncro(boolean syncro) {
        this.syncro = syncro;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdPokemon() {
        return idPokemon;
    }

    public void setIdPokemon(String idPokemon) {
        this.idPokemon = idPokemon;
    }
}
