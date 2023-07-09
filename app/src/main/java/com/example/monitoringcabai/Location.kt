package com.example.monitoringcabai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.monitoringcabai.databinding.FragmentLocationBinding
import com.google.firebase.database.*
import kotlinx.parcelize.Parcelize

@Parcelize
class Location : Fragment(), Parcelable {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference
            .child("w5JQlOfIKqPBIfxqmmcyK1QD6zn2")

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Tidak perlu melakukan apa pun di sini karena kita akan mengambil link terbaru saat tombol diklik
                val Link =dataSnapshot.child("/Link").value
                binding.toolloc1.setOnClickListener{
                    if (Link != null && Link is String && Link.isNotEmpty()){
                        val openUrl = Intent(Intent.ACTION_VIEW)
                        openUrl.data = Uri.parse(Link)
                        startActivity(openUrl)
                    }else{
                        Toast.makeText(requireContext(), "Unreadable Location", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Mengambil link terbaru saat ada perubahan pada child data
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Data telah dihapus
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Data telah dipindahkan
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Database Error: ${databaseError.message}")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


