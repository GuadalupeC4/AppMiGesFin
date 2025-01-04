package com.example.appmigesfin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appmigesfin.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton agrega;
    final Calendar myCalender = Calendar.getInstance();
    EditText eTFecha;
    AdminSQL aBD;
    SQLiteDatabase db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        agrega = findViewById(R.id.bottomAgregar);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            }else if (itemId == R.id.perfil) {
                replaceFragment(new PerfilFragment());
            }

            return true;
        });

        binding.bottomAgregar.setOnClickListener(view -> showBottomDialog());

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomagragar);

        // Spinner de Categorías
        Spinner spnCategoria = dialog.findViewById(R.id.spnCategorias);
        Map<String, Integer> categoriasMap = new LinkedHashMap<>(); // Para almacenar nombres e IDs
        List<String> listCategoria = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("idUsuario", -1);

        if (idUsuario != -1) {
            try {
                AdminSQL admin = new AdminSQL(this, "categorias", null, 1);
                SQLiteDatabase db = admin.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT id_Categoria, nombre FROM categorias WHERE id_Usuario = ?", new String[]{String.valueOf(idUsuario)});

                if (cursor.moveToFirst()) {
                    do {
                        int idCategoria = cursor.getInt(0); // Primera columna: id
                        String nombreCategoria = cursor.getString(1); // Segunda columna: nombre
                        categoriasMap.put(nombreCategoria, idCategoria);
                        listCategoria.add(nombreCategoria); // Solo nombres para el Spinner
                    } while (cursor.moveToNext());
                } else {
                    listCategoria.add("No hay categorías disponibles");
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                Toast.makeText(this, "Error al cargar categorías: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_LONG).show();
            listCategoria.add("Error al cargar categorías");
        }

        // Configura el adaptador del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCategoria);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(adapter);

        // Calendario
        EditText eTFechaDialog = dialog.findViewById(R.id.eTFecha);
        eTFechaDialog.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, (datePicker, year, month, dayOfMonth) -> {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MMM-yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                eTFechaDialog.setText(dateFormat.format(myCalendar.getTime()));
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
        });

        //inicializacion de elementos
        EditText eTDescripcion = dialog.findViewById(R.id.eTDescripcion);
        EditText eTCantidad = dialog.findViewById(R.id.eTCantidad);
        RadioGroup rgTipo = dialog.findViewById(R.id.rgTipo);
        Button btnMovimiento = dialog.findViewById(R.id.btnAggMovimiento);

        // Botón agregar movimiento
        btnMovimiento.setOnClickListener(view -> {
            String categoriaSeleccionada = spnCategoria.getSelectedItem().toString();
            String fecha = eTFechaDialog.getText().toString().trim();
            String descripcion = eTDescripcion.getText().toString().trim();
            String cantidad = eTCantidad.getText().toString().trim();
            String tipo = "";

            // Determina si es ingreso o gasto
            int idSel = rgTipo.getCheckedRadioButtonId();
            if (idSel == R.id.rBIngreso) {
                tipo = "Ingreso";
            } else if (idSel == R.id.rBGasto) {
                tipo = "Gasto";
            }

            // Validaciones
            if (categoriaSeleccionada.equals("No hay categorías disponibles") || categoriaSeleccionada.equals("Error al cargar categorías")) {
                Toast.makeText(this, "Por favor, selecciona una categoría válida", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fecha.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Id de la categoria seleccionada
            int idCategoria = categoriasMap.get(categoriaSeleccionada);

            // Insertar el movimiento
            insertarMovimiento(idCategoria, fecha, descripcion, cantidad, tipo);
            dialog.dismiss();
        });

        // Botón cerrar diálogo
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        // Configuración de estilos del diálogo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }

    private void insertarMovimiento(int idCategoria, String fecha, String descripcion, String cantidad, String tipo) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("idUsuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        try (SQLiteDatabase db = new AdminSQL(this, "movimientos", null, 1).getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("id_Categoria", idCategoria); // Relación con categoría
            valores.put("fecha", fecha);
            valores.put("descripcion", descripcion);
            valores.put("cantidad", cantidad);
            valores.put("tipo", tipo);

            long resultado = db.insert("movimientos", null, valores);
            if (resultado != -1) {
                Toast.makeText(this, "Movimiento agregado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al agregar el movimiento", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



}