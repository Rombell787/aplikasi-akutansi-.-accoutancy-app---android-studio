package com.akutansikantor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class KeuanganDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "keuangan.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "keuangan"
        const val COLUMN_ID = "id"
        const val COLUMN_BULAN = "bulan"
        const val COLUMN_KETERANGAN = "keterangan"
        const val COLUMN_PEMASUKAN = "pemasukan"
        const val COLUMN_PENGELUARAN = "pengeluaran"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BULAN TEXT,
                $COLUMN_KETERANGAN TEXT,
                $COLUMN_PEMASUKAN INTEGER,
                $COLUMN_PENGELUARAN INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // --- Fungsi CRUD (Create, Read, Update, Delete) ---

    // CREATE
    fun insertData(data: KeuanganModel) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BULAN, data.bulan)
            put(COLUMN_KETERANGAN, data.keterangan)
            put(COLUMN_PEMASUKAN, data.pemasukan)
            put(COLUMN_PENGELUARAN, data.pengeluaran)
        }
        db.insert(TABLE_NAME, null, values)
    }

    // READ
    fun getAllData(): List<KeuanganModel> {
        val list = mutableListOf<KeuanganModel>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_ID DESC")

        with(cursor) {
            while (moveToNext()) {
                val item = KeuanganModel(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    bulan = getString(getColumnIndexOrThrow(COLUMN_BULAN)),
                    keterangan = getString(getColumnIndexOrThrow(COLUMN_KETERANGAN)),
                    pemasukan = getInt(getColumnIndexOrThrow(COLUMN_PEMASUKAN)),
                    pengeluaran = getInt(getColumnIndexOrThrow(COLUMN_PENGELUARAN))
                )
                list.add(item)
            }
        }
        cursor.close()
        return list
    }

    fun getDataById(catatanId: Int): KeuanganModel {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $catatanId", null)

        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val bulan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BULAN))
        val keterangan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN))
        val pemasukan = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PEMASUKAN))
        val pengeluaran = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENGELUARAN))
        cursor.close()
        db.close()

        return KeuanganModel(id, bulan, keterangan, pemasukan, pengeluaran)
    }

    fun updateData(data: KeuanganModel) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BULAN, data.bulan)
            put(COLUMN_KETERANGAN, data.keterangan)
            put(COLUMN_PEMASUKAN, data.pemasukan)
            put(COLUMN_PENGELUARAN, data.pengeluaran)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(data.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getAllDataByBulan(bulan: String): List<KeuanganModel> {
        val list = mutableListOf<KeuanganModel>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_BULAN = ?", arrayOf(bulan), null, null, "$COLUMN_ID DESC")

        with(cursor) {
            while (moveToNext()) {
                val item = KeuanganModel(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    bulan = getString(getColumnIndexOrThrow(COLUMN_BULAN)),
                    keterangan = getString(getColumnIndexOrThrow(COLUMN_KETERANGAN)),
                    pemasukan = getInt(getColumnIndexOrThrow(COLUMN_PEMASUKAN)),
                    pengeluaran = getInt(getColumnIndexOrThrow(COLUMN_PENGELUARAN))
                )
                list.add(item)
            }
        }
        cursor.close()
        return list
    }

    // DELETE
    fun deleteData(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Di dalam kelas KeuanganDBHelper
    fun getMonthlySummary(): List<KeuanganModel> {
        val db = readableDatabase
        val summaryList = mutableListOf<KeuanganModel>()
        val query = "SELECT $COLUMN_BULAN, SUM($COLUMN_PEMASUKAN) AS total_pemasukan, SUM($COLUMN_PENGELUARAN) AS total_pengeluaran FROM $TABLE_NAME GROUP BY $COLUMN_BULAN"

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val bulan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BULAN))
                val totalPemasukan = cursor.getInt(cursor.getColumnIndexOrThrow("total_pemasukan"))
                val totalPengeluaran = cursor.getInt(cursor.getColumnIndexOrThrow("total_pengeluaran"))

                summaryList.add(KeuanganModel(
                    id = 0, // ID tidak relevan untuk ringkasan
                    bulan = bulan,
                    keterangan = "", // Keterangan tidak relevan
                    pemasukan = totalPemasukan,
                    pengeluaran = totalPengeluaran
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return summaryList
    }


}