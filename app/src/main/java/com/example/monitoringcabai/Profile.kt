package com.example.monitoringcabai

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringcabai.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize

@Parcelize
class Profile : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            binding.kUser.setText(user.email)
        }

        binding.cPass.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            tombolkeluar()
        }
        binding.ppnh.setOnClickListener {
                startActivity(Intent(this, Menu::class.java))
            }
    }

    private fun tombolkeluar() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}
