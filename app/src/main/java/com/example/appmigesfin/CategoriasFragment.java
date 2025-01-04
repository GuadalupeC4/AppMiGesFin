package com.example.appmigesfin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoriasFragment extends Fragment {
    TextView tvConsulta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        tvConsulta = view.findViewById(R.id.textView3);

        StringBuilder salida = new StringBuilder();

        //Consulta de todas las categorias
        try {
            AdminSQL admin = new AdminSQL(requireContext(), "categorias", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT id_Categoria, id_Usuario, nombre FROM categorias;", null);

            if (cursor.moveToFirst()) {
                do {
                    String id_Categoria = cursor.getString(0);
                    String id_Usuario = cursor.getString(1);
                    String nombre = cursor.getString(2);

                    salida.append("\nID_CATEGORIA: ").append(id_Categoria)
                            .append("\nID_USUARIO: ").append(id_Usuario)
                            .append("\nNOMBRE: ").append(nombre)
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
            return view;
        }

        tvConsulta.setText(salida.toString());
        return view;
    }
}