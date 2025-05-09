package com.example.proyectoappfinanzas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.R
import com.example.proyectoappfinanzas.modelos.Flashcard

class FlashcardAdapter(
    private val flashcards: List<Flashcard>,
    private val onItemClick: (Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFront: TextView = itemView.findViewById(R.id.tvFront)
        val tvBack: TextView = itemView.findViewById(R.id.tvBack)

        fun bind(flashcard: Flashcard) {
            tvFront.text = flashcard.pregunta
            tvBack.text = flashcard.respuesta
            itemView.setOnClickListener { onItemClick(flashcard) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(flashcards[position])
    }

    override fun getItemCount(): Int = flashcards.size
}
