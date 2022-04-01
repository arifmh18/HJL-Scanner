package com.artry.scannerBarcode

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.artry.scannerBarcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("scanPref", AppCompatActivity.MODE_PRIVATE)
        val dataVer = sharedPreferences.getInt("ver", 0)
        val login = sharedPreferences.getBoolean("login", false)

        if (login) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish() }

        if (dataVer == 0) {
            defaultLogin() }

        binding.btnLogin.setOnClickListener {
            validate()
        }
    }

    fun validate(){
        val username = binding.userName.text.toString()
        val password = binding.password.text.toString()
        if (username.isEmpty()) {
            Toast.makeText(this, "Silahkan isi username terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Silahkan isi password terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val usernameDB = sharedPreferences.getString("username", "")
        val passwordDB = sharedPreferences.getString("password", "")

        if (username == usernameDB && password == passwordDB) {
            val editor: SharedPreferences.Editor = this.getSharedPreferences("scanPref",
                AppCompatActivity.MODE_PRIVATE
            ).edit()
            editor.putBoolean("login", true)
            editor.apply()
            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Username atau password anda salah", Toast.LENGTH_SHORT).show()
        }
    }

    fun defaultLogin(){
        val editor: SharedPreferences.Editor = this.getSharedPreferences("scanPref",
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString("username", "admin")
        editor.putString("password", "hitorijayalogistik")
        editor.putInt("ver", 1)
        editor.apply()
    }
}