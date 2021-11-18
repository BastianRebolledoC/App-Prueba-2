package com.bastianrebolledo.app_prueba2_android;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CrearRegistro extends AppCompatActivity implements SensorEventListener {

    private String datoSensor, datoFecha, datoHora, datoObservacion;
    private String datoValor = "0.00";
    private int nuevoCodigo;
    private boolean esGuardable = false;
    private boolean GPSIsOn = false;

    private TextView valorSensor, fecha, hora;
    private EditText observacion;

    private Spinner spinner;

    private SensorManager sensorManager;
    private Sensor sensorP, sensorG, sensorX;

    //gps
    private LocationManager locationManager;
    private String lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_registro);

        valorSensor = findViewById(R.id.valorSensor);
        fecha = findViewById(R.id.fecha);
        hora = findViewById(R.id.hora);
        observacion = findViewById(R.id.observacion);

        //Fecha y hora
        DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        datoFecha = date.format(Calendar.getInstance().getTime());
        datoHora = time.format(Calendar.getInstance().getTime());
        fecha.setText("Fecha: " + datoFecha);
        hora.setText("Hora: " + datoHora);

        //spiner
        spinner = findViewById(R.id.spinner);
        String[] sensores = {"⬇️ Selecciona un sensor ⬇️",
                "⏱️ Giroscopio",
                "\uD83D\uDCF2 Proximidad",
                "\uD83D\uDD26 Luz",
                "\uD83D\uDCCD GPS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sensores);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        esGuardable = false;
                        break;
                    //Giroscopio
                    case 1:
                        datoSensor = "Giroscopio";
                        esGuardable = true;
                        datoValor = "0.00";
                        break;
                    case 2:
                        datoSensor = "Proximidad";
                        esGuardable = true;
                        datoValor = "0.00";
                        break;
                    case 3:
                        datoSensor = "Luz";
                        esGuardable = true;
                        datoValor = "5.00";
                        break;
                    case 4:
                        datoSensor = "GPS";
                        ActivityCompat.requestPermissions(CrearRegistro.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            OnGPS();
                            Toast.makeText(getApplicationContext(), "Por favor active su GPS y vuelva a intentar.", Toast.LENGTH_SHORT).show();
                        }else{
                            getLocation();
                            GPSIsOn = true;
                            esGuardable = true;
                            datoValor = lat + "," + lon;
                        }
                        /*if(GPSIsOn){
                            esGuardable = true;
                            datoValor = lat + "," + lon;
                        }else{
                            esGuardable = false;
                            datoValor = "0.00";
                            Toast.makeText(getApplicationContext(), "Vuelva a reiniciar la APP con el GPS prendido", Toast.LENGTH_SHORT).show();
                        }*/
                        break;
                    default:
                        datoSensor = "DESCONOCIDO";
                        esGuardable = false;
                        break;
                }
                valorSensor.setText("Valor del sensor: " + datoValor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sensores
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorG = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, sensorG, SensorManager.SENSOR_DELAY_NORMAL);

        sensorP = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener((SensorEventListener) this, sensorP, SensorManager.SENSOR_DELAY_NORMAL);

        sensorX = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener((SensorEventListener) this, sensorX, SensorManager.SENSOR_DELAY_NORMAL);

        //GPS
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

    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(CrearRegistro.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CrearRegistro.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                esGuardable = false;
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void previous(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void save(View view) {

        datoObservacion = !observacion.getText().toString().isEmpty() ?
                observacion.getText().toString().trim() :
                "";

        if (esGuardable && datoValor != null) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "admin", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor cursor = db.rawQuery("select codigo from datos", null);
            if (cursor.moveToLast()) {
                int valorCodigo = Integer.parseInt(cursor.getString(0));
                nuevoCodigo = valorCodigo + 1;
            } else {
                nuevoCodigo = 1;
            }


            ContentValues nuevosDatos = new ContentValues();
            nuevosDatos.put("codigo", nuevoCodigo);
            nuevosDatos.put("sensor", datoSensor);
            nuevosDatos.put("valor", datoValor);
            nuevosDatos.put("fecha", datoFecha);
            nuevosDatos.put("hora", datoHora);
            nuevosDatos.put("observacion", datoObservacion);

            db.insert("datos", null, nuevosDatos);
            db.close();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Seleccione un sensor, registre un valor o reinicie la APP con el GPS", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (spinner.getSelectedItemPosition() == 1 && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            datoValor = String.valueOf(event.values[0]);
            valorSensor.setText(String.format("Valor del sensor: %.7f", event.values[0]));
        } else if (spinner.getSelectedItemPosition() == 2 && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            datoValor = String.valueOf(event.values[0]);
            valorSensor.setText(String.format("Valor del sensor: %.2f", event.values[0]));
        } else if (spinner.getSelectedItemPosition() == 3 && event.sensor.getType() == Sensor.TYPE_LIGHT) {
            datoValor = String.valueOf(event.values[0]);
            valorSensor.setText(String.format("Valor del sensor: %.2f", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}