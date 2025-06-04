package com.example.proyectoappfinanzas

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.modelos.Flashcard

class FlashcardFlipAdapter(
    private var flashcards: List<Flashcard> = listOf(),
    private val onEditClick: (Flashcard) -> Unit,
    private val onDeleteClick: (Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardFlipAdapter.FlashcardViewHolder>() {

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardFront: CardView = itemView.findViewById(R.id.card_front)
        val cardBack: CardView = itemView.findViewById(R.id.card_back)
        private val tvFront: TextView = itemView.findViewById(R.id.tv_front_text)
        private val tvBack: TextView = itemView.findViewById(R.id.tv_back_text)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)

        private var isFlipped = false

        fun bind(flashcard: Flashcard) {
            tvFront.text = flashcard.pregunta
            tvBack.text = flashcard.respuesta

            cardFront.visibility = View.VISIBLE
            cardBack.visibility = View.GONE
            isFlipped = false

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
                val frontOut = ObjectAnimator.ofFloat(cardFront, "rotationY", 0f, 90f)
                val backIn = ObjectAnimator.ofFloat(cardBack, "rotationY", -90f, 0f)

                frontOut.duration = 300
                backIn.duration = 300

                frontOut.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        cardFront.visibility = View.GONE
                        cardBack.visibility = View.VISIBLE
                        backIn.start()
                    }
                })

                frontOut.start()
            } else {
                val backOut = ObjectAnimator.ofFloat(cardBack, "rotationY", 0f, 90f)
                val frontIn = ObjectAnimator.ofFloat(cardFront, "rotationY", -90f, 0f)

                backOut.duration = 300
                frontIn.duration = 300

                backOut.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        cardBack.visibility = View.GONE
                        cardFront.visibility = View.VISIBLE
                        frontIn.start()
                    }
                })

                backOut.start()
            }
            isFlipped = !isFlipped
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_flashcard_flip, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val flashcard = flashcards[position]
        holder.bind(flashcards[position])


        holder.btnEditar.setOnClickListener {
            onEditClick(flashcard)
        }

        holder.btnBorrar.setOnClickListener {
            onDeleteClick(flashcard)
        }
    }

    override fun getItemCount(): Int = flashcards.size

    fun setFlashcards(nuevos: List<Flashcard>) {
        flashcards = nuevos
        notifyDataSetChanged()
    }
}
