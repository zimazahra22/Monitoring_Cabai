package com.example.monitoringcabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.monitoringcabai.R
import com.example.monitoringcabai.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import android.os.Parcelable
import android.text.InputType
import kotlinx.parcelize.Parcelize

@Parcelize
class Register : AppCompatActivity(), Parcelable {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible: Boolean = false

    private fun RegistFirebase(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun btnBackListener() {
        binding.rPnh.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun togglePasswordVisibility() {

        if (isPasswordVisible) {
            binding.togglePasswordcrp.setImageResource(R.drawable.ic_show_password)
            binding.togglePasswordcop.setImageResource(R.drawable.ic_show_password)

            binding.rkPw.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.rkPww.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

        } else {
            binding.togglePasswordcrp.setImageResource(R.drawable.ic_hide_password)
            binding.togglePasswordcop.setImageResource(R.drawable.ic_hide_password)

            binding.rkPw.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            binding.rkPww.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }
        binding.rkPw.setSelection(binding.rkPw.text.length)
        binding.rkPww.setSelection(binding.rkPww.text.length)
        isPasswordVisible = !isPasswordVisible

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.rSign.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.rBtn.setOnClickListener {
            val name = binding.rkName.text.toString()
            val email = binding.rkEmail.text.toString()
            val password = binding.rkPw.text.toString()
            val password2 = binding.rkPww.text.toString()

            // Validasi nama lengkap
            if (name.isEmpty()) {
                binding.rkName.error = "Name Required"
                binding.rkName.requestFocus()
                return@setOnClickListener
            }

            // Validasi email
            if (email.isEmpty()) {
                binding.rkEmail.error = "Email Required"
                binding.rkEmail.requestFocus()
                return@setOnClickListener
            }

            // Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.rkEmail.error = "Invalid Email"
                binding.rkEmail.requestFocus()
                return@setOnClickListener
            }

            // Validasi password
            if (password.isEmpty()) {
                binding.rkPw.error = "Password Required"
                binding.rkPw.requestFocus()
                return@setOnClickListener
            }

            // Validasi password2
            if (password2.isEmpty()) {
                binding.rkPww.error = "Confirm Password"
                binding.rkPww.requestFocus()
                return@setOnClickListener
            }

            if (password != password2) {
                binding.rkPww.error = "Password Mismatch!!"
                binding.rkPww.requestFocus()
                return@setOnClickListener
            }

            RegistFirebase(email, password)
        }

        binding.togglePasswordcrp.setOnClickListener {
            togglePasswordVisibility()
        }
        binding.togglePasswordcop.setOnClickListener {
            togglePasswordVisibility()
        }
        binding.rkPw.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.rkPww.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD


        btnBackListener()
    }
}
