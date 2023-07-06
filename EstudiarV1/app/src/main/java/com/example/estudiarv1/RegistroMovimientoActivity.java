package com.example.estudiarv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.estudiarv1.BD.AppDatabase;
import com.example.estudiarv1.Utilities.RetrofitU;
import com.example.estudiarv1.entities.Movimientos;
import com.example.estudiarv1.repositories.MovimientoRepository;
import com.example.estudiarv1.repositories.PokemonRepository;

import retrofit2.Retrofit;

public class RegistroMovimientoActivity extends AppCompatActivity {

    Retrofit retrofitP;
    Context context = this;
    int idPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_movimiento);

        EditText regMmov = findViewById(R.id.etMovimientoP);

        Button btnRegistro = findViewById(R.id.btttnRegistro);

        retrofitP = RetrofitU.build(); //settear mockapi

        idPokemon = getIntent().getIntExtra("position", 0); //RECIVI EL POKEMON EXACTO

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String movi = regMmov.getText().toString();

                //Crear un service del repository
                AppDatabase database = AppDatabase.getInstance(context);
                MovimientoRepository movimRepository = database. movimientoRepository();

                // Obtener el último ID registrado en la base de datos para crear uno nuevo
                int lastId = movimRepository.getLastId();

                //Crear un movimiento
                Movimientos movv = new Movimientos();
                movv.setIdMov(lastId + 1);
                //Llenar
                movv.setComentario(movi);
                movv.setIdPokemon(idPokemon+"");

                movimRepository.create(movv);


                Intent intent =  new Intent(RegistroMovimientoActivity.this, ListaMovimientosActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Button btnGoingBack3 = findViewById(R.id.bttnGoingBack3);
        btnGoingBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegistroMovimientoActivity.this, DetalleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}