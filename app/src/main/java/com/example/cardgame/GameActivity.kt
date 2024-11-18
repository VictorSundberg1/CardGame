package com.example.cardgame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cardgame.databinding.ActivityGameBinding
import com.example.cardgame.databinding.ActivityMainBinding

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val playerCardView: ImageView = findViewById(R.id.playerCard)
        val opponentCardView: ImageView = findViewById(R.id.opponentCard)
        val flippedCardView: ImageView = findViewById(R.id.flippedCard)

        val deck = generateDeck(this)
        val playerCard = deck.random().also { deck.remove(it) }
        val opponentCard = deck.random().also { deck.remove(it) }
        val flippedCard = deck.random().also { deck.remove(it) }

        displayCard(playerCardView, playerCard)
        displayCard(opponentCardView, opponentCard)
        displayCard(flippedCardView, flippedCard)

        Log.i("!!!", "$playerCard")
        Log.i("!!!", "$opponentCard")
        Log.i("!!!", "$flippedCard")
    }

    fun generateDeck(context: Context): MutableList<Card> {
        val suits = listOf("hearts", "diamonds", "clubs", "spades")
        val ranks = listOf("two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king", "ace")
        val values = (2..14).toList()
        val deck = mutableListOf<Card>()

        for (suit in suits){
            for ((index, rank) in ranks.withIndex()){
                val imageResource = getCardImageResource(context, rank, suit)
                deck.add(Card(suit, rank, values[index], imageResource))
            }
        }
        return deck
    }

    fun getCardImageResource(context: Context, rank: String, suit: String): Int {
        val resourceName = "${rank.lowercase()}_of_${suit.lowercase()}"
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }

    fun displayCard(imageView: ImageView, card: Card){
        imageView.setImageResource(card.imageResId)
    }
}