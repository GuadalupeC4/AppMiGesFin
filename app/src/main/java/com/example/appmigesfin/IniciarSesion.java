package com.example.appmigesfin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appmigesfin.Connection.ConnectionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class IniciarSesion extends AppCompatActivity {

    EditText Correo, Password;
    Button Inicio;
    AdminSQL aBD;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        Inicio = findViewById(R.id.btIniciarSesion);
        Correo = findViewById(R.id.eTCorreo);
        Password = findViewById(R.id.eTPassword);

        Inicio.setOnClickListener(view -> {
            if (TextUtils.isEmpty(Correo.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Ingresa un correo", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(Password.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Ingresa la contraseña", Toast.LENGTH_LONG).show();
            } else {
                Sesion(Correo.getText().toString(), Password.getText().toString());
            }
        });
    }

    public void Sesion(String correo, String contraseña) {
        try {
            aBD = new AdminSQL(this, "usuarios", null, 1);
            db = aBD.getReadableDatabase();

            // Consulta para obtener el id y el usuario
            String sql = "SELECT id, usuario FROM usuarios WHERE correo = ? AND contraseña = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{correo, contraseña});

             //Verificar si el correo y contraseñas son validas
            if (cursor.moveToFirst()) {
                int idUsuario = cursor.getInt(0);
                String nombreUsuario = cursor.getString(1);

                SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.putString("nombreUsuario", nombreUsuario);
                editor.apply();

                //Toast.makeText(IniciarSesion.this, "Acceso Exitoso", Toast.LENGTH_LONG).show();

                Intent intX = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intX);
                finish();
            } else {
                Toast.makeText(IniciarSesion.this, "Error en el correo o contraseña", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
