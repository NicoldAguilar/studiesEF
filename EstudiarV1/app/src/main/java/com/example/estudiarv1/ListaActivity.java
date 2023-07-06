package com.example.estudiarv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.estudiarv1.Adapters.PokemonAdapter;
import com.example.estudiarv1.BD.AppDatabase;
import com.example.estudiarv1.Utilities.RetrofitU;
import com.example.estudiarv1.entities.Pokemon;
import com.example.estudiarv1.repositories.PokemonRepository;
import com.example.estudiarv1.services.PokemonService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListaActivity extends AppCompatActivity {

    RecyclerView mRvLista;
    boolean mIsLoading = false;
    int mPage = 1;
    List<Pokemon> mdata = new ArrayList<>();
    PokemonAdapter mAdapter = new PokemonAdapter(mdata, this);
    Retrofit mRetrofit;
    Context context = this;
    String currentFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        mRetrofit = RetrofitU.build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvLista =  findViewById(R.id.rvListaSimple);
        mRvLista.setLayoutManager(layoutManager);
        mRvLista.setAdapter(mAdapter);

        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnGoBack = findViewById(R.id.btnGoBack);

        mRvLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!mIsLoading) {

                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mdata.size() - 1) {
                        mPage++;
                        loadMore(mPage);
                    }
                }

            }
        });
        //Manda la lista desde base de datos para mostrarse
        AppDatabase db = AppDatabase.getInstance(context);
        PokemonRepository repository = db.pokemonRepository();
        List<Pokemon> users = repository.getAllUser();
        Log.i("MAIN_APP: DB", new Gson().toJson(users));
        mAdapter.setPokemon(users);
        mAdapter.notifyDataSetChanged();

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToWebService(currentFilter, mPage);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(ListaActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRvLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!mIsLoading) {

                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mdata.size() - 1) {
                        mPage++;
                        loadMore(mPage);
                    }
                }

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void uploadToWebService(String filter, int nextPage) {

        AppDatabase db = AppDatabase.getInstance(context);
        db.clearAllTables();

        PokemonService service = mRetrofit.create(PokemonService.class);
        service.getAllUser(20, nextPage).enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaActivity.this);
                    PokemonRepository repository = db.pokemonRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Pokemon> newData = repository.getAllUser();
                        mAdapter.setPokemon(newData);
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });
    }


    private void loadMore(int nextPage) {
        mIsLoading = true;

        mdata.add(null);
        mAdapter.notifyItemInserted(mdata.size() - 1);

        PokemonService service = mRetrofit.create(PokemonService.class);
        Log.i("MAIN_APP  Page:", String.valueOf(nextPage));
        service.getAllUser(100, nextPage).enqueue(new Callback<List<Pokemon>>() { // Cambia el número de registros por página según tus necesidades
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mdata.remove(mdata.size() - 1);
                    mAdapter.notifyItemRemoved(mdata.size() - 1);

                    mdata.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
                    mIsLoading = false;

                    // Si hay más registros disponibles, cargar la siguiente página
                    if (response.body().size() >= 100) { // Cambia el número de registros por página según tus necesidades
                        loadMore(nextPage + 1);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                // Manejar error de la llamada al servicio MockAPI
            }
        });
    }
}