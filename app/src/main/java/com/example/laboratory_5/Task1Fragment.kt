package com.example.laboratory_5

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.DecimalFormat

class Task1Fragment : Fragment(R.layout.fragment_task1) {

    private val elements = mapOf(
        "ПЛ-110 кВ" to Pair(0.07, 10.0),
        "ПЛ-35 кВ" to Pair(0.07, 10.0),
        "Трансформатор 110/10 кВ" to Pair(0.015, 100.0),
        "Ввідний вимикач 10 кВ" to Pair(0.02, 15.0),
        "Приєднання 10 кВ" to Pair(0.03, 2.0),
        "Т-110 кВ" to Pair(0.015, 100.0),
        "Т-35 кВ" to Pair(0.02, 28.0),
        "Т-10 кВ (кабельна мережа)" to Pair(0.005, 60.0),
        "Т-10 кВ (повітряна мережа)" to Pair(0.005, 30.0),
        "В-110 кВ (елегазовий)" to Pair(0.01, 15.0),
        "В-10 кВ (вакуумний)" to Pair(0.02, 12.0)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = view.findViewById<Spinner>(R.id.elementSpinner)
        val addElementButton = view.findViewById<Button>(R.id.addElementButton)
        val calculateButton = view.findViewById<Button>(R.id.calculateReliabilityButton)
        val selectedElementsList = view.findViewById<TextView>(R.id.selectedElementsList)
        val resultsTextView = view.findViewById<TextView>(R.id.resultsTextView)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            elements.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val selectedElements = mutableListOf<String>()
        val df = DecimalFormat("#.######")

        addElementButton.setOnClickListener {
            val selectedElement = spinner.selectedItem.toString()
            selectedElements.add(selectedElement)
            selectedElementsList.text = selectedElements.joinToString(", ")
        }

        calculateButton.setOnClickListener {
            try {
                val calculations = selectedElements.map { element ->
                    elements[element] ?: throw Exception("Елемент не знайдено")
                }

                val omegaOC = calculations.sumOf { it.first }
                val tbOC = calculations.sumOf { it.first * it.second } / omegaOC
                val kaOC = omegaOC * tbOC / 8760.0
                val kpMax = 43.0
                val kpOC = 1.2 * kpMax / 8760.0
                val omegaDK = 2 * omegaOC * (kaOC + kpOC)
                val omegaSC = omegaDK + 0.02

                resultsTextView.text = """
                    Вибрані елементи: ${selectedElements.joinToString(", ")}
                    
                    Частота відмов одноколової системи: ${df.format(omegaOC)} рік⁻¹
                    Середня тривалість відновлення: ${df.format(tbOC)} год
                    Коефіцієнт аварійного простою: ${df.format(kaOC)}
                    Коефіцієнт планового простою: ${df.format(kpOC)}
                    
                    Частота відмов для двоколового варіанту: ${df.format(omegaDK)} рік⁻¹
                    Частота відмов двоколової системи (з секційним вимикачем): ${df.format(omegaSC)} рік⁻¹
                """.trimIndent()
            } catch (e: Exception) {
                resultsTextView.text = "Помилка обчислення: ${e.message}"
            }
        }
    }
}
