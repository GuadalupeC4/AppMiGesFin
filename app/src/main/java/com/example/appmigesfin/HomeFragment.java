package com.example.appmigesfin;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment {
    AdminSQL aBD;
    SQLiteDatabase db = null;
    Button nTabla;
    LinearLayout containerCategorias;
    TextView NomUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nTabla = view.findViewById(R.id.btnCTabla);

        containerCategorias = view.findViewById(R.id.containerCategorias);
        NomUsuario = view.findViewById(R.id.tvNomUsuario);

        cargarCategorias();

        nTabla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tablaBottomDialog();
            }
        });
        //Verificacion del usuario
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("idUsuario", -1);
        if (userId != -1) {
            ConsultaNombre(String.valueOf(userId));
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void tablaBottomDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottontabla);

        // Elementos de bottontabla
        EditText nuevaTabla = dialog.findViewById(R.id.nuevatabla);
        Button agregarButton = dialog.findViewById(R.id.btnNTabla);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        // Cerrar diálogo
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        agregarButton.setOnClickListener(view -> {
            String nombreTabla = nuevaTabla.getText().toString().trim();

            if (!nombreTabla.isEmpty()) {
                insertarCategoria(nombreTabla);
                dialog.dismiss(); // Cierra el diálogo después de guardar
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }

    private void cargarCategorias() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("idUsuario", -1);

        if (idUsuario != -1) {
            try {
                aBD = new AdminSQL(requireContext(), "categorias", null, 1);
                SQLiteDatabase db = aBD.getReadableDatabase();

                Cursor cursor = db.rawQuery("SELECT id_Categoria,nombre FROM categorias WHERE id_Usuario = ?",
                        new String[]{String.valueOf(idUsuario)});

                if (cursor.moveToFirst()) {
                    do {
                        int idCategoria = cursor.getInt(0);
                        String nombreCategoria = cursor.getString(1);
                        agregarBotonCategoria(idCategoria,nombreCategoria);
                    } while (cursor.moveToNext());
                }

                cursor.close();
                db.close();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al cargar categorías: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
        }
    }


    private void insertarCategoria(String nombreTabla) {
        SQLiteDatabase db = null;
        try {
            // Recupera del identificación del usuario
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
            int idUsuario = sharedPreferences.getInt("idUsuario", -1); // -1 como valor por defecto si no existe

            if (idUsuario == -1) {
                Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
                return;
            }

            aBD = new AdminSQL(requireContext(), "categorias", null, 1);
            db = aBD.getWritableDatabase();

            if (db != null) {
                // Inserta datos en la tabla Categorias
                ContentValues valores = new ContentValues();
                valores.put("nombre", nombreTabla);
                valores.put("id_Usuario", idUsuario);

                long resultado = db.insert("Categorias", null, valores);

                if (resultado != -1) {
                    Toast.makeText(requireContext(), "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error al agregar la categoría", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "No fue posible abrir la base de datos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private void agregarBotonCategoria(int idCategoria, String nombreCategoria) {
        Button boton = new Button(requireContext());
        boton.setText(nombreCategoria);
        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        boton.setOnClickListener(v -> {
            //PRUEBA
            //Toast.makeText(requireContext(), "Categoría seleccionada: " + nombreCategoria, Toast.LENGTH_SHORT).show();
            //replaceFragment(new TablaFragment());

            TablaFragment tablaFragment = new TablaFragment();

            Bundle args = new Bundle();
            args.putInt("idCategoria", idCategoria);
            args.putString("nombreCategoria", nombreCategoria);
            tablaFragment.setArguments(args);

            replaceFragment(tablaFragment);
        });
        containerCategorias.addView(boton);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void ConsultaNombre(String id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            AdminSQL aBD = new AdminSQL(requireContext(), "usuarios", null, 1);
            db = aBD.getReadableDatabase();

            if (db != null) {
                String query = "SELECT usuario, correo FROM usuarios WHERE id = ?";
                cursor = db.rawQuery(query, new String[]{id});

                // Verificar si el cursor tiene datos
                if (cursor.moveToFirst()) {
                    // Extraer los datos del usuario
                    String nombreUsuario = cursor.getString(0);

                    NomUsuario.setText("Hola "+nombreUsuario);
                } else {
                    Toast.makeText(requireContext(), "Nombre no encontrado", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(requireContext(), "Error en la BD", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }




}
