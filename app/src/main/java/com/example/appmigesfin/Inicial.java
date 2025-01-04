package com.example.appmigesfin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicial extends AppCompatActivity {

    Button iniciarS, registro, consulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        iniciarS = findViewById(R.id.btIniciar);
        registro = findViewById(R.id.btRegistro);
        consulta = findViewById(R.id.btnConsulta);

        iniciarS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IS = new Intent(getApplicationContext(), IniciarSesion.class);
                startActivity(IS);
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent R= new Intent(getApplicationContext(), Registro.class);
                startActivity(R);
            }
        });

        //Solo para pruebas
        consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent R= new Intent(getApplicationContext(), Consulta.class);
                startActivity(R);
            }
        });
    }
}