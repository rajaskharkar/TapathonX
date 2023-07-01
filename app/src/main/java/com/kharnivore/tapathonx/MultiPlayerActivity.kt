package com.kharnivore.tapathonx

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MultiPlayerActivity : AppCompatActivity() {

    private lateinit var tapZoneTop: TextView
    private lateinit var tapZoneBottom: TextView
    private lateinit var startButton: TextView
    private lateinit var timerValueSpinner: Spinner
    private var tapCountTop = 0
    private var tapCountBottom = 0
    private var timerValue = 5
    private var currentTimerDisplayValue = 0
    private var isGameActive = false
    private var isPreGameCountDownActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_player)
        tapZoneTop = findViewById(R.id.tap_zone_top)
        tapZoneBottom = findViewById(R.id.tap_zone_bottom)
        startButton = findViewById(R.id.start_button)
        timerValueSpinner = findViewById(R.id.timer_spinner)
        val timerValues = resources.getStringArray(R.array.timer_values)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.timer_values,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timerValueSpinner.adapter = adapter
        timerValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                timerValue = timerValues[position].toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        startButton.setOnClickListener {
            if (!isGameActive && !isPreGameCountDownActive) {
                startPreGameCountdown()
            }
        }
        tapZoneTop.setOnClickListener {
            if (isGameActive) {
                tapCountTop++
                updateTapZoneTop()
            }
        }
        tapZoneBottom.setOnClickListener {
            if (isGameActive) {
                tapCountBottom++
                updateTapZoneBottom()
            }
        }
    }

    private fun startPreGameCountdown() {
        isPreGameCountDownActive = true
        timerValueSpinner.isEnabled = false
        startButton.text = "Get Ready..."
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tapZoneBottom.text = "Get Ready...\n\n${millisUntilFinished/1000}"
                tapZoneTop.text = "Get Ready...\n\n${millisUntilFinished/1000}"
            }

            override fun onFinish() {
                isPreGameCountDownActive = false
                startGame()
            }
        }.start()
    }

    private fun startGame() {
        isGameActive = true
        startButton.text = "Go Go Go!"
        tapCountTop = 0
        tapCountBottom = 0
        updateTapZoneTop()
        updateTapZoneBottom()
        object : CountDownTimer(timerValue * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update tap zones with remaining time and current tap count
                currentTimerDisplayValue = (millisUntilFinished / 1000).toInt()
                updateTapZoneTop()
                updateTapZoneBottom()
            }
            override fun onFinish() {
                isGameActive = false
                timerValueSpinner.isEnabled = true
                startButton.text = "Play Again!"
                when {
                    tapCountTop > tapCountBottom -> {
                        tapZoneTop.text = "You Won!\nTap Count: $tapCountTop"
                        tapZoneBottom.text = "You Lost!\nTap Count: $tapCountBottom"
                    }
                    tapCountTop < tapCountBottom -> {
                        tapZoneTop.text = "You Lost!\nTap Count: $tapCountTop"
                        tapZoneBottom.text = "You Won!\nTap Count: $tapCountBottom"
                    }
                    else -> {
                        tapZoneTop.text = "It's a Draw!\nTap Count: $tapCountTop"
                        tapZoneBottom.text = "It's a Draw!\nTap Count: $tapCountBottom"
                    }
                }
            }
        }.start()
    }

    private fun updateTapZoneTop() {
        if (isGameActive) {
            tapZoneTop.text = "Tap now!\nTime left: ${currentTimerDisplayValue}\nTap Count: $tapCountTop"
        }
    }

    private fun updateTapZoneBottom() {
        if (isGameActive) {
            tapZoneBottom.text = "Tap now!\nTime left: ${currentTimerDisplayValue}\nTap Count: $tapCountBottom"
        }
    }
}
