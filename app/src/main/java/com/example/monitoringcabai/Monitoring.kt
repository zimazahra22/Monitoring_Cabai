package com.example.monitoringcabai

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import com.example.monitoringcabai.databinding.ActivityGrafikkBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.*
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.graphics.Typeface
import com.db.williamchart.ExperimentalFeature
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringcabai.databinding.ActivityMonitoringBinding
import com.google.firebase.database.*
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Monitoring : AppCompatActivity(), Parcelable {

    private lateinit var binding: ActivityMonitoringBinding
    private lateinit var database: DatabaseReference
    private lateinit var timer: Timer
    private lateinit var lineChart: LineChart
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseListener()
        btnMonitoringListener()
        fetchStatus()

        database = FirebaseDatabase.getInstance().reference

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetchData()
            }
        }, 0, 3600000) // Update every 1 hour (3600000 milliseconds)

        lineChart = binding.lineChart

        lineChart.apply {

            setDrawGridBackground(false)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = false


            // Set layout params for the chart
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (resources.displayMetrics.heightPixels ).toInt()
            )
            layoutParams.addRule(RelativeLayout.BELOW, R.id.dropdown)
            layoutParams.setMargins(16, 0, 16, 0)
            lineChart.layoutParams = layoutParams
        }



        databaseReference = FirebaseDatabase.getInstance().reference
            .child("w5JQlOfIKqPBIfxqmmcyK1QD6zn2")

        val dropdown = binding.dropdown
        val options = arrayOf("N", "P", "K", "pH", "Moisture")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = options[position]
                displayChart(selectedOption)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val selectedOption = options[dropdown.selectedItemPosition]
                val entries = mutableListOf<Entry>()
                val timeLabels = mutableListOf<String>()
                val sortedSnapshot = snapshot.children.sortedByDescending { it.key }.take(12).reversed()

                for ((index, dataSnapshot) in sortedSnapshot.withIndex()) {
                    val value = dataSnapshot.child(selectedOption).value as? String
                    value?.let {
                        val floatValue = it.toFloatOrNull()
                        floatValue?.let {
                            entries.add(Entry(index.toFloat(), floatValue))
                        }
                    }

                    val time = dataSnapshot.child("Time").value as? String
                    time?.let {
                        timeLabels.add(it)
                    }
                }

                if (entries.isNotEmpty()) {
                    val lineDataSet = LineDataSet(entries, "Nilai $selectedOption")
                    lineDataSet.color = Color.GREEN
                    lineDataSet.setDrawCircles(true)
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.cubicIntensity = 0.2f
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.fillColor = Color.GREEN
                    lineDataSet.fillAlpha = 70
                    lineDataSet.valueTextSize = 15f // Set ukuran teks nilai data dalam grafik
                    lineDataSet.valueTextColor = Color.BLACK // Set warna teks nilai data dalam grafik
                    lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD // Set teks nilai data dalam grafik menjadi tebal

                    val lineData = LineData(lineDataSet)

                    // Set X-axis labels
                    val xAxis = lineChart.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(timeLabels)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.setDrawGridLines(false)
                    xAxis.labelRotationAngle = -90f

                    lineChart.data = lineData
                    lineChart.invalidate()
                    val yAxis = lineChart.axisLeft
                    yAxis.textSize = 12f // Set ukuran teks nilai sumbu Y
                    yAxis.textColor = Color.BLACK // Set warna teks nilai sumbu Y

                    lineChart.data = lineData
                    lineChart.invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        }


        databaseReference.addValueEventListener(valueEventListener)

        // Disable right axis
        lineChart.axisRight.isEnabled = false

    }

    private fun databaseListener() {
        database = FirebaseDatabase.getInstance().getReference()
        val uid = "prediksi"

        val nListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val n = snapshot.child("n").value
                binding.ansprenutrient.text = n.toString() + " mg"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Monitoring, "Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        val pListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val p = snapshot.child("p").value
                binding.ansprephospor.text = p.toString() + " mg"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Monitoring, "Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        val kListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val k = snapshot.child("k").value
                binding.ansprekalium.text = k.toString() + " mg"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Monitoring, "Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }


        val anstglpreListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val waktu = snapshot.child("waktu").value
                binding.anstglpre.text =  " On " + waktu.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Monitoring, "Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        val databaseReference = database.child(uid)
        databaseReference.addValueEventListener(nListener)
        databaseReference.addValueEventListener(pListener)
        databaseReference.addValueEventListener(kListener)
        databaseReference.addValueEventListener(anstglpreListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        databaseReference.removeEventListener(valueEventListener)
    }
    private fun updateStatusUI(status: String) {
        val onColor = Color.GREEN
        val offColor = Color.RED

        runOnUiThread {
            if (status == "on") {
                binding.outerBox.setBackgroundColor(onColor)
            } else {
                binding.outerBox.setBackgroundColor(offColor)
            }
            binding.onoff.text = status
        }
    }


    private fun fetchStatus() {
        val uid = "status_alat_mobile"
        val databaseReference = FirebaseDatabase.getInstance().getReference(uid).child("status")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.value as? String
                status?.let {
                    updateStatusUI(status) // Panggil fungsi updateStatusUI di sini dengan parameter status
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    private fun btnMonitoringListener() {
        binding.mPnh.setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        fetchData()
    }

    private fun fetchData() {
        val databaseReference = FirebaseDatabase.getInstance().reference
            .child("w5JQlOfIKqPBIfxqmmcyK1QD6zn2")



        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                val latestData = snapshot.children.lastOrNull()
                if (latestData != null) {
                    val n = latestData.child("N").value as? String
                    val p = latestData.child("P").value as? String
                    val k = latestData.child("K").value as? String
                    val ph = latestData.child("pH").value as? String
                    val moisture = latestData.child("Moisture").value as? String

                    runOnUiThread {
                        binding.ansnutrient.text = n + " ppm"
                        binding.ansphospor.text = p + " ppm"
                        binding.anskalium.text = k + " ppm"
                        binding.ansph.text = ph
                        binding.ansmoisture.text = moisture + " %"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Monitoring, "Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        databaseReference.addValueEventListener(valueEventListener)

    }

    override fun onStop() {
        super.onStop()
        database.removeEventListener(valueEventListener)
    }


    private fun displayChart(selectedOption: String) {
        databaseReference.addListenerForSingleValueEvent(valueEventListener)

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<Entry>()
                val sortedSnapshot = snapshot.children.sortedByDescending { it.key }.take(12).reversed()
                val timeLabels = mutableListOf<String>()

                for ((index, dataSnapshot) in sortedSnapshot.withIndex()) {
                    val value = dataSnapshot.child(selectedOption).value as? String
                    value?.let {
                        val floatValue = it.toFloatOrNull()
                        floatValue?.let {
                            entries.add(Entry(index.toFloat(), floatValue))
                        }
                    }

                    val time = dataSnapshot.child("Time").value as? String
                    time?.let {
                        timeLabels.add(it)
                    }
                }

                if (entries.isNotEmpty()) {
                    val lineDataSet = LineDataSet(entries, "Nilai $selectedOption")
                    lineDataSet.color = Color.GREEN
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.cubicIntensity = 0.2f
                    lineDataSet.valueTextSize = 8f // Set ukuran teks nilai data dalam grafik
                    lineDataSet.valueTextColor = Color.BLACK // Set warna teks nilai data dalam grafik
                    lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD // Set teks nilai data dalam grafik menjadi tebal



                    lineDataSet.setDrawFilled(true)
                    lineDataSet.fillColor = Color.GREEN
                    lineDataSet.fillAlpha = 70

                    val lineData = LineData(lineDataSet)
                    lineChart.data = lineData
                    lineChart.invalidate()


                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        }

        databaseReference.addValueEventListener(valueEventListener)
    }

}