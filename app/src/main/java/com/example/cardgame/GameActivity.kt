package com.example.cardgame

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameActivity : AppCompatActivity() {

    private var playerWins = 0
    private var opponentWins = 0
    private val roundResults = mutableListOf<RoundResult>()
    private lateinit var playerCard: Card
    private lateinit var opponentCard: Card
    private lateinit var roundResultsAdapter: RoundResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_results)
        roundResultsAdapter = RoundResultsAdapter(roundResults)
        recyclerView.adapter = roundResultsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val playerCardView: ImageView = findViewById(R.id.playerCard)
        val opponentCardView: ImageView = findViewById(R.id.opponentCard)
        val flippedCardView: ImageView = findViewById(R.id.flippedCard)
        val flipButton: Button = findViewById(R.id.flipBtn)
        val drawButton: Button = findViewById(R.id.drawBtn)

        val deck = generateDeck()

        drawButton.setOnClickListener {
            if (deck.size < 2){
                Toast.makeText(this, "No more cards left in the deck! Game over", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "No more cards left in the deck! Game over", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val flippedCard = deck.random().also { deck.remove(it) }
            displayCard(flippedCardView, flippedCard)

            val playerDifference = kotlin.math.abs(flippedCard.value - playerCard.value)
            val opponentDifference = kotlin.math.abs(flippedCard.value - opponentCard.value)

            val result: String = when {
                playerDifference < opponentDifference -> "Player wins!"
                opponentDifference < playerDifference -> "Opponent wins!"
                else -> "Its a tie!"
            }

            roundResults.add(0, RoundResult(roundResults.size + 1, result))
            roundResultsAdapter.notifyItemInserted(0)
            recyclerView.scrollToPosition(0)

            when {
                playerDifference < opponentDifference -> {
                    playerWins++
                }
                opponentDifference < playerDifference -> {
                    opponentWins++
                }
            }
            updateScores()

            drawButton.isVisible = true
            flipButton.isVisible = false
        }
    }


    private fun updateScores(){
        val playerScoreView: TextView = findViewById(R.id.playerScore)
        val opponentScoreView: TextView = findViewById(R.id.opponentScore)

        playerScoreView.text = "Player: $playerWins"
        opponentScoreView.text = "Opponent: $opponentWins"
    }

    private fun generateDeck(): MutableList<Card> {
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

    private fun getCardImageResource(rank: String, suit: String): Int {
        val resourceName = "${rank}_of_${suit}"
        return CardImageMap.cardResources[resourceName]?:
        throw IllegalArgumentException("$resourceName not found")
    }

    private fun displayCard(imageView: ImageView, card: Card){
        imageView.setImageResource(card.imageId)
    }
}

