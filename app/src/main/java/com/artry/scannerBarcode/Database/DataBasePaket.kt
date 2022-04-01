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
import com.artry.scannerBarcode.Model.PaketModel
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DataBasePaket (var context : Context) : SQLiteOpenHelper(context, DATABASENAME, null, 1) {
    val COL_CODE = "code_barcode"
    val COL_DATEPAKET = "date_scan"
    val COL_IDPAKET = "id_paket"
    val calendarToday = Calendar.getInstance()
    val simple = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(p0: SQLiteDatabase?) {}

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insertData(data: PaketModel) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_CODE, data.codepaket)
        contentValues.put(COL_DATEPAKET, data.date)
        contentValues.put(COL_IDPAKET, data.idpaket)

        val result = database.insert(TABLENAMEPAKET, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        database.close()
    }

    fun check(code:String) : Int {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        val sql = "SELECT $COL_CODE FROM $TABLENAMEPAKET WHERE $COL_CODE='$code'"
        cursor = db.rawQuery(sql, null)
        return cursor.count
    }

    fun readData(idpaket:Int): MutableList<PaketModel> {
        val list: MutableList<PaketModel> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLENAMEPAKET WHERE $COL_IDPAKET=$idpaket"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val data = PaketModel()
                data.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
                data.codepaket = result.getString(result.getColumnIndexOrThrow(COL_CODE))
                data.date = result.getString(result.getColumnIndexOrThrow(COL_DATEPAKET))
                data.idpaket = result.getInt(result.getColumnIndexOrThrow(COL_IDPAKET))
                list.add(data)
            }
            while (result.moveToNext())
        }
        db.close()
        return list
    }

    fun removeData(id: Int) {
        val database = this.writableDatabase
        database.execSQL("DELETE FROM $TABLENAMEPAKET WHERE $COL_ID= $id")
        database.close()
    }

    fun exportDB(idpaket:Int, namapaket:String){
        val state = Environment.getExternalStorageState()
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return
        } else {
            val exportDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!exportDir.exists()) { exportDir.mkdirs() }
            val file: File
            var printWriter: PrintWriter? = null
            try {
                file = File(exportDir, "HJL SCANNER [${namapaket.uppercase()}, ${simple.format(calendarToday.time)}].csv")
                file.createNewFile()
                printWriter = PrintWriter(FileWriter(file))
                val db = this.readableDatabase
                val dataRaw = readData(idpaket)

                printWriter.println("PAKET YANG TELAH DI SCAN")
                printWriter.println("ID, CODE PAKET, DATE, PAKET")
                for (item in dataRaw) {
                    val record: String = item.id.toString() + "," + item.codepaket + "," + item.date + "," + namapaket
                    printWriter.println(record)
                }
                db.close()
            } catch (exc: Exception) {
                Toast.makeText(context, "Export Gagal, ${exc.printStackTrace()}", Toast.LENGTH_SHORT).show()
                return
            }

            finally {
                Toast.makeText(context, "Export Berhasil, silahkan cek di folder ${Environment.DIRECTORY_DOWNLOADS}", Toast.LENGTH_SHORT).show()
                printWriter?.close()
            }
        }
    }
}
