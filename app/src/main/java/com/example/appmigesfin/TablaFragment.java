package com.example.appmigesfin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TablaFragment extends Fragment {
    AdminSQL aBD;
    SQLiteDatabase db = null;
    private TableLayout tableLayout;

    TextView tvNomCategoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabla, container, false);


        tableLayout = view.findViewById(R.id.tableLayout);
        tvNomCategoria = view.findViewById(R.id.NomCategoria);

        //Identificar la categoria seleccionada
        int idCategoria = -1;
        String nombreCategoria = "Sin categoría";

        if (getArguments() != null) {
            idCategoria = getArguments().getInt("idCategoria", -1);
            nombreCategoria = getArguments().getString("nombreCategoria", "Home");
            //tvNomCategoria.setText(nombreCategoria); // Mostrar el nombre de la categoría
        }
        tvNomCategoria.setText(nombreCategoria);

        cargarMovimientosDesdeBD(idCategoria);

        return view;
    }

    private void cargarMovimientosDesdeBD(int idCategoria) {
        AdminSQL admin = new AdminSQL(getContext(), "movimientos", null, 1);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Acumulables
        double totalIngresos = 0;
        double totalGastos = 0;
        double total = 0;

        try {
            db = admin.getReadableDatabase();

            String query = "SELECT id_Movimiento, fecha, descripcion, cantidad, tipo FROM movimientos WHERE id_Categoria = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(idCategoria)});

            while (cursor.moveToNext()) {
                int idMovimiento = cursor.getInt(0);
                String fecha = cursor.getString(1) != null ? cursor.getString(1) : "Sin fecha";
                String descripcion = cursor.getString(2) != null ? cursor.getString(2) : "Sin descripción";
                String cantidadStr = cursor.getString(3);
                String tipo = cursor.getString(4);

                double cantidadNumerica = 1;
                if (cantidadStr != null && !cantidadStr.isEmpty()) {
                    cantidadNumerica = Double.parseDouble(cantidadStr);
                }

                // Sumatorias de Ingresos y Gastos
                if ("Ingreso".equals(tipo)) {
                    totalIngresos += cantidadNumerica;
                    agregarFilaATabla(idMovimiento, fecha, descripcion, String.format("%.2f", cantidadNumerica), "");
                } else if ("Gasto".equals(tipo)) {
                    totalGastos += cantidadNumerica;
                    agregarFilaATabla(idMovimiento, fecha, descripcion, "", String.format("%.2f", cantidadNumerica));
                }

                total = totalIngresos - totalGastos;
            }

            //Metodo para agregar filas
            agregarFilaTotales(totalIngresos, totalGastos);
            agregarFilaTotal(total);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar movimientos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void agregarFilaATabla(int idMovimiento, String fecha, String descripcion, String ingreso, String gasto) {
        // Crear una nueva fila
        TableRow nuevaFila = new TableRow(getContext());

        // Creacion de celdas
        TextView tvFecha = new TextView(getContext());
        tvFecha.setText(fecha);
        tvFecha.setPadding(40, 10, 20, 10);
        tvFecha.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView tvDescripcion = new TextView(getContext());
        tvDescripcion.setText(descripcion);
        tvDescripcion.setPadding(10, 10, 20, 10);
        tvDescripcion.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        tvDescripcion.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView tvIngreso = new TextView(getContext());
        tvIngreso.setText(ingreso);
        tvIngreso.setPadding(10, 10, 20, 10);
        tvIngreso.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView tvGasto = new TextView(getContext());
        tvGasto.setText(gasto);
        tvGasto.setPadding(10, 10, 20, 10);
        tvGasto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // fila
        nuevaFila.addView(tvFecha);
        nuevaFila.addView(tvDescripcion);
        nuevaFila.addView(tvIngreso);
        nuevaFila.addView(tvGasto);

        nuevaFila.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Descripción: " + descripcion, Toast.LENGTH_SHORT).show();
            consultaBottomDialog(idMovimiento);
        });

        // Agregar la fila
        tableLayout.addView(nuevaFila);
    }

    private void agregarFilaTotales(double totalIngresos, double totalGastos) {
        TableRow filaTotales = new TableRow(getContext());

        TextView tvFecha = new TextView(getContext());
        tvFecha.setText("Totales:");
        tvFecha.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvFecha.setPadding(10, 10, 00, 10);
        tvFecha.setTextColor(getResources().getColor(R.color.azull));

        TextView tvDescripcion = new TextView(getContext());
        tvDescripcion.setText("");
        tvDescripcion.setPadding(10, 10, 10, 10);

        TextView tvTotalIngresos = new TextView(getContext());
        tvTotalIngresos.setText(String.format("%.2f", totalIngresos)); // Mostrar con dos decimales
        tvTotalIngresos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTotalIngresos.setPadding(80, 10, 10, 10);
        tvTotalIngresos.setTextColor(getResources().getColor(R.color.azull));

        TextView tvTotalGastos = new TextView(getContext());
        tvTotalGastos.setText(String.format("%.2f", totalGastos)); // Mostrar con dos decimales
        tvTotalGastos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTotalGastos.setPadding(80, 10, 10, 10);
        tvTotalGastos.setTextColor(getResources().getColor(R.color.azull));

        // fila
        filaTotales.addView(tvFecha);
        filaTotales.addView(tvDescripcion);
        filaTotales.addView(tvTotalIngresos);
        filaTotales.addView(tvTotalGastos);

        // Agregar la fila
        tableLayout.addView(filaTotales);
    }

    private void agregarFilaTotal(double total) {
        TableRow filaTotales = new TableRow(getContext());

        TextView tvFecha = new TextView(getContext());
        tvFecha.setText("Total:");
        tvFecha.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvFecha.setPadding(10, 10, 00, 10);
        tvFecha.setTextColor(getResources().getColor(R.color.azul));

        TextView tvDescripcion = new TextView(getContext());
        tvDescripcion.setText(""); // Espacio vacío para "Descripción"
        tvDescripcion.setPadding(10, 10, 10, 10);

        TextView tvIngresos = new TextView(getContext());
        tvIngresos.setText(" "); // Mostrar con dos decimales
        tvIngresos.setPadding(10, 10, 10, 10);

        TextView tvTotal = new TextView(getContext());
        tvTotal.setText(String.format("%.2f", total)); // Mostrar con dos decimales
        tvTotal.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTotal.setPadding(80, 10, 10, 10);
        tvTotal.setTextColor(getResources().getColor(R.color.azul));

        // fila
        filaTotales.addView(tvFecha);
        filaTotales.addView(tvDescripcion);
        filaTotales.addView(tvIngresos);
        filaTotales.addView(tvTotal);

        // Agregar la fila
        tableLayout.addView(filaTotales);
    }

    private void consultaBottomDialog(int idMovimiento) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottonconsulta);

        // Inicializacion de elementos de bottonconsulta
        TextView tvCategoria = dialog.findViewById(R.id.tvCategoria);
        TextView tvFecha = dialog.findViewById(R.id.tvFecha);
        TextView tvDescripcion = dialog.findViewById(R.id.tvDescripcion);
        TextView tvCantidad = dialog.findViewById(R.id.tvCantidad);
        TextView tvTipo = dialog.findViewById(R.id.tvTipo);
        Button btnBorrar = dialog.findViewById(R.id.btnBorrar);
        Button btnEditar = dialog.findViewById(R.id.btnEditar);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        //cerrar dialogo
        cancelButton.setOnClickListener(view -> dialog.dismiss());


        AdminSQL adminSQL = new AdminSQL(getContext(), "movimientos", null, 1);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = adminSQL.getReadableDatabase();
            //String query = "SELECT id_Categoria, fecha, descripcion, cantidad, tipo FROM movimientos WHERE id_Movimiento = ?";
            String query = "SELECT categorias.nombre, movimientos.fecha, movimientos.descripcion, movimientos.cantidad, movimientos.tipo FROM movimientos LEFT JOIN categorias ON movimientos.id_Categoria = categorias.id_Categoria WHERE movimientos.id_Movimiento = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(idMovimiento)});

            if (cursor != null && cursor.moveToFirst()) {
                // Asignar los datos a las vistas
                String categoria = cursor.getString(0); // Nombre de la categoría
                String fecha = cursor.getString(1);
                String descripcion = cursor.getString(2);
                String cantidad = cursor.getString(3);
                String tipo = cursor.getString(4);


                tvCategoria.setText(categoria != null ? "Categoría: " + categoria : "Categoría: Sin categoría");
                tvFecha.setText(fecha != null ? "Fecha: " + fecha : "Fecha: Sin fecha");
                tvDescripcion.setText(descripcion != null ? "Descripción: " + descripcion : "Descripción: Sin descripción");
                tvCantidad.setText(cantidad != null ? "Cantidad: " + cantidad : "Cantidad: 0");
                tvTipo.setText(tipo != null ? "Tipo: " + tipo : "Tipo: Sin tipo");
            } else {
                Toast.makeText(getContext(), "Movimiento no encontrado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar los detalles: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        // Configurar acciones de los botones
        btnBorrar.setOnClickListener(view -> {
            Borrar(idMovimiento);
            dialog.dismiss();
       });
        btnEditar.setOnClickListener(view -> {
            EditarBottomDialog(idMovimiento);
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }

    private void Borrar(int idMovimiento){

        try {
            aBD = new AdminSQL(getContext(), "movimientos", null, 1);
            db = aBD.getWritableDatabase();

            String whereClause = "id_Movimiento = ?";
            String[] whereArgs = {String.valueOf(idMovimiento)};

            int rowsDeleted = db.delete("movimientos", whereClause, whereArgs);

            if (rowsDeleted > 0) {
                Toast.makeText(getContext(), "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) db.close();
        }

    }

    private void EditarBottomDialog(int idMovimiento) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottoneditar);

        // Spinner de Categorías
        Spinner spnCategoria = dialog.findViewById(R.id.spnCategorias);
        Map<String, Integer> categoriasMap = new LinkedHashMap<>();
        List<String> listCategoria = new ArrayList<>();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("idUsuario", -1);

        if (idUsuario != -1) {
            try {
                AdminSQL admin = new AdminSQL(getContext(), "categorias", null, 1);
                SQLiteDatabase db = admin.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT id_Categoria, nombre FROM categorias WHERE id_Usuario = ?", new String[]{String.valueOf(idUsuario)});

                if (cursor.moveToFirst()) {
                    do {
                        int idCategoria = cursor.getInt(0);
                        String nombreCategoria = cursor.getString(1);
                        categoriasMap.put(nombreCategoria, idCategoria);
                        listCategoria.add(nombreCategoria);
                    } while (cursor.moveToNext());
                } else {
                    listCategoria.add("No hay categorías disponibles");
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al cargar categorías: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_LONG).show();
            listCategoria.add("Error al cargar categorías");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listCategoria);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(adapter);

        //Retornar movimiento
        EditText eTFechaDialog = dialog.findViewById(R.id.eTFecha);
        EditText eTDescripcion = dialog.findViewById(R.id.eTDescripcion);
        EditText eTCantidad = dialog.findViewById(R.id.eTCantidad);
        RadioGroup rgTipo = dialog.findViewById(R.id.rgTipo);

        try {
            AdminSQL admin = new AdminSQL(getContext(), "movimientos", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT fecha, descripcion, cantidad, tipo, id_Categoria FROM movimientos WHERE id_Movimiento = ?", new String[]{String.valueOf(idMovimiento)});

            if (cursor.moveToFirst()) {
                String fecha = cursor.getString(0);
                String descripcion = cursor.getString(1);
                String cantidad = cursor.getString(2);
                String tipo = cursor.getString(3);
                int idCategoria = cursor.getInt(4);

                eTFechaDialog.setText(fecha);
                eTDescripcion.setText(descripcion);
                eTCantidad.setText(cantidad);

                if (tipo.equals("Ingreso")) {
                    rgTipo.check(R.id.rBIngreso);
                } else if (tipo.equals("Gasto")) {
                    rgTipo.check(R.id.rBGasto);
                }

                ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) spnCategoria.getAdapter();
                int categoryIndex = categoryAdapter.getPosition(getKeyByValue(categoriasMap, idCategoria));
                spnCategoria.setSelection(categoryIndex);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar los datos del movimiento: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Calendario
        eTFechaDialog.setOnClickListener(view -> {
            new DatePickerDialog(getContext(), (datePicker, year, month, dayOfMonth) -> {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MMM-yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                eTFechaDialog.setText(dateFormat.format(myCalendar.getTime()));
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
        });

        //Boton EditarGuardar
        Button btnEditarGuardar = dialog.findViewById(R.id.btnEditarGuardar);
        btnEditarGuardar.setOnClickListener(view -> {
            String categoriaSeleccionada = spnCategoria.getSelectedItem().toString();
            String fecha = eTFechaDialog.getText().toString().trim();
            String descripcion = eTDescripcion.getText().toString().trim();
            String cantidad = eTCantidad.getText().toString().trim();
            int tipoSeleccionado = rgTipo.getCheckedRadioButtonId();

            if (categoriaSeleccionada.equals("No hay categorías disponibles") || categoriaSeleccionada.equals("Error al cargar categorías")) {
                Toast.makeText(getContext(), "Por favor, selecciona una categoría válida", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fecha.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || tipoSeleccionado == -1) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipo = (tipoSeleccionado == R.id.rBIngreso) ? "Ingreso" : "Gasto";
            int idCategoria = categoriasMap.get(categoriaSeleccionada);

            Editar(idMovimiento, fecha, descripcion, cantidad, tipo, idCategoria);
            dialog.dismiss();
        });

        //Cerrar dialogo
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        dialog.show();
    }

    private String getKeyByValue(Map<String, Integer> map, int value) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void Editar(int idMovimiento, String fecha, String descripcion, String cantidad, String tipo, int idCategoria) {
        if (fecha.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            aBD = new AdminSQL(getContext(), "movimientos", null, 1);
            db = aBD.getWritableDatabase();

            // Actualiza los datos en la base de datos
            String query = "UPDATE movimientos SET fecha = ?, descripcion = ?, cantidad = ?, tipo = ?, id_Categoria = ? WHERE id_Movimiento = ?";
            db.execSQL(query, new Object[]{fecha, descripcion, cantidad, tipo, idCategoria, idMovimiento});

            Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
