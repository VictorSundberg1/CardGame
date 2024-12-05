package com.example.cardgame

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.databinding.ActivityRulesBinding

class RulesActivity : AppCompatActivity() {
    lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRulesBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back_btn = findViewById<Button>(R.id.back_btn)

        back_btn.setOnClickListener {
            finish()
        }
    }
}