package com.example.estudiarv1.Utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitU {
    public static Retrofit build() {
        return new Retrofit.Builder()
                .baseUrl("https://63023872c6dda4f287b57f7c.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
