package com.example.cardgame

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible

class GameActivity : AppCompatActivity() {

    var playerWins = 0
    var opponentWins = 0
    lateinit var playerCard: Card
    lateinit var opponentCard: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val deck = generateDeck()

        val playerCardView: ImageView = findViewById(R.id.playerCard)
        val opponentCardView: ImageView = findViewById(R.id.opponentCard)
        val flippedCardView: ImageView = findViewById(R.id.flippedCard)
        val flipButton: Button = findViewById(R.id.flipBtn)
        val drawButton: Button = findViewById(R.id.drawBtn)

        drawButton.setOnClickListener {
            if (deck.size < 2){
                showResult("No more cards left in the deck! Game over")
                return@setOnClickListener
            }

            playerCard = deck.random().also { deck.remove(it) }
            opponentCard = deck.random().also { deck.remove(it) }
            displayCard(playerCardView, playerCard)
            displayCard(opponentCardView, opponentCard)
            flippedCardView.setImageResource(R.drawable.back_of_card)
            flipButton.isVisible = true
            drawButton.isVisible = false
        }

        flipButton.setOnClickListener {
            if (deck.isEmpty()){
                showResult("No more cards left in the deck! Game over")
                return@setOnClickListener
            }

            val flippedCard = deck.random().also { deck.remove(it) }
            displayCard(flippedCardView, flippedCard)

            val playerDifference = kotlin.math.abs(flippedCard.value - playerCard.value)
            val opponentDifference = kotlin.math.abs(flippedCard.value - opponentCard.value)

            when {
                playerDifference < opponentDifference -> {
                    playerWins++
                    showResult("Player wins this round!")
                }
                opponentDifference < playerDifference -> {
                    opponentWins++
                    showResult("Opponent wins this round!")
                }
                else -> {
                    showResult("It's a tie!")
                }
            }
            updateScores()

            drawButton.isVisible = true
            flipButton.isVisible = false
        }
    }

        fun resetGame(){
            playerWins = 0
            opponentWins = 0
            updateScores()
        }


    fun showResult(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun updateScores(){
        val playerScoreView: TextView = findViewById(R.id.playerScore)
        val opponentScoreView: TextView = findViewById(R.id.opponentScore)

        playerScoreView.text = "Player: $playerWins"
        opponentScoreView.text = "Opponent: $opponentWins"
    }

    fun generateDeck(): MutableList<Card> {
        val suits = listOf("hearts", "diamonds", "clubs", "spades")
        val rankToValue = mapOf(
            "two" to 2, "three" to 3, "four" to 4, "five" to 5,
            "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
            "ten" to 10, "jack" to 11, "queen" to 12, "king" to 13, "ace" to 14
        )

        val deck = mutableListOf<Card>()

        for (suit in suits) {
            for (rank in rankToValue.keys) {
                val imageResource = getCardImageResource(rank, suit)
                val value = rankToValue[rank] ?: 0
                deck.add(Card(suit, rank, value, imageResource))
            }
        }
        return deck
    }

    fun getCardImageResource(rank: String, suit: String): Int {
        val resourceName = "${rank.lowercase()}_of_${suit.lowercase()}"
        return CardImageMap.cardResources[resourceName]?:
        throw IllegalArgumentException("$resourceName not found")
    }

    fun displayCard(imageView: ImageView, card: Card){
        imageView.setImageResource(card.imageId)
    }
}

