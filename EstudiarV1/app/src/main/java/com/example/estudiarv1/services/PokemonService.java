package com.example.estudiarv1.services;

import com.example.estudiarv1.entities.ImageResponse;
import com.example.estudiarv1.entities.ImageToSave;
import com.example.estudiarv1.entities.Pokemon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonService {
    @GET("Pokemones")
    Call<List<Pokemon>> getAllUser(@Query("limit") int limit, @Query("page") int page);

    @GET("Pokemones/{id}")
    Call<Pokemon> findUser(@Path("id") int id);

    @POST("Pokemones")
    Call<Pokemon> create(@Body Pokemon pokemon);

    @PUT("Pokemones/{id}")
    Call<Pokemon> update(@Path("id") int id, @Body Pokemon pokemon);

    @DELETE("Pokemones/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("image")
    Call<ImageResponse> subirImagen(@Body ImageToSave imagen);
}
