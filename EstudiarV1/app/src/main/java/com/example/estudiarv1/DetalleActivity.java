package com.example.estudiarv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.estudiarv1.BD.AppDatabase;
import com.example.estudiarv1.Utilities.RetrofitU;
import com.example.estudiarv1.entities.Pokemon;
import com.example.estudiarv1.repositories.PokemonRepository;
import com.example.estudiarv1.services.PokemonService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetalleActivity extends AppCompatActivity {

    int idPokemon;
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        TextView regDENomP = findViewById(R.id.tvNombrePokem);
        TextView regDETipoP = findViewById(R.id.tvTipoPokem);
        ImageView regDEImgP = findViewById(R.id.imFotoPokem);

        mRetrofit = RetrofitU.build();

        Button goBackP = findViewById(R.id.btnGoBack2);
        goBackP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalleActivity.this, ListaActivity.class);
                startActivity(intent);
            }
        });

        int position = getIntent().getIntExtra("position", 0);

        //Crear mapa en BD
        AppDatabase db = AppDatabase.getInstance(this);
        PokemonRepository repository = db.pokemonRepository();
        Pokemon pokemon = repository.findPokemonById(position);

        idPokemon = position;
        regDENomP.setText(pokemon.getNombre());
        regDETipoP.setText(pokemon.getTipo());
        Picasso.get().load(pokemon.getFoto())
                .resize(300, 400) //tamaño específico
                .into(regDEImgP);

        Button goMaps = findViewById(R.id.btnGoMaps);
        goMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalleActivity.this, MapsActivity.class);
                intent.putExtra("position", idPokemon);
                startActivity(intent);
            }
        });

        Button goSyncro = findViewById(R.id.btnSincronizar);
        goSyncro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pokemon.isSynced()) {
                    PokemonService service = mRetrofit.create(PokemonService.class);
                    Pokemon pokemon1 = new Pokemon(); //crear nuevo pokemon - se va al mockapi

                    pokemon1.setNombre(pokemon.getNombre());
                    pokemon1.setTipo(pokemon.getTipo());
                    pokemon1.setFoto(pokemon.getFoto());
                    pokemon1.setLatitud(pokemon.getLatitud());
                    pokemon1.setLongitud(pokemon.getLongitud());
                    pokemon1.setSynced(true);

                    Call<Pokemon> call = service.create(pokemon1);

                    call.enqueue(new Callback<Pokemon>() {
                        @Override
                        public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                            Log.i("MAIN_APP",  String.valueOf(response.code()));

                            Intent intent =  new Intent(DetalleActivity.this, ListaActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<Pokemon> call, Throwable t) {

                        }
                    });
                }
            }
        });

        //registrar movimiento
        Button regMov = findViewById(R.id.btnRegMovimiento);
        regMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(DetalleActivity.this, RegistroMovimientoActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });

        //ver movimientos
        Button mosMov = findViewById(R.id.btnMosMovimiento);
        mosMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(DetalleActivity.this, ListaMovimientosActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

    }
}