package com.example.appmigesfin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MovimientosFragment extends Fragment {
    TextView tvConsulta;
    Button btnTabla;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimientos, container, false);

        tvConsulta = view.findViewById(R.id.textView3);
        btnTabla =view.findViewById(R.id.btnTabla);

        StringBuilder salida = new StringBuilder();

        try {
            AdminSQL admin = new AdminSQL(requireContext(), "movimientos", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT id_Movimiento,id_Categoria,fecha,descripcion,cantidad,tipo FROM movimientos;", null);

            if (cursor.moveToFirst()) {
                do {
                    String id_Movimiento = cursor.getString(0);
                    String id_Categoria = cursor.getString(1);
                    String fecha = cursor.getString(2);
                    String descripcion = cursor.getString(3);
                    String cantidad = cursor.getString(4);
                    String tipo = cursor.getString(5);

                        salida.append("\nID_MOVIMIENTO: ").append(id_Movimiento)
                            .append("\nID_CATEGORIA: ").append(id_Categoria)
                            .append("\nFECHA: ").append(fecha)
                            .append("\nDESCRIPCIÓN: ").append(descripcion)
                            .append("\nCANTIDAD: ").append(cantidad)
                            .append("\nTIPO: ").append(tipo)
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


        btnTabla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new TablaFragment());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}