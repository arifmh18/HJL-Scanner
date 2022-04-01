package com.artry.scannerBarcode

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.artry.scannerBarcode.Adapter.DashboardListAdapter
import com.artry.scannerBarcode.Database.DataBaseDashboard
import com.artry.scannerBarcode.Model.DashboardModel
import com.artry.scannerBarcode.Utilities.Utils
import com.artry.scannerBarcode.databinding.ActivityDashboardBinding
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDashboardBinding
    private lateinit var db : DataBaseDashboard
    val calendarToday = Calendar.getInstance()
    val simple = SimpleDateFormat("yyyy-MM-dd")
    var sharedpreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DataBaseDashboard(this)

        binding.btnTambah.setOnClickListener { showAlert() }
        sharedpreferences = this.getSharedPreferences("scanPref", AppCompatActivity.MODE_PRIVATE)
        val username = sharedpreferences?.getString("username", "")

        binding.txtWelcome.text = "Selamat datang, ${username?.uppercase()}"
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        setAdapter()

        val broadcast: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getIntExtra("id",0)
                val builder = AlertDialog.Builder(this@DashboardActivity)
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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast, IntentFilter("deleteData"))

    }

    private fun showAlert(){
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dashboard)
        val body = dialog.findViewById(R.id.namePaket) as TextInputEditText
        val yesBtn = dialog.findViewById(R.id.btnSimpan) as Button
        val noBtn = dialog.findViewById(R.id.btnBatal) as Button
        yesBtn.setOnClickListener {
            if (body.text.toString().isEmpty()) { Toast.makeText(this, "Harap isi Keterangan Paket", Toast.LENGTH_SHORT).show() }
            else {
                val date = Utils().convertIndoTgl(simple.format(calendarToday.time), true)
                val raw = DashboardModel()
                raw.date = date
                raw.paket = body.text.toString()
                db.insertData(raw)
                dialog.dismiss()
                setAdapter()
            }
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setAdapter(){
        val data = db.readData()
        if (data.isEmpty()) {
            binding.noData.visibility = View.VISIBLE }
        else {
            binding.noData.visibility = View.GONE }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = DashboardListAdapter(this, data)
        binding.recyclerView.adapter = adapter
    }

    private fun logout(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Apakah Anda yakin ingin logout")
        builder.setPositiveButton("Logout") { dialog, which ->
            val editor: SharedPreferences.Editor = this.getSharedPreferences("scanPref",
                AppCompatActivity.MODE_PRIVATE
            ).edit()
            editor.putBoolean("login", false)
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss() }
        builder.show()
    }
}