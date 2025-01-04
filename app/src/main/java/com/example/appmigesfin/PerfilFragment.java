package com.example.appmigesfin;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class PerfilFragment extends Fragment {

    private TextView usuarioTextView;
    private TextView correoTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializacion de elementos
        usuarioTextView = view.findViewById(R.id.eTUsuario);
        correoTextView = view.findViewById(R.id.eTCorreo);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("idUsuario", -1);
        if (userId != -1) {
            ConsultaPerfil(String.valueOf(userId));
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public void ConsultaPerfil(String id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            AdminSQL aBD = new AdminSQL(requireContext(), "usuarios", null, 1);
            db = aBD.getReadableDatabase();

            if (db != null) {
                String query = "SELECT usuario, correo FROM usuarios WHERE id = ?";
                cursor = db.rawQuery(query, new String[]{id});

                if (cursor.moveToFirst()) {
                    String nombreUsuario = cursor.getString(0);
                    String correoUsuario = cursor.getString(1);

                    // Establecer los valores en los TextView
                    usuarioTextView.setText(nombreUsuario);
                    correoTextView.setText(correoUsuario);
                } else {
                    Toast.makeText(requireContext(), "usuario no encontrado", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(requireContext(), "Error en la BD", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            // Asegurar el cierre del cursor y la base de datos
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}
