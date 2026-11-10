
package com.example.pantallas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// CREAR CLASE DATABASEHELPER QUE HEREDA DE SQLITEOPENHELPER
class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    // DATOS DE LA BASE DE DATOS
    companion object {

        // INFORMACION DE LA BD
        private const val DATABASE_NAME = "libros.db"
        private const val DATABASE_VERSION = 3

        // INFORMACION DE LA TABLA USUARIOS
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        private const val COL_TELEFONO = "telefono"

        // INFORMACION DE LA TABLA FAVORITOS
        private const val TABLE_FAVORITOS = "favoritos_libros"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_LIBRO_ID = "libro_id"
        private const val COL_TITULO = "titulo"
        private const val COL_AUTOR = "autor"
        private const val COL_ANIO = "anio"
        private const val COL_IMAGEN = "imagen"

        // CREAR TABLA DE USUARIOS
        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT NOT NULL,
            $COL_CORREO TEXT NOT NULL,
            $COL_PASSWORD TEXT NOT NULL,
            $COL_TELEFONO TEXT NOT NULL
            )
        """

        // CREAR TABLA DE FAVORITOS
        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS(
            $COL_ID_FAVORITO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_LIBRO_ID TEXT UNIQUE NOT NULL,
            $COL_TITULO TEXT NOT NULL,
            $COL_AUTOR TEXT,
            $COL_ANIO INTEGER,
            $COL_IMAGEN TEXT
            )
        """
    }

    // METODO ONCREATE PARA CREAR LAS TABLAS
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)

    }

    // METODO ONUPGRADE PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 3) {

            // CAMBIAR LA TABLA ANTERIOR
            db?.execSQL(
                "ALTER TABLE $TABLE_FAVORITOS RENAME TO favoritos_libros_antigua"
            )

            // CREAR LA NUEVA TABLA
            db?.execSQL(CREATE_TABLE_FAVORITOS)

            // COPIAR LOS DATOS ANTERIORES
            db?.execSQL("""
                INSERT OR IGNORE INTO $TABLE_FAVORITOS
                ($COL_LIBRO_ID, $COL_TITULO, $COL_AUTOR, $COL_ANIO, $COL_IMAGEN)
                SELECT
                CAST(libro_id AS TEXT),
                titulo,
                autor,
                CASE
                    WHEN editorial GLOB '[0-9]*'
                    THEN CAST(editorial AS INTEGER)
                    ELSE NULL
                END,
                imagen
                FROM favoritos_libros_antigua
            """)

            // ELIMINAR LA TABLA ANTERIOR
            db?.execSQL("DROP TABLE favoritos_libros_antigua")
        }
    }

    // METODO PARA REGISTRAR UN USUARIO
    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        telefono: String
    ): Boolean {

        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
            put(COL_TELEFONO, telefono)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        db.close()

        return resultado != -1L
    }

    // METODO PARA INICIAR SESION
    fun inisiarSesion(corre: String, password: String): Boolean {

        val db = readableDatabase

        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(
            query_sql,
            arrayOf(corre, password)
        )

        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado
    }

    // METODO PARA GUARDAR UN LIBRO EN FAVORITOS
    fun guardarLibroFavorito(
        libroId: String,
        titulo: String,
        autor: String,
        anio: Int,
        imagen: String
    ): Boolean {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put(COL_LIBRO_ID, libroId)
            put(COL_TITULO, titulo)
            put(COL_AUTOR, autor)
            put(COL_ANIO, anio)
            put(COL_IMAGEN, imagen)
        }

        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            valores,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        db.close()

        return resultado != -1L
    }

    // METODO PARA ELIMINAR UN LIBRO DE FAVORITOS
    fun eliminarLibroFavorito(libroId: String): Boolean {

        val db = writableDatabase

        val filas_afectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_LIBRO_ID = ?",
            arrayOf(libroId)
        )

        db.close()

        return filas_afectadas > 0
    }

    // METODO PARA VERIFICAR SI UN LIBRO ESTA REGISTRADO
    fun existeLibroFavorito(libroId: String): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_LIBRO_ID FROM $TABLE_FAVORITOS WHERE $COL_LIBRO_ID = ?",
            arrayOf(libroId)
        )

        val existe = cursor.moveToFirst()

        cursor.close()
        db.close()

        return existe
    }
}
