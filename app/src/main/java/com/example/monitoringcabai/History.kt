package com.example.monitoringcabai

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.monitoringcabai.databinding.FragmentHistoryBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.*
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

class History : Fragment() {
    // ...

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var lineChart: LineChart
    private val options = arrayOf("N", "P", "K", "pH", "Moisture")

    private val entries = mutableMapOf<String, MutableList<Entry>>()
    private val timeLabels = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("w5JQlOfIKqPBIfxqmmcyK1QD6zn2")

        lineChart = binding.lineChart

        lineChart.apply {
            // Konfigurasi chart
            setupLineChart()
        }

        val dropdown = binding.dropdown

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
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
                val selectedDate = binding.etDate.text.toString()
                entries.clear()
                timeLabels.clear()

                snapshot.children.forEach { dataSnapshot ->
                    val dataDate = dataSnapshot.child("Date").value as? String
                    val time = dataSnapshot.child("Time").value as? String

                    if (dataDate?.trim() == selectedDate.trim() && time != null) {
                        options.forEach { option ->
                            val optionValue = dataSnapshot.child(option).value as? String
                            val floatValue = optionValue?.toFloatOrNull()

                            if (floatValue != null) {
                                entries.getOrPut(option) { mutableListOf() }
                                    .add(Entry(entries[option]?.size?.toFloat() ?: 0f, floatValue))

                                if (timeLabels.isEmpty()) {
                                    timeLabels.add(time)
                                }
                            }
                        }
                    }
                }

                if (entries.isNotEmpty()) {
                    val selectedOption = options[dropdown.selectedItemPosition]
                    val optionEntries = entries[selectedOption] ?: mutableListOf()

                    val lineDataSet = LineDataSet(optionEntries, "Nilai $selectedOption")
                    lineDataSet.color = Color.GREEN
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.cubicIntensity = 0.2f

                    lineDataSet.setDrawFilled(true)
                    lineDataSet.fillColor = Color.GREEN
                    lineDataSet.fillAlpha = 80
                    lineDataSet.valueTextSize = 10f
                    lineDataSet.valueTextColor = Color.BLACK
                    lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD

                    val lineData = LineData(lineDataSet)
                    lineChart.data = lineData

                    lineChart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(timeLabels)
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                    }

                    lineChart.invalidate()
                } else {
                    lineChart.clear()
                    lineChart.setNoDataText("Data tidak tersedia")
                    lineChart.invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        binding.etDate.setText(selectedDate)
        databaseReference.addListenerForSingleValueEvent(valueEventListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupLineChart() {
        lineChart.apply {
            // Konfigurasi chart
            setTouchEnabled(true)
            setPinchZoom(true)
            description.isEnabled = false
            legend.isEnabled = true
            axisRight.isEnabled = false
            setNoDataText("Data Unavailable")
            animateX(1000)
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.time
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                binding.etDate.setText(formattedDate)

                databaseReference.addListenerForSingleValueEvent(valueEventListener)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun displayChart(selectedOption: String) {
        val entries = entries[selectedOption] ?: mutableListOf()
        if (entries.isNotEmpty()) {
            val lineDataSet = LineDataSet(entries, "Nilai $selectedOption")
            lineDataSet.color = Color.GREEN
            lineDataSet.setDrawCircles(false)
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.cubicIntensity = 0.2f

            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = Color.GREEN
            lineDataSet.fillAlpha = 80
            lineDataSet.valueTextSize = 10f
            lineDataSet.valueTextColor = Color.BLACK
            lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD

            val lineData = LineData(lineDataSet)
            lineChart.data = lineData

            lineChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(timeLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }

            lineChart.invalidate()
        } else {
            lineChart.clear()
            lineChart.setNoDataText("Data tidak tersedia")
            lineChart.invalidate()
        }
    }

    @Parcelize
    data class ProductModel(val name: String, val price: Int) : Parcelable

    private fun String?.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }
}