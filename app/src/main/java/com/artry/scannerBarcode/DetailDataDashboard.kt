package com.artry.scannerBarcode

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Instrumentation
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.artry.scannerBarcode.Adapter.DetailDataListAdapter
import com.artry.scannerBarcode.Database.DataBasePaket
import com.artry.scannerBarcode.Model.PaketModel
import com.artry.scannerBarcode.databinding.ActivityDetailDataDashboardBinding
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.*

import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.journeyapps.barcodescanner.ScanOptions
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.zxing.client.android.Intents

import com.journeyapps.barcodescanner.ScanContract

class DetailDataDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDetailDataDashboardBinding
    private lateinit var db : DataBasePaket
    val calendarToday = Calendar.getInstance()
    val simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var idpaket = 0
    var sharedpreferences: SharedPreferences? = null
    var namepaket = ""
    var datepaket = ""
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.getContents() == null) {
            val originalIntent: Intent = result.getOriginalIntent()
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(
                    this,
                    "Cancelled due to missing camera permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            val raw = PaketModel()
            raw.codepaket = result.contents
            raw.date = simple.format(calendarToday.time)
            raw.idpaket = idpaket
            if (db.check(result.contents) > 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Data sudah pernah discan")
                builder.setMessage("Apakah Anda ingin menyimpan data ini lagi?")
                builder.setPositiveButton("Simpan") { dialog, which ->
                    db.insertData(raw)
                    setAdapter()
                }
                builder.setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss() }
                builder.show()
            } else { db.insertData(raw)
                setAdapter()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDataDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DataBasePaket(this)
        sharedpreferences = this.getSharedPreferences("scanPref", AppCompatActivity.MODE_PRIVATE)

        idpaket = intent.getIntExtra("idpaket", 0)
        namepaket = intent.getStringExtra("namepaket").toString()
        datepaket = intent.getStringExtra("datepaket").toString()
        binding.namePaket.text = "Paket: $namepaket"
        binding.datePaket.text = "dibuat pada: $datepaket"

        binding.btnScan.setOnClickListener {
            val options = ScanOptions().setOrientationLocked(false).setCaptureActivity(
                ScannerActivity::class.java
            )
            barcodeLauncher.launch(options)

//            val intentIntegrator = IntentIntegrator(this)
//            intentIntegrator.setCaptureActivity(ScannerActivity::class.java)
//            intentIntegrator.setBeepEnabled(false)
//            intentIntegrator.setCameraId(0)
//            intentIntegrator.setPrompt("SCAN PAKET")
//            intentIntegrator.setOrientationLocked(false)
//            intentIntegrator.setTorchEnabled(true)
//            intentIntegrator.initiateScan()
        }

        binding.btnPrint.setOnClickListener { print() }
        binding.btnBack.setOnClickListener { onBackPressed() }

        setAdapter()
        checkAndRequestPermissions()

        val broadcast: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getIntExtra("id",0)
                val builder = AlertDialog.Builder(this@DetailDataDashboard)
                builder.setTitle("Hapus Data")
                builder.setMessage("Apakah Anda yakin ingin menghapus data ini?")
                builder.setPositiveButton("Hapus") { dialog, which ->
                    db.removeData(id)
                    dialog.dismiss()
                    setAdapter()
                }
                builder.setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss() }
                builder.show()

            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast, IntentFilter("deleteRow"))
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val raw = PaketModel()
                raw.codepaket = result.contents
                raw.date = simple.format(calendarToday.time)
                raw.idpaket = idpaket
                if (db.check(result.contents) > 0) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Data sudah pernah discan")
                    builder.setMessage("Apakah Anda ingin menyimpan data ini lagi?")
                    builder.setPositiveButton("Simpan") { dialog, which ->
                        db.insertData(raw)
                        setAdapter()
                    }
                    builder.setNegativeButton("Tidak") { dialog, which ->
                        dialog.dismiss() }
                    builder.show()
                } else { db.insertData(raw)
                    setAdapter()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val wtite = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(
                            "in fragment on request",
                            "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted"
                        )
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            showDialogOK(
                                "Camera and Storage Permission required for this app"
                            ) { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> {}
                                }
                            }
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
    private fun setAdapter(){
        val data = db.readData(idpaket)
        if (data.isEmpty()) {
            binding.btnPrint.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE }
        else {
            binding.btnPrint.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE }

        binding.listPaket.layoutManager = LinearLayoutManager(this)
        val adapter = DetailDataListAdapter(this, data)
        binding.listPaket.adapter = adapter
        binding.titleBar.text = "Jumlah Paket (${data.size})"
    }

    private fun print(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cetak Data CSV")
        builder.setMessage("Apakah Anda yakin ingin mencetak data ini?")
        builder.setPositiveButton("Cetak") { dialog, which ->
            db.exportDB(idpaket, namepaket)
        }
        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss() }
        builder.show()
    }

}