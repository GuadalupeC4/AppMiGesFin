package com.example.appmigesfin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    EditText etUsuario, etCorreo, etContraseña;
    Button btnRegistro, consulta;
    AdminSQL aBD;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etUsuario = findViewById(R.id.etUsuario);
        etCorreo = findViewById(R.id.etCorreo);
        etContraseña = findViewById(R.id.etContraseña);
        btnRegistro = findViewById(R.id.btnRegristo);
        consulta = findViewById(R.id.btnConsulta);

        consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent R= new Intent(getApplicationContext(), Consulta.class);
                startActivity(R);
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etUsuario.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Ingresa un nombre de usuario", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(etCorreo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Ingresa un correo", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(etContraseña.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Ingresa una contraseña", Toast.LENGTH_LONG).show();
                } else {
                    insertarUsuario(etUsuario.getText().toString(), etCorreo.getText().toString(), etContraseña.getText().toString());
                }
            }
        });
    }

    public void insertarUsuario(String usuario, String correo, String contraseña) {
        try {
            aBD = new AdminSQL(this, "usuarios", null, 1);
            db = aBD.getWritableDatabase();

            if (db != null) {
                ContentValues valores = new ContentValues();
                valores.put("usuario", usuario);
                valores.put("correo", correo);
                valores.put("contraseña", contraseña);

                long resultado = db.insert("usuarios", null, valores);

                if (resultado != -1) {
                    Toast.makeText(getApplicationContext(), "Datos insertados correctamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al insertar datos", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No fue posible crear la BD", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
