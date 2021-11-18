package com.bastianrebolledo.app_prueba2_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class editar_registro extends AppCompatActivity {

    private String codigo, sensor, valor, fecha, hora, observacion;
    private boolean sePuedeVolver = false;

    private TextView txt_codigo, txt_sensor, txt_valor, txt_fecha, txt_hora;
    private EditText edit_observacion;

    //gps
    private LocationManager locationManager;
    private String lat, lon;
    private boolean GPSIsOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_registro);

        txt_codigo = findViewById(R.id.txt_codigo);
        txt_sensor = findViewById(R.id.txt_sensor);
        txt_valor = findViewById(R.id.txt_valor);
        txt_fecha = findViewById(R.id.txt_fecha);
        txt_hora = findViewById(R.id.txt_hora);
        edit_observacion = findViewById(R.id.edit_observacion);

        //SQLite
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "admin", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        codigo = getIntent().getStringExtra("codigo");
        Cursor cursor = db.rawQuery("select sensor, valor, fecha, hora, observacion from datos where codigo=" + codigo, null);

        if(cursor.moveToFirst()){
            sensor = cursor.getString(0);
            valor = cursor.getString(1);
            fecha = cursor.getString(2);
            hora = cursor.getString(3);
            observacion = cursor.getString(4);
        }
        db.close();

        //Setear textos
        txt_codigo.setText("Codigo: " + codigo);
        txt_sensor.setText("Sensor: " + sensor);
        txt_valor.setText("Valor: " + valor);
        txt_fecha.setText("Fecha: " + fecha);
        txt_hora.setText("Hora: " + hora);
        edit_observacion.setText(observacion);

        //GPS
        if(sensor.equals("GPS")) {
        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                getLocation();
                GPSIsOn = true;
            }
        }
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(editar_registro.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(editar_registro.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locationGPS != null){
                double latitud = locationGPS.getLatitude();
                double longitud = locationGPS.getLongitude();

                lat = String.valueOf(latitud);
                lon = String.valueOf(longitud);
                //Toast.makeText(getApplicationContext(), lat + " - " + lon, Toast.LENGTH_SHORT).show();
            }
            else if(locationNetwork != null){
                double latitud = locationNetwork.getLatitude();
                double longitud = locationNetwork.getLongitude();

                lat = String.valueOf(latitud);
                lon = String.valueOf(longitud);
                //Toast.makeText(getApplicationContext(), lat + " - " + lon, Toast.LENGTH_SHORT).show();
            }
            else if(locationPassive != null){
                double latitud = locationPassive.getLatitude();
                double longitud = locationPassive.getLongitude();

                lat = String.valueOf(latitud);
                lon = String.valueOf(longitud);
                //Toast.makeText(getApplicationContext(), lat + " - " + lon, Toast.LENGTH_SHORT).show();
            }
            else{
                lat = "-38.74850309";
                lon = "-72.5901538";
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Activar el GPS?").setCancelable(false).setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void eliminar(View view){

        //SQLite
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "admin", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        int borrado = db.delete("datos", "codigo=" + codigo, null);
                        if(borrado == 1){
                            Toast.makeText(getApplicationContext(), "Registro eliminado correctamente!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "No es posible eliminar el registro, pruebe más tarde :(", Toast.LENGTH_SHORT).show();
                        }
                        db.close();
                        sePuedeVolver = true;
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
                if(sePuedeVolver){
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de eliminar este registro?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    public void Guardar(View view){
        if(!edit_observacion.getText().toString().trim().equals(observacion)){
            //SQLite
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "admin", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            ContentValues nuevosDatos = new ContentValues();
            nuevosDatos.put("observacion", edit_observacion.getText().toString());

            int modificacionDatos = db.update("datos", nuevosDatos, "codigo=" + codigo, null);
            if(modificacionDatos == 1){
                Toast.makeText(getApplicationContext(), "Registro modificado correctamente!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "No fue posible modificar el registro, pruebe más tarde :(", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else {
            Toast.makeText(this, "No se han generado cambios", Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void volver(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void Mapa(View view){
        if(sensor.equals("GPS")){
            if(GPSIsOn){
                Intent i = new Intent(this, MapsActivity.class);
                i.putExtra("valor", valor);
                startActivity(i);
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe activar su GPS primero!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Este sensor no es compatible con esta opccion", Toast.LENGTH_SHORT).show();
        }
    }
}