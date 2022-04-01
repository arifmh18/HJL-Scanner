package com.artry.scannerBarcode

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.artry.scannerBarcode.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("scanPref", AppCompatActivity.MODE_PRIVATE)

        binding.btnReset.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Reset Profile")
            builder.setMessage("Apakah Anda yakin ingin reset profil ini?")
            builder.setPositiveButton("Reset") { dialog, which ->
                reset()
                dialog.dismiss()
            }
            builder.setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss() }
            builder.show()
        }

        binding.btnLogin.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Perbarui Profile")
            builder.setMessage("Apakah Anda yakin ingin memperbarui profil ini?")
            builder.setPositiveButton("Perbarui") { dialog, which ->
                update()
                dialog.dismiss()
            }
            builder.setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss() }
            builder.show()
        }

        binding.btnBack.setOnClickListener { onBackPressed() }

        setup()
    }

    fun setup(){
        val username = sharedPreferences.getString("username", "").toString()
        binding.userName.setText(username)
    }

    fun update(){
        val username = binding.userName.text.toString()
        val newpassword = binding.password.text.toString()
        val oldpassword = binding.passwordLama.text.toString()

        val pass = sharedPreferences.getString("password", "")

        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (newpassword.isEmpty()) {
            Toast.makeText(this, "Password Baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (oldpassword.isEmpty()) {
            Toast.makeText(this, "Password Lama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (oldpassword != pass) {
            Toast.makeText(this, "Password Lama tidak sesuai!", Toast.LENGTH_SHORT).show()
            return
        }

        val editor: SharedPreferences.Editor = this.getSharedPreferences("scanPref",
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString("username", username)
        editor.putString("password", newpassword)
        editor.putInt("ver", 1)
        editor.apply()

        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        clear()
        setup()
    }

    fun clear(){
        binding.userName.setText(sharedPreferences.getString("username", "").toString())
        binding.password.setText("")
        binding.passwordLama.setText("")
    }

    fun reset(){
        val editor: SharedPreferences.Editor = this.getSharedPreferences("scanPref",
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString("username", "admin")
        editor.putString("password", "hitorijayalogistik")
        editor.putInt("ver", 1)
        editor.apply()
        clear()
    }

}