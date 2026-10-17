package com.example.tip_calc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // Объявление элементов интерфейса
    private lateinit var etOrderSum: EditText
    private lateinit var etDishCount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvSliderLabel: TextView
    private lateinit var rgDiscount: RadioGroup
    private lateinit var rb3: RadioButton
    private lateinit var rb5: RadioButton
    private lateinit var rb7: RadioButton
    private lateinit var rb10: RadioButton
    private lateinit var etDiscountAmount: EditText
    private lateinit var btnTotal: Button
    private lateinit var etFinalTotal: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация View
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etOrderSum = findViewById(R.id.etOrderSum)
        etDishCount = findViewById(R.id.etDishCount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvSliderLabel = findViewById(R.id.tvSliderLabel)
        rgDiscount = findViewById(R.id.rgDiscount)
        rb3 = findViewById(R.id.rb3)
        rb5 = findViewById(R.id.rb5)
        rb7 = findViewById(R.id.rb7)
        rb10 = findViewById(R.id.rb10)
        etDiscountAmount = findViewById(R.id.etDiscountAmount)
        btnTotal = findViewById(R.id.btnTotal)
        etFinalTotal = findViewById(R.id.etFinalTotal)
    }

    private fun setupListeners() {
        // 1. Обработка изменения количества блюд (п.5)
        etDishCount.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updateDiscountRadioButton()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 2. Обработка слайдера (п.4)
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Шаг изменения равен 5 (хотя стандартный SeekBar идет по 1, в задании сказано "шаг 5")
                // Для точного соответствия заданию лучше ограничить значения.
                // Но если оставить стандартный SeekBar, он будет плавным.
                // Чтобы был шаг 5, нужно округлять: val steppedValue = (progress / 5) * 5

                val steppedValue = (progress / 5) * 5
                tvSliderLabel.text = "Чаевые: $steppedValue%"

                // Показ Snackbar с суммой чаевых (п.4)
                val sum = etOrderSum.text.toString().toDoubleOrNull() ?: 0.0
                val tipAmount = calculateTip(sum, steppedValue)

                Snackbar.make(findViewById(android.R.id.content),
                    "Сумма чаевых: $tipAmount руб.",
                    Snackbar.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Фиксируем значение слайдера, чтобы был шаг 5
                val progress = seekBar?.progress ?: 0
                val steppedValue = (progress / 5) * 5
                seekBar?.progress = steppedValue
            }
        })

        // 3. Кнопка "Итого" (п.7)
        btnTotal.setOnClickListener {
            calculateAndDisplayTotal()
        }
    }

    // Функция обновления радиокнопок (п.5)
    private fun updateDiscountRadioButton() {
        val count = etDishCount.text.toString().toIntOrNull() ?: 0

        // Логика выбора скидки
        when {
            count >= 10 -> rb10.isChecked = true
            count in 6..9 -> rb7.isChecked = true // 6-10 (и более) в задании, но 10 уже выше. Берем 6-9.
            count in 3..5 -> rb5.isChecked = true
            count in 1..2 -> rb3.isChecked = true
            else -> rgDiscount.clearCheck() // Если 0 или пусто
        }
    }

    // Функция расчета чаевых (отдельная функция по заданию)
    private fun calculateTip(sum: Double, percent: Int): Double {
        return (sum * percent / 100.0 * 100.0).roundToInt() / 100.0
    }

    // Функция расчета скидки (отдельная функция)
    private fun calculateDiscount(sum: Double): Double {
        val selectedId = rgDiscount.checkedRadioButtonId
        val percent = when (selectedId) {
            R.id.rb3 -> 3
            R.id.rb5 -> 5
            R.id.rb7 -> 7
            R.id.rb10 -> 10
            else -> 0
        }
        return (sum * percent / 100.0 * 100.0).roundToInt() / 100.0
    }

    // Основная логика кнопки "Итого"
    private fun calculateAndDisplayTotal() {
        // Получаем данные
        val sumStr = etOrderSum.text.toString()
        val sum = if (sumStr.isEmpty()) 0.0 else sumStr.toDouble()

        // 1. Считаем скидку и выводим в поле (п.6)
        val discountAmount = calculateDiscount(sum)
        etDiscountAmount.setText(discountAmount.toString())

        // 2. Считаем чаевые
        val tipPercent = (seekBarTip.progress / 5) * 5
        val tipAmount = calculateTip(sum, tipPercent)

        // 3. Итоговая сумма
        // Итого = Сумма - Скидка + Чаевые
        val total = sum - discountAmount + tipAmount

        // Округляем до 2 знаков
        val finalTotal = (total * 100).roundToInt() / 100.0

        // Вывод
        etFinalTotal.setText(finalTotal.toString())
    }
}