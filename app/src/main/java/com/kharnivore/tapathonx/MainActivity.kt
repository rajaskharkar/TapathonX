package com.kharnivore.tapathonx

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val singlePlayerButton = findViewById<TextView>(R.id.single_player_button)
        singlePlayerButton.setOnClickListener {
            val intent = Intent(this, SinglePlayerActivity::class.java)
            startActivity(intent)
        }
        val twoPlayerButton = findViewById<TextView>(R.id.two_player_button)
        twoPlayerButton.setOnClickListener {
            val intent = Intent(this, MultiPlayerActivity::class.java)
            startActivity(intent)
        }
    }
}
