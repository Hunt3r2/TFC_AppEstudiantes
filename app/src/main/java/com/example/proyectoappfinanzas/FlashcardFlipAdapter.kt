package com.example.proyectoappfinanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.modelos.Flashcard

class FlashcardFlipAdapter(
    private var flashcards: List<Flashcard> = listOf(),
    private val onEditClick: (Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardFlipAdapter.FlashcardViewHolder>() {

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardFront: CardView = itemView.findViewById(R.id.card_front)
        private val cardBack: CardView = itemView.findViewById(R.id.card_back)
        private val tvFront: TextView = itemView.findViewById(R.id.tv_front_text)
        private val tvBack: TextView = itemView.findViewById(R.id.tv_back_text)

        private var isFlipped = false

        fun bind(flashcard: Flashcard) {
            tvFront.text = flashcard.pregunta
            tvBack.text = flashcard.respuesta

            itemView.setOnClickListener {
                flipCard()
            }

            itemView.setOnLongClickListener {
                onEditClick(flashcard)
                true
            }
        }

        private fun flipCard() {
            val scale = itemView.context.resources.displayMetrics.density
            cardFront.cameraDistance = 8000 * scale
            cardBack.cameraDistance = 8000 * scale

            if (!isFlipped) {
                cardFront.animate().rotationY(90f).setDuration(150).withEndAction {
                    cardFront.visibility = View.GONE
                    cardBack.rotationY = -90f
                    cardBack.visibility = View.VISIBLE
                    cardBack.animate().rotationY(0f).setDuration(150).start()
                }.start()
            } else {
                cardBack.animate().rotationY(90f).setDuration(150).withEndAction {
                    cardBack.visibility = View.GONE
                    cardFront.rotationY = -90f
                    cardFront.visibility = View.VISIBLE
                    cardFront.animate().rotationY(0f).setDuration(150).start()
                }.start()
            }
            isFlipped = !isFlipped
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_flashcard_flip, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(flashcards[position])
    }

    override fun getItemCount(): Int = flashcards.size

    fun setFlashcards(nuevos: List<Flashcard>) {
        flashcards = nuevos
        notifyDataSetChanged()
    }
}
