package com.example.pantallas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "libros.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        private const val COL_TELEFONO = "telefono"

        private const val TABLE_FAVORITOS = "favoritos_libros"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_LIBRO_ID = "libro_id"
        private const val COL_TITULO = "titulo"
        private const val COL_AUTOR = "autor"
        private const val COL_EDITORIAL = "editorial"
        private const val COL_PAGINAS = "paginas"
        private const val COL_FECHA = "fecha"
        private const val COL_IMAGEN = "imagen"

        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT NOT NULL,
            $COL_CORREO TEXT NOT NULL,
            $COL_PASSWORD TEXT NOT NULL,
            $COL_TELEFONO TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS(
            $COL_ID_FAVORITO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_LIBRO_ID INTEGER UNIQUE NOT NULL,
            $COL_TITULO TEXT NOT NULL,
            $COL_AUTOR TEXT,
            $COL_EDITORIAL TEXT,
            $COL_PAGINAS INTEGER,
            $COL_FECHA TEXT,
            $COL_IMAGEN TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, correo: String, password: String, telefono: String): Boolean {
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

    fun inisiarSesion(corre: String, password: String): Boolean {
        val db = readableDatabase
        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(query_sql, arrayOf(corre, password))
        val usuarioEncontrado = cursor.count > 0
        cursor.close()
        db.close()
        return usuarioEncontrado
    }

    fun guardarLibroFavorito(libroId: Long, titulo: String, autor: String, editorial: String, paginas: Int, fecha: String, imagen: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(COL_LIBRO_ID, libroId)
            put(COL_TITULO, titulo)
            put(COL_AUTOR, autor)
            put(COL_EDITORIAL, editorial)
            put(COL_PAGINAS, paginas)
            put(COL_FECHA, fecha)
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
}