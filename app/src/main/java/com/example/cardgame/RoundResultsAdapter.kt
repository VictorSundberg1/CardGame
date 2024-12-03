package com.example.cardgame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoundResultsAdapter(val results: List<RoundResult>) : RecyclerView.Adapter<RoundResultsAdapter.RoundResultViewHolder>() {

    inner class RoundResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val roundNumber: TextView = itemView.findViewById(R.id.roundNumber)
        val resultText: TextView = itemView.findViewById(R.id.resultText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_round_result, parent, false)
        return RoundResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoundResultViewHolder, position: Int) {
        val result = results[position]
        holder.roundNumber.text = "Round ${result.roundNumber}"
        holder.resultText.text = result.result
    }

    override fun getItemCount(): Int {
        return results.size
    }

}