package com.bastianrebolledo.app_prueba2_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bastianrebolledo.app_prueba2_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private ActivityMapsBinding binding;

    private Button btn_volver_lista, btn_volver_editar, btn_ir_mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_volver_lista = findViewById(R.id.btn_volver_lista);
        btn_volver_editar = findViewById(R.id.btn_volver_editar);
        btn_ir_mapa = findViewById(R.id.btn_ir_mapa);
    }

    public void irLista(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void irEditar(View view){
        this.finish();
    }

    public void irMapa(View view){
        //get geo
        String valores = getIntent().getStringExtra("valor");
        String[] partes = valores.split(",");
        String lat = partes[0];
        String lon = partes[1];

        //get intent
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("geo:" + lat + "," + lon));
        Intent chooser = Intent.createChooser(i, "Ir al mapa");
        startActivity(chooser);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        String valores = getIntent().getStringExtra("valor");
        String[] partes = valores.split(",");
        String lat = partes[0];
        String lon = partes[1];
        LatLng sydney = new LatLng(Float.parseFloat(lat), Float.parseFloat(lon));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación Actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }
}