package com.example.reconocimiento_billetes.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.reconocimiento_billetes.domain.BillData

/**
 * Esta clase ayuda a interactuar con una base de datos SQLite para almacenar, recuperar y gestionar
 * información sobre billetes en la aplicación.
 * Hereda de [SQLiteOpenHelper], proporcionando métodos para crear y actualizar la base de datos.
 */
class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "billDatabase.db"

        // Nombre de la tabla en la base de datos
        private const val TABLE_BILLS = "Bills"

        // Columnas de la tabla
        private const val KEY_ID = "id"
        private const val KEY_BILL = "bill"
        private const val KEY_DATE = "date"

        // Consultas SQL como constantes
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_BILLS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_BILL INTEGER,
                $KEY_DATE TEXT
            )
        """

        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_BILLS"
        private const val SQL_SELECT_ALL_BILLS = "SELECT * FROM $TABLE_BILLS"
        private const val SQL_DELETE_ALL_BILLS = "DELETE FROM $TABLE_BILLS"
        private const val SQL_SUM_BILLS = "SELECT SUM($KEY_BILL) FROM $TABLE_BILLS"
    }

    /**
     * Aquí se define la estructura de la base de datos y se ejecuta la consulta de creación de la tabla.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * Se ejecuta cuando se actualiza la base de datos (cuando se cambia la versión).
     * En este caso, elimina la tabla existente y crea una nueva.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DROP_TABLE)
        onCreate(db)
    }

    /**
     * Inserta un nuevo billete en la base de datos.
     *
     * @param value El valor del billete que se inserta.
     * @param date La fecha asociada al billete.
     * @return El ID del nuevo registro insertado en la base de datos.
     */
    fun insertBill(value: Int, date: String): Long {
        // Crea un ContentValues para almacenar los datos del billete.
        val contentValues = ContentValues().apply {
            put(KEY_BILL, value)
            put(KEY_DATE, date)
        }

        // Inserta el billete en la base de datos y retorna el ID del nuevo registro.
        return writableDatabase.use { db ->
            db.insert(TABLE_BILLS, null, contentValues)
        }
    }

    /**
     * Obtiene todos los billetes almacenados en la base de datos.
     *
     * @return Una lista de objetos [BillData] representando todos los billetes.
     */
    fun getAllBills(): List<BillData> {
        val billList = mutableListOf<BillData>()

        // Consulta la base de datos y recorre el cursor para obtener todos los billetes.
        readableDatabase.use { db ->
            db.rawQuery(SQL_SELECT_ALL_BILLS, null).use { cursor ->
                while (cursor.moveToNext())
                    billList.add(cursor.toBillData())
            }
        }

        return billList
    }

    /**
     * Elimina todos los billetes de la base de datos.
     */
    fun deleteAllBills() {
        writableDatabase.use { db ->
            db.execSQL(SQL_DELETE_ALL_BILLS)
        }
    }

    /**
     * Calcula el total de los valores de todos los billetes almacenados.
     *
     * @return El total de los valores de los billetes.
     */
    fun getTotalAmount(): Int {
        readableDatabase.use { db ->
            db.rawQuery(SQL_SUM_BILLS, null).use { cursor ->
                // Si hay un resultado, retorna la suma; de lo contrario, retorna 0.
                return if (cursor.moveToFirst()) cursor.getInt(0) else 0
            }
        }
    }

    /**
     * Convierte un cursor de la base de datos a un objeto [BillData].
     * Este método ayuda a transformar cada fila del cursor en un objeto que represente los datos del billete.
     *
     * @return Un objeto [BillData] con los datos extraídos del cursor.
     */
    private fun android.database.Cursor.toBillData(): BillData {
        return BillData(
            value = getInt(getColumnIndexOrThrow(KEY_BILL)),
            date = getString(getColumnIndexOrThrow(KEY_DATE))
        )
    }
}