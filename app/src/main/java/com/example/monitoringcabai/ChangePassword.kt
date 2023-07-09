package com.example.monitoringcabai

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringcabai.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.parcelize.Parcelize

@Parcelize
class ChangePassword : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cdvLpass.visibility = View.VISIBLE
        binding.cdvBpass.visibility = View.GONE

        binding.glPass.setOnClickListener {

            val oldPassword = binding.passLama.text.toString()

            if (oldPassword.isEmpty()) {
                binding.passLama.error = "Confirm Password Cannot Be Empty"
                binding.passLama.requestFocus()
                return@setOnClickListener
            }

            user?.let {

                val userCredential = EmailAuthProvider.getCredential(it.email!!, oldPassword)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            binding.cdvLpass.visibility = View.GONE
                            binding.cdvBpass.visibility = View.VISIBLE
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.passLama.error = "Incorrect Password"
                            binding.passLama.requestFocus()
                        }
                        else -> {
                            Toast.makeText(
                                this,
                                "${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        binding.cpPnh.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
        binding.cppPnh.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        binding.gbPass.setOnClickListener newPassword@{
            val newPassword = binding.passBaru.text.toString()
            val confirmPassword = binding.passBaru2.text.toString()

            if (newPassword.isEmpty()) {
                binding.passBaru.error = "Confirm Password Cannot Be Empty"
                binding.passBaru.requestFocus()
                return@newPassword
            }
            if (confirmPassword.isEmpty()) {
                binding.passBaru2.error = "Confirm Password Cannot Be Empty"
                binding.passBaru2.requestFocus()
                return@newPassword
            }
            if (newPassword != confirmPassword) {
                binding.passBaru2.error = "Password Mismatch"
                binding.passBaru2.requestFocus()
                return@newPassword
            }
            if (newPassword.length < 6) {
                binding.passBaru.error = "Minimum 6 Characters Password"
                binding.passBaru.requestFocus()
                return@newPassword
            }
            if (confirmPassword.length < 6) {
                binding.passBaru2.error = "Minimum 6 Characters Password"
                binding.passBaru2.requestFocus()
                return@newPassword
            }

            user?.let {
                it.updatePassword(newPassword)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Password Successfully Changed. Please Log in Again",
                            Toast.LENGTH_SHORT
                        ).show()
                        logout()
                    }
                }
            }
        }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
