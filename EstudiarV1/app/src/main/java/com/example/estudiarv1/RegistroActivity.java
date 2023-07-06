package com.example.estudiarv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.estudiarv1.BD.AppDatabase;
import com.example.estudiarv1.Utilities.CameraU;
import com.example.estudiarv1.Utilities.RetrofitU;
import com.example.estudiarv1.entities.ImageResponse;
import com.example.estudiarv1.entities.ImageToSave;
import com.example.estudiarv1.entities.Pokemon;
import com.example.estudiarv1.repositories.PokemonRepository;
import com.example.estudiarv1.services.PokemonService;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroActivity extends AppCompatActivity {

    private static final String urlFotoApi= "https://demo-upn.bit2bittest.com/";
    private static final int OPEN_CAMERA_REQUEST = 1001;
    private static final int OPEN_GALLERY_REQUEST = 1002;

    Retrofit retrofitP;
    Context context = this;

    private ImageView ivAvatar;
    private String urlCamara; //settear camara
    private LocationManager mlocationManager; //para mapas
    double latitud = 0;
    double longitud = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        EditText regPNom = findViewById(R.id.etNombreP);
        EditText regPTipo = findViewById(R.id.etTipoP);

        ivAvatar = findViewById(R.id.ivImagenP);

        Button btnCamera = findViewById(R.id.bttnCamara);
        Button btnGallery = findViewById(R.id.btnGaleria);
        Button btnRegistro = findViewById(R.id.bttnRegistro);
        Button btnGoingBack = findViewById(R.id.bttnGoingBack);

        retrofitP = RetrofitU.build(); //settear mockapi

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOpenCamera();
                obtenerCoordenadas(); //para cargar coodenadas
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                    obtenerCoordenadas(); //para cargar coodenadas
                }
                else {
                    String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 2000);
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = regPNom.getText().toString();
                String tipo = regPTipo.getText().toString();

                //Crear un service del repository
                AppDatabase database = AppDatabase.getInstance(context);
                PokemonRepository pokeRepository = database.pokemonRepository();

                // Obtener el último ID registrado en la base de datos para crear uno nuevo
                int lastId = pokeRepository.getLastId();

                //Crear un nuevo pokemon
                Pokemon pokemon = new Pokemon();
                //Llenar los datos
                pokemon.setId(lastId + 1);
                pokemon.setNombre(nombre);
                pokemon.setTipo(tipo);
                pokemon.setFoto(urlCamara);
                pokemon.setLatitud(latitud+"");
                pokemon.setLongitud(longitud+"");
                pokemon.setSynced(false);

                pokeRepository.create(pokemon); //enviando pokemon a la BD

                //Ir al mostrar luego de registrar
                Intent intent =  new Intent(RegistroActivity.this, ListaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGoingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivAvatar.setImageBitmap(photo);
            //Convertir a base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            ImageToSave imgB64 = new ImageToSave(imgBase64);
            enviarImagen(imgB64);
        }

        if(requestCode == OPEN_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close(); // close cursor

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ivAvatar.setImageBitmap(bitmap);
            //Convertir a base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            ImageToSave imgB64 = new ImageToSave(imgBase64);
            enviarImagen(imgB64);
        }

    }
    private void enviarImagen (ImageToSave imgB64){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlFotoApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokemonService service = retrofit.create(PokemonService.class);
        Call<ImageResponse> call = service.subirImagen(imgB64);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if(response.isSuccessful()){
                    Log.i("MAIN_APP", "Si se subió");
                    Log.i("MAIN_APP", urlFotoApi  + response.body().getUrl());
                    urlCamara = urlFotoApi + response.body().getUrl();
                }
                else{
                    Log.i("MAIN_APP", "No se subió");
                }
            }
            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
            }
        });
    }
    private void handleOpenCamera() {
        if(checkSelfPermission(Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED)
        {
            // abrir camara
            Log.i("MAIN_APP", "Tiene permisos para abrir la camara");
            abrirCamara();
        } else {
            // solicitar el permiso
            Log.i("MAIN_APP", "No tiene permisos para abrir la camara, solicitando");
            String[] permissions = new String[] {Manifest.permission.CAMERA};
            requestPermissions(permissions, 1000);
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST);
    }

    // ALMACENAR COORDENADAS
    void obtenerCoordenadas(){
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    Log.i("MAIN_APP", "Latitud" + latitud);
                    Log.i("MAIN_APP", "Longitud" + longitud);
                    mlocationManager.removeUpdates(this);
                }
            };
            mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
        }
        else{
            String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            Log.i("MAIN_APP", "No hay permisos pa esta webada");
            requestPermissions(permissions, 1000);
        }
    }
}