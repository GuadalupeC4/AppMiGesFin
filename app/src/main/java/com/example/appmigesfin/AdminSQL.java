package com.example.appmigesfin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQL extends SQLiteOpenHelper {
    // Definición de la tabla
    String TablaUsuario = "CREATE TABLE usuarios(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "usuario TEXT, " +
            "correo TEXT, " +
            "contraseña TEXT)";

    String TablaCategorias = "CREATE TABLE categorias(" +
            "id_Categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_Usuario INTEGER, " +
            "nombre TEXT, " +
            "FOREIGN KEY(id_Usuario) REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE)";
    String TablaMovimientos = "CREATE TABLE movimientos(" +
            "id_Movimiento INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_Categoria INTEGER, " +
            "fecha TEXT, " +
            "descripcion TEXT, " +
            "cantidad TEXT," +
            "tipo TEXT, " +
            "FOREIGN KEY(id_Categoria) REFERENCES categorias(id_Categoria) ON DELETE CASCADE ON UPDATE CASCADE)";

    public AdminSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla usuario
        db.execSQL(TablaUsuario);
        db.execSQL(TablaCategorias);
        db.execSQL(TablaMovimientos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina la tabla usuario si existe y luego la recrea
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        onCreate(db);
    }
}




