package com.example.estudiarv1.services;

import com.example.estudiarv1.entities.ImageResponse;
import com.example.estudiarv1.entities.ImageToSave;
import com.example.estudiarv1.entities.Movimientos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovimientoService {
    @GET("Movimientos")
    Call<List<Movimientos>> getAllUser(@Query("limit") int limit, @Query("page") int page);

    @GET("Movimientos/{id}")
    Call<Movimientos> findUser(@Path("id") int id);

    @POST("Movimientos")
    Call<Movimientos> create(@Body Movimientos movimientos);

    @PUT("Movimientos/{id}")
    Call<Movimientos> update(@Path("id") int id, @Body Movimientos movimientos);

    @DELETE("Movimientos/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("image")
    Call<ImageResponse> subirImagen(@Body ImageToSave imagen);
}
