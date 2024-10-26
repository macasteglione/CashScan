package com.example.reconocimiento_billetes.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.reconocimiento_billetes.domain.BillData

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "billDatabase.db"
        private const val TABLE_BILLS = "Bills"
        private const val KEY_ID = "id"
        private const val KEY_BILL = "bill"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_BILLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BILL + " INTEGER,"
                + KEY_DATE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BILLS")
        onCreate(db)
    }

    fun insertBill(value: Int, date: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_BILL, value)
            put(KEY_DATE, date)
        }
        val success = db.insert(TABLE_BILLS, null, contentValues)
        db.close()
        return success
    }

    fun getAllBills(): List<BillData> {
        val billList = mutableListOf<BillData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_BILLS", null)
        if (cursor.moveToFirst())
            do {
                val bill = BillData(
                    value = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BILL)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE))
                )
                billList.add(bill)
            } while (cursor.moveToNext())
        cursor.close()
        db.close()
        return billList
    }

    fun deleteAllBills() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_BILLS")
        db.close()
    }

    fun getTotalAmount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM($KEY_BILL) FROM $TABLE_BILLS", null)
        var total = 0

        if (cursor.moveToFirst())
            total = cursor.getInt(0)

        cursor.close()
        return total
    }
}