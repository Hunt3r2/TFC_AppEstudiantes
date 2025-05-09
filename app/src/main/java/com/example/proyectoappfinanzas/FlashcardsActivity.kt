package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.adapters.FlashcardAdapter
import com.example.proyectoappfinanzas.database.AppBD
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FlashcardsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private lateinit var db: AppBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        recyclerView = findViewById(R.id.recyclerFlashcards)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FlashcardAdapter { flashcard ->
            val intent = Intent(this, FlashcardFormularioActivity::class.java)
            intent.putExtra("flashcard_id", flashcard.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        db = AppBD.getDatabase(this)

        val fab: FloatingActionButton = findViewById(R.id.fab_add_flashcard)
        fab.setOnClickListener {
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
            val flashcards = db.flashcardsDao().obtenerTodas()
            runOnUiThread {
                if (flashcards.isEmpty()) {
                    Toast.makeText(this@FlashcardsActivity, "No hay flashcards", Toast.LENGTH_SHORT).show()
                }
                adapter.submitList(flashcards)
            }
        }
    }
}
