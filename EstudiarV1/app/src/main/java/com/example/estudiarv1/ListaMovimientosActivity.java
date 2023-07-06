package com.example.estudiarv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.estudiarv1.Adapters.MovimientoAdapter;
import com.example.estudiarv1.Adapters.PokemonAdapter;
import com.example.estudiarv1.BD.AppDatabase;
import com.example.estudiarv1.Utilities.RetrofitU;
import com.example.estudiarv1.entities.Movimientos;
import com.example.estudiarv1.entities.Pokemon;
import com.example.estudiarv1.repositories.MovimientoRepository;
import com.example.estudiarv1.repositories.PokemonRepository;
import com.example.estudiarv1.services.MovimientoService;
import com.example.estudiarv1.services.PokemonService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListaMovimientosActivity extends AppCompatActivity {

    RecyclerView mRvMLista;
    boolean mIsLoading = false;
    int mPage = 1;
    List<Movimientos> mdata = new ArrayList<>();
    MovimientoAdapter mAdapter = new MovimientoAdapter(mdata, this);
    Retrofit mRetrofit;
    Context context = this;
    String currentFilter = "";
    int idPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_movimientos);

        idPokemon = getIntent().getIntExtra("position", 0); //RECIVI EL POKEMON EXACTO
        mRetrofit = RetrofitU.build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvMLista =  findViewById(R.id.rvListaSimple2);
        mRvMLista.setLayoutManager(layoutManager);
        mRvMLista.setAdapter(mAdapter);

        Button btnActualizar = findViewById(R.id.btnActualizar2);
        Button btnGoBack = findViewById(R.id.btnGoBack2);
        Button btnSyncro = findViewById(R.id.btnSyncro);

        mRvMLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        MovimientoRepository repository = db.movimientoRepository();
        List<Movimientos> users = repository.getAllUser(); //mandamos la lista de los pokemones
        Log.i("MAIN_APP: DB", new Gson().toJson(users));
        Log.i("MAIN_APP", idPokemon+"");
        mAdapter.setMovimiento(users);
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
                Intent intent =  new Intent(ListaMovimientosActivity.this, DetalleActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSyncro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<users.size(); i++){
                    if (!users.get(i).isSyncro()){

                        Movimientos aux = new Movimientos();
                        aux.setComentario(users.get(i).getComentario());
                        aux.setIdPokemon(users.get(i).getIdPokemon());
                        aux.setSyncro(true);

                        mRetrofit = RetrofitU.build();
                        MovimientoService service = mRetrofit.create(MovimientoService.class);
                        Call<Movimientos> call = service.create(aux);

                        call.enqueue(new Callback<Movimientos>() {
                            @Override
                            public void onResponse(Call<Movimientos> call, Response<Movimientos> response) {
                                Intent intent =  new Intent(ListaMovimientosActivity.this, ListaActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Movimientos> call, Throwable t) {

                            }
                        });
                    }
                }
            }
        });

        mRvMLista.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void uploadToWebService(String filter, int nextPage) {

        AppDatabase db = AppDatabase.getInstance(context);
        db.clearAllTables();

        MovimientoService service = mRetrofit.create(MovimientoService.class);
        service.getAllUser(20, nextPage).enqueue(new Callback<List<Movimientos>>() {
            @Override
            public void onResponse(Call<List<Movimientos>> call, Response<List<Movimientos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inserta los datos en la base de datos
                    AppDatabase db = AppDatabase.getInstance(ListaMovimientosActivity.this);
                    MovimientoRepository repository = db.movimientoRepository();
                    repository.insertAll(response.body());

                    // Actualiza los datos en el adaptador y notifica los cambios
                    List<Movimientos> newData = repository.getAllUser();
                    mAdapter.setMovimiento(newData);
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Movimientos>> call, Throwable t) {
                // Maneja el error de la llamada al servicio MockAPI
            }
        });
    }


    private void loadMore(int nextPage) {
        mIsLoading = true;

        mdata.add(null);
        mAdapter.notifyItemInserted(mdata.size() - 1);

        MovimientoService service = mRetrofit.create(MovimientoService.class);
        Log.i("MAIN_APP  Page:", String.valueOf(nextPage));
        service.getAllUser(100, nextPage).enqueue(new Callback<List<Movimientos>>() { // Cambia el número de registros por página según tus necesidades
            @Override
            public void onResponse(Call<List<Movimientos>> call, Response<List<Movimientos>> response) {
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
            public void onFailure(Call<List<Movimientos>> call, Throwable t) {
                // Manejar error de la llamada al servicio MockAPI
            }
        });
    }
}