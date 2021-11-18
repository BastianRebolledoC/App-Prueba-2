package com.bastianrebolledo.app_prueba2_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private ArrayList<String> sensores = new ArrayList<String>();
    private ArrayList<String> valores = new ArrayList<String>();
    private ArrayList<String> codigo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SQLite
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "admin", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor cursor = db.rawQuery("select codigo, sensor, valor from datos", null);
        ArrayList<String> sensoresYValores = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {
                codigo.add(cursor.getString(0));
                sensores.add(cursor.getString(1));
                valores.add(cursor.getString(2));

                String emoji = "";
                switch (cursor.getString(1)){
                    case "Giroscopio":
                        emoji = "⏱️";
                        break;
                    case "Proximidad":
                        emoji = "\uD83D\uDCF2";
                        break;
                    case "Luz":
                        emoji = "\uD83D\uDD26";
                        break;
                    case "GPS":
                        emoji = "\uD83D\uDDFA";
                        break;
                    default:
                        emoji = "\uD83C\uDF83";
                        break;
                }
                String sensorYValor = emoji + " " + cursor.getString(1) + ": " + cursor.getString(2);
                sensoresYValores.add(sensorYValor);
            } while (cursor.moveToNext());
        }
        db.close();

        //ListView
        listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sensoresYValores);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(listView.getContext(), editar_registro.class);
                i.putExtra("codigo", codigo.get(position));
                startActivity(i);
            }
        });

    }
    public void create(View view){
        Intent i = new Intent(this, CrearRegistro.class);
        startActivity(i);
    }
}