package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Flashcard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FlashcardsActivity : AppCompatActivity() {
    private lateinit var recyclerFlashcards: RecyclerView
    private lateinit var adapter: FlashcardFlipAdapter
    private lateinit var fabAddFlashcard: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcards)

        recyclerFlashcards = findViewById(R.id.recyclerFlashcards)
        fabAddFlashcard = findViewById(R.id.btnAgregarFlashcard)

        adapter = FlashcardFlipAdapter(onEditClick = { flashcard ->
            val intent = Intent(this, FlashcardFormularioActivity::class.java)
            intent.putExtra("flashcard_id", flashcard.id)
            startActivity(intent)
        })

        recyclerFlashcards.layoutManager = LinearLayoutManager(this)
        recyclerFlashcards.itemAnimator = DefaultItemAnimator()
        recyclerFlashcards.adapter = adapter

        fabAddFlashcard.setOnClickListener {
            val intent = Intent(this, FlashcardFormularioActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarFlashcards()
    }

    private fun cargarFlashcards() {
        lifecycleScope.launch {
            val flashcards = AppBD.getDatabase(this@FlashcardsActivity).flashcardDao().obtenerTodas()
            adapter.setFlashcards(flashcards)
        }
    }
}
