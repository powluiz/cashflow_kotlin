package com.powluiz.cashflow_app.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cashflow_app.db"
        private const val DATABASE_VERSION = 1

        private const val KEY_TABLE_TRANSACTION = "cash_transactions"
        private const val KEY_COLUMN_ID = "_id"
        private const val KEY_COLUMN_DATE = "date"
        private const val KEY_COLUMN_DETAIL = "detail"
        private const val KEY_COLUMN_VALUE = "value"
        private const val KEY_COLUMN_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
        CREATE TABLE $KEY_TABLE_TRANSACTION(
            $KEY_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $KEY_COLUMN_TYPE TEXT NOT NULL,
            $KEY_COLUMN_DETAIL TEXT NOT NULL,
            $KEY_COLUMN_VALUE REAL NOT NULL,
            $KEY_COLUMN_DATE TEXT NOT NULL
        );
    """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $KEY_TABLE_TRANSACTION")
        onCreate(db)
    }


    fun createTransaction(transaction: CashTransaction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_COLUMN_VALUE, transaction.value)
            put(KEY_COLUMN_DATE, transaction.date)
            put(KEY_COLUMN_TYPE, transaction.type.name)
            put(KEY_COLUMN_DETAIL, transaction.detail.name)
        }
        return db.insert(KEY_TABLE_TRANSACTION, null, values)
    }

    fun deleteTransaction(transactionId: Int) {
        val db = writableDatabase
        db.delete(KEY_TABLE_TRANSACTION, "$KEY_COLUMN_ID = ?", arrayOf(transactionId.toString()))
    }

    @SuppressLint("Range")
    fun getAllTransactions(): List<CashTransaction> {
        val db = readableDatabase
        val transactions = mutableListOf<CashTransaction>()

        val cursor = db.query(
            KEY_TABLE_TRANSACTION,
            arrayOf(KEY_COLUMN_ID, KEY_COLUMN_TYPE, KEY_COLUMN_DETAIL, KEY_COLUMN_VALUE, KEY_COLUMN_DATE),
            null,  // (SELECT * FROM table)
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_COLUMN_ID))
                val type = TransactionType.valueOf(cursor.getString(cursor.getColumnIndex(KEY_COLUMN_TYPE)))
                val detail = TransactionDetail.valueOf(cursor.getString(cursor.getColumnIndex(KEY_COLUMN_DETAIL)))
                val value = cursor.getDouble(cursor.getColumnIndex(KEY_COLUMN_VALUE))
                val date = cursor.getString(cursor.getColumnIndex(KEY_COLUMN_DATE))

                transactions.add(CashTransaction(id, value, type, detail, date))
            } while (cursor.moveToNext())
            cursor.close()
        }

        return transactions
    }


}
