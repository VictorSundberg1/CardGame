package com.example.cardgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cardgame.databinding.ActivityGameBinding
import com.example.cardgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get the stats from the sharedpreference and display it in a Dialog window
        binding.statisticsBtn.setOnClickListener {
            val sharedPrefs = getSharedPreferences("Game_stats", MODE_PRIVATE)
            val wins = sharedPrefs.getInt("wins", 0)
            val gamesPlayed = sharedPrefs.getInt("gamesPlayed", 0)

            // Visa en dialog med statistiken
            AlertDialog.Builder(this)
                .setTitle("Game Statistics")
                .setMessage("Wins: $wins \nGames Played: $gamesPlayed")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.menuRulesBtn.setOnClickListener{
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        binding.menuStartBtn.setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

}