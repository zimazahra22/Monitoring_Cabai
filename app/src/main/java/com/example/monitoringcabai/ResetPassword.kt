package com.example.monitoringcabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.monitoringcabai.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
class ResetPassword : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.rstbtn.setOnClickListener {
            val email = binding.inemail.text.toString()

            if (email.isEmpty()) {
                binding.inemail.error = "Email Required"
                binding.inemail.requestFocus()
                return@setOnClickListener
            }

            // Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.inemail.error = "Invalid Email"
                binding.inemail.requestFocus()
                return@setOnClickListener
            }

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Check Email for Reset Password", Toast.LENGTH_SHORT).show()
                    Intent(this@ResetPassword, Login::class.java).also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnBackListener()
    }

    private fun btnBackListener() {
        binding.rpnh.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

}