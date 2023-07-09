package com.example.monitoringcabai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.example.monitoringcabai.databinding.ActivityMenuBinding
import com.example.monitoringcabai.databinding.ActivityGrafikkBinding
import kotlinx.parcelize.Parcelize

@Parcelize
class Menu : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.location -> replaceFragment(Location())
                R.id.history -> replaceFragment(History())
                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flmenu, fragment)
        fragmentTransaction.commit()
    }
}
