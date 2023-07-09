package com.example.monitoringcabai

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringcabai.databinding.ActivityMainBinding
import kotlinx.parcelize.Parcelize

@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        btnLoginListener()
        btnRegisterListener()
    }

    private fun btnLoginListener() {
        binding.btn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    private fun btnRegisterListener() {
        binding.btn2.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}