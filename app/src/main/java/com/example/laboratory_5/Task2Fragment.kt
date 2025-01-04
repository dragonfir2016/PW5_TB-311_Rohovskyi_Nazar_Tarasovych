package com.example.laboratory_5

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.DecimalFormat

class Task2Fragment : Fragment(R.layout.fragment_task2) {

    private val elements = mapOf(
        "Т-110 кВ" to Triple(0.015, 100.0, 43.0),
        "Т-35 кВ" to Triple(0.01, 45.0, 4.0),
        "Т-10 кВ (кабельна мережа 10 кВ)" to Triple(0.005, 60.0, 10.0),
        "Т-10 кВ (повітряна мережа 10 кВ)" to Triple(0.05, 60.0, 10.0),
        "В-110 кВ (елегазовий)" to Triple(0.01, 30.0, 30.0),
        "В-10 кВ (малооливний)" to Triple(0.02, 15.0, 15.0),
        "В-10 кВ (вакуумний)" to Triple(0.01, 15.0, 15.0),
        "Збірні шини 10 кВ на 1 приєднання" to Triple(0.03, 5.0, 5.0),
        "АВ-0.38 кВ" to Triple(0.05, 4.0, 10.0),
        "ЕД 6,10 кВ" to Triple(0.1, 10.0, 0.0),
        "ЕД 0,38 кВ" to Triple(0.1, 5.0, 0.0)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = view.findViewById<Spinner>(R.id.elementSpinner)
        val inputAccidentCost = view.findViewById<EditText>(R.id.inputAccidentCost)
        val inputPlannedCost = view.findViewById<EditText>(R.id.inputPlannedCost)
        val calculateButton = view.findViewById<Button>(R.id.calculateButton)
        val resultsTextView = view.findViewById<TextView>(R.id.resultsTextView)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            elements.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val df = DecimalFormat("#.######")

        calculateButton.setOnClickListener {
            try {
                val selectedElement = spinner.selectedItem.toString()
                val (omega, tb, tp) = elements[selectedElement]
                    ?: throw Exception("Елемент не знайдено")

                val accidentCost = inputAccidentCost.text.toString().toDouble()
                val plannedCost = inputPlannedCost.text.toString().toDouble()

                val Pm = 5120.0
                val Tm = 6451.0

                val MwAccident = omega * tb * 0.001 * Pm * Tm

                val MwPlanned = tp * 0.001 * Pm * Tm

                val totalLosses = (accidentCost * MwAccident) + (plannedCost * MwPlanned)

                resultsTextView.text = """
                    Вибраний елемент: $selectedElement
                    
                    Математичне сподівання аварійного недовідпущення: ${df.format(MwAccident)} кВт·год
                    Математичне сподівання планового недовідпущення: ${df.format(MwPlanned)} кВт·год
                    
                    Загальні збитки: ${df.format(totalLosses)} грн
                """.trimIndent()
            } catch (e: Exception) {
                resultsTextView.text = "Помилка: ${e.message}"
            }
        }
    }
}
