package com.example.cardgame

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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
    private lateinit var deck: MutableList<Card>
    private lateinit var playerCardView: ImageView
    private lateinit var opponentCardView: ImageView
    private lateinit var flippedCardView: ImageView
    private lateinit var flipButton: Button
    private lateinit var drawButton: Button

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

        playerCardView = findViewById(R.id.playerCard)
        opponentCardView = findViewById(R.id.opponentCard)
        flippedCardView = findViewById(R.id.flippedCard)
        flipButton = findViewById(R.id.flipBtn)
        drawButton = findViewById(R.id.drawBtn)

        deck = generateDeck()

        drawButton.setOnClickListener {
            drawCard(playerCardView, opponentCardView, flippedCardView, flipButton, drawButton)
        }

        flipButton.setOnClickListener {
            flipCard(flippedCardView, recyclerView, flipButton, drawButton)
        }
    }

    //saves the stats of gamesPlayed and (game)Wins to sharedPreferences
    private fun saveStats(wins: Int, gamesPlayed: Int){
        val sharedPrefs = getSharedPreferences("Game_stats", MODE_PRIVATE)
        with(sharedPrefs.edit()){
            putInt("wins", wins)
            putInt("gamesPlayed", gamesPlayed)
            apply()
        }
    }

    //saves the stats of gamesPlayed and (game)Wins to sharedPreferences
    private fun endGame(playerWon: Boolean){
        val sharedPrefs = getSharedPreferences("Game_stats", MODE_PRIVATE)
        val previousWins = sharedPrefs.getInt("wins", 0)
        val previousGamesPlayed = sharedPrefs.getInt("gamesPlayed", 0)

        val newWins = if (playerWon) previousWins + 1 else previousWins
        val newGamesPlayed = previousGamesPlayed + 1

        saveStats(newWins, newGamesPlayed)
    }

    //Opens the dialog window when the deck is empty with a menu for restart or exit
    private fun restartGameDialog(){
        AlertDialog.Builder(this)
            .setTitle("Restart Game")
            .setMessage("No more cards in the deck! Would you like to restart the game?")
            .setPositiveButton("Yes"){_, _ ->
                endGame(playerWins > opponentWins)
                resetGame()
            }
            .setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Exit") {_, _ ->
                endGame(playerWins > opponentWins)
                finish() }

            .setCancelable(false)
            .show()
    }

    //function to restart the game. Resets the scores and creates a new deck of 52 cards and removes the old cards
    private fun resetGame(){
        deck = generateDeck()

        playerWins = 0
        opponentWins = 0
        roundResults.clear()

        roundResultsAdapter.notifyDataSetChanged()

        updateScores()

        playerCardView.setImageResource(R.drawable.back_of_card)
        opponentCardView.setImageResource(R.drawable.back_of_card)
        flippedCardView.setImageResource(R.drawable.back_of_card)
        flipButton.isVisible = false
        drawButton.isVisible = true

        Toast.makeText(this, "Game restarted!", Toast.LENGTH_SHORT).show()
    }

    //Get both the playerCard and the opponentCard and show them
    private fun drawCard(
        playerCardView: ImageView,
        opponentCardView: ImageView,
        flippedCardView: ImageView,
        flipButton: Button,
        drawButton: Button
    ){
        if (deck.size < 2){
            restartGameDialog()
            return
        }

        playerCard = drawCardFromDeck()
        opponentCard = drawCardFromDeck()

        displayCard(playerCardView, playerCard)
        displayCard(opponentCardView, opponentCard)
        flippedCardView.setImageResource(R.drawable.back_of_card)

        flipButton.isVisible = true
        drawButton.isVisible = false
    }

    //Get the flipcard and show it then check the winner and update the recyclerview with the result
    private fun flipCard(
        flippedCardView: ImageView,
        recyclerView: RecyclerView,
        flipButton: Button,
        drawButton: Button
    ){
        if (deck.isEmpty()){
            restartGameDialog()
            return
        }

        val flippedCard = drawCardFromDeck()
        displayCard(flippedCardView, flippedCard)

        val result = checkRound(flippedCard)

        roundResults.add(0, RoundResult(roundResults.size + 1, result))
        roundResultsAdapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)

        updateScores()

        drawButton.isVisible = true
        flipButton.isVisible = false

    }

    //checking for who won the round by getting the absolute value and then comparing them to see who wins or if its a tie
    private fun checkRound(flippedCard: Card): String{

        val playerDifference = kotlin.math.abs(flippedCard.value - playerCard.value)
        val opponentDifference = kotlin.math.abs(flippedCard.value - opponentCard.value)

        return when {
            playerDifference < opponentDifference ->{
                playerWins++
                "Player wins!"
            }
            opponentDifference < playerDifference -> {
                opponentWins++
                "Opponent wins!"
            }
            else -> "It's a tie!"
        }
    }

    //get one of the cards from the deck then remove the card from the deck to not get 2 of the same cards
    private fun drawCardFromDeck(): Card {
        return deck.random().also { deck.remove(it) }
    }

    //function to update the textviews of scores for player and opponent for each round
    private fun updateScores(){
        val playerScoreView: TextView = findViewById(R.id.playerScore)
        val opponentScoreView: TextView = findViewById(R.id.opponentScore)

        playerScoreView.text = "Player: $playerWins"
        opponentScoreView.text = "Opponent: $opponentWins"
    }

    //function to create the deck of 52 individual cards and return the full deck
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

    //function to get the imageResource from the CardImageMap or throw an exception if the resource doesn't exist
    private fun getCardImageResource(rank: String, suit: String): Int {
        val resourceName = "${rank}_of_${suit}"
        return CardImageMap.cardResources[resourceName]?:
        throw IllegalArgumentException("$resourceName not found")
    }

    //function to display the imageResource in the imageview
    private fun displayCard(imageView: ImageView, card: Card){
        imageView.setImageResource(card.imageId)
    }
}

