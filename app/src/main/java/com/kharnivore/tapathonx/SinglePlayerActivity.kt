package com.kharnivore.tapathonx

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SinglePlayerActivity : AppCompatActivity() {

    private lateinit var highScoreTextView: TextView
    private lateinit var timerValueSpinner: Spinner
    private lateinit var startButton: TextView
    private lateinit var tapZone: TextView
    private var currentHighScore = 0
    private var currentTimerValue = 0
    private var currentTimerDisplayValue = 0
    private var tapCount = 0
    private var isGameActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player)
        highScoreTextView = findViewById(R.id.high_score)
        timerValueSpinner = findViewById(R.id.timer_value)
        startButton = findViewById(R.id.start_button)
        tapZone = findViewById(R.id.tap_zone)
        val timerValues = resources.getStringArray(R.array.timer_values)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.timer_values,
            R.layout.spinner_item // The custom layout for the spinner items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timerValueSpinner.adapter = adapter
        timerValueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentTimerValue = timerValues[position].toInt()
                currentHighScore = getHighScore(currentTimerValue)
                //TODO string cleanup
                highScoreTextView.text = "High Score: $currentHighScore"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        startButton.setOnClickListener {
            if(!isGameActive){
                startGame()
            }
        }
        tapZone.setOnClickListener {
            if (isGameActive) {
                tapCount++
                updateTapZone()
            }
        }
    }

    private fun startGame() {
        isGameActive = true
        timerValueSpinner.isEnabled = false
        startButton.text = "Go Go Go!"
        tapCount = 0
        updateTapZone()
        object : CountDownTimer(currentTimerValue * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tapZone.text = "Tap now!\nTime left: ${millisUntilFinished / 1000}\nTap Count: $tapCount"
                currentTimerDisplayValue = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                isGameActive = false
                timerValueSpinner.isEnabled = true
                startButton.text = "Play Again!"
                tapZone.text = "Time Up!\nTapathon score: $tapCount"
                if (tapCount > currentHighScore) {
                    currentHighScore = tapCount
                    highScoreTextView.text = "High Score: $currentHighScore"
                    saveHighScore(currentTimerValue, currentHighScore)
                }
            }
        }.start()
    }

    private fun updateTapZone() {
        tapZone.text = "Tap now!\nTime left: $currentTimerDisplayValue\nTap Count: $tapCount"
    }

    private fun getHighScore(timerValue: Int): Int {
        val sharedPreferences = getSharedPreferences("Tapathon", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("HighScore_$timerValue", 0)
    }

    private fun saveHighScore(timerValue: Int, highScore: Int) {
        val sharedPreferences = getSharedPreferences("Tapathon", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("HighScore_$timerValue", highScore)
        editor.apply()
    }
}