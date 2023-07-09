package com.example.monitoringcabai

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Calendar
import android.os.Parcelable
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
import kotlinx.parcelize.Parcelize

@Parcelize
class grafikk : AppCompatActivity(), Parcelable {

    private lateinit var binding: ActivityGrafikkBinding
    private lateinit var lineChart: LineChart
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrafikkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lineChart = binding.lineChart

        lineChart.apply {
            // Konfigurasi chart
        }

        databaseReference = FirebaseDatabase.getInstance().reference
            .child("w5JQlOfIKqPBIfxqmmcyK1QD6zn2")

        val dropdown = binding.dropdown
        val options = arrayOf("N", "P", "K", "pH", "Moisture", "Humidity")

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
                val currentDate = getCurrentDate() // Mendapatkan tanggal saat ini (YYYY-MM-DD)

                snapshot.children.forEach { dataSnapshot ->
                    val dataDate = dataSnapshot.child("Date").value as? String
                    if (dataDate == currentDate) { // Hanya memproses data dengan tanggal yang sama
                        val value = dataSnapshot.child(selectedOption).value as? String
                        value?.let {
                            val floatValue = it.toFloatOrNull()
                            floatValue?.let {
                                entries.add(Entry(entries.size.toFloat(), floatValue))
                                val time = dataSnapshot.child("Time").value as? String
                                time?.let {
                                    timeLabels.add(it)
                                }
                            }
                        }
                    }
                }

                if (entries.isNotEmpty()) {
                    val lineDataSet = LineDataSet(entries, "Nilai $selectedOption")
                    lineDataSet.color = Color.GREEN
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.cubicIntensity = 0.2f

                    lineDataSet.setDrawFilled(true)
                    lineDataSet.fillColor = Color.GREEN
                    lineDataSet.fillAlpha = 70

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

    override fun onDestroy() {
        super.onDestroy()
        databaseReference.removeEventListener(valueEventListener)
    }

    fun displayChart(selectedOption: String) {        // Implementasi logika untuk menampilkan grafik berdasarkan pilihan
        databaseReference.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}
