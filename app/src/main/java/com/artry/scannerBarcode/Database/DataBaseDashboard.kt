package com.artry.scannerBarcode.Database

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.artry.scannerBarcode.Model.DashboardModel

const val DATABASENAME = "scannerAppsLogistik"
const val TABLENAMEDASHBOARD = "Dashboard"
const val TABLENAMEPAKET = "Paket"
const val COL_ID = "id"
const val COL_NAME = "paket"
const val COL_DATE = "date"

class DataBaseDashboard (var context : Context) : SQLiteOpenHelper(context, DATABASENAME, null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAMEDASHBOARD + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " VARCHAR(256)," +
                COL_DATE + " VARCHAR(256)" +
                " )"
        val createTable2 = "CREATE TABLE " + TABLENAMEPAKET + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "code_barcode VARCHAR(256), " +
                "date_scan VARCHAR(256), " +
                "id_paket INTEGER(4)" +
                " )"
        p0?.execSQL(createTable)
        p0?.execSQL(createTable2)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLENAMEDASHBOARD")
        p0?.execSQL("DROP TABLE IF EXISTS $TABLENAMEPAKET")
        onCreate(p0)
    }

    fun insertData(data: DashboardModel) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, data.paket)
        contentValues.put(COL_DATE, data.date)

        val result = database.insert(TABLENAMEDASHBOARD, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            val editor: SharedPreferences.Editor = context.getSharedPreferences("scanPref",
                AppCompatActivity.MODE_PRIVATE
            ).edit()
            editor.putInt("versi", 1)
            editor.apply()
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        database.close()
    }

    fun readData(): MutableList<DashboardModel> {
        val list: MutableList<DashboardModel> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAMEDASHBOARD"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val data = DashboardModel()
                data.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
                data.paket = result.getString(result.getColumnIndexOrThrow(COL_NAME))
                data.date = result.getString(result.getColumnIndexOrThrow(COL_DATE))
                list.add(data)
            }
            while (result.moveToNext())
        }
        db.close()
        return list
    }

    fun removeData(id: Int) {
        val database = this.writableDatabase
        database.execSQL("DELETE FROM $TABLENAMEDASHBOARD WHERE $COL_ID= $id")
        database.execSQL("DELETE FROM $TABLENAMEPAKET WHERE id_paket = $id")
        database.close()
    }

}
