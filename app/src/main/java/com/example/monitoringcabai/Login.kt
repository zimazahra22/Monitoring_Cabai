package com.example.monitoringcabai

import com.example.monitoringcabai.databinding.ActivityMonitoringBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.monitoringcabai.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.text.InputType

@Parcelize
class Login : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false


    private fun loginFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Menu::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun btnBackListener() {
        binding.lPnh.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.lLogin.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.fPw.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
        }

        binding.lBtn.setOnClickListener {
            val email = binding.enteremail.text.toString()
            val password = binding.enterpassword.text.toString()

            // Validasi email
            if (email.isEmpty()) {
                binding.enteremail.error = "Email Required"
                binding.enteremail.requestFocus()
                return@setOnClickListener
            }

            // Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.enterpassword.error = "Invalid Email"
                binding.enterpassword.requestFocus()
                return@setOnClickListener
            }

            // Validasi password
            if (password.isEmpty()) {
                binding.enterpassword.error = "Password Required"
                binding.enterpassword.requestFocus()
                return@setOnClickListener
            }

            loginFirebase(email, password)
        }

        binding.togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Mengubah ikon berdasarkan status tampilan password
            if (isPasswordVisible) {
                binding.togglePassword.setImageResource(R.drawable.ic_hide_password)
                binding.enterpassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.togglePassword.setImageResource(R.drawable.ic_show_password)
                binding.enterpassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            // Mengatur kursor ke posisi akhir teks
            binding.enterpassword.setSelection(binding.enterpassword.text.length)
        }

        // Mengatur tampilan awal password menjadi tersembunyi
        binding.enterpassword.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        btnBackListener()

    }
}






