package com.example.appmigesfin;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class Consulta extends AppCompatActivity {
    TextView tvConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        tvConsulta = findViewById(R.id.textView3);

        StringBuilder salida = new StringBuilder();

        //Consulta de usuarios registrados
        try {
            AdminSQL admin = new AdminSQL(this, "usuarios", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT id, usuario, correo, contraseña FROM usuarios;", null);

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String usuario = cursor.getString(1);
                    String correo = cursor.getString(2);
                    String contraseña = cursor.getString(3);

                    salida.append("\nID: ").append(id)
                            .append("\nUSUARIO: ").append(usuario)
                            .append("\nCORREO: ").append(correo)
                            .append("\nCONTRASEÑA: ").append(contraseña)
                            .append("\n")
                            .append("-----------------------------------------------------------------\n");
                } while (cursor.moveToNext());
            } else {
                salida.append("No hay usuarios registrados.");
            }

            cursor.close();
            db.close();

        } catch (Exception e) {
            tvConsulta.setText("Error: " + e.getMessage());
            Log.e("ConsultaError", "Error consultando usuarios", e);
            return;
        }

        tvConsulta.setText(salida.toString());
    }
}
