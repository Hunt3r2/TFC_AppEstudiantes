package com.example.proyectoappfinanzas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Flashcard
import kotlinx.coroutines.launch

class FlashcardFormularioActivity : AppCompatActivity() {

    private lateinit var etFront: EditText
    private lateinit var etBack: EditText
    private lateinit var btnGuardar: Button

    private var flashcardId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_formulario)

        etFront = findViewById(R.id.et_front)
        etBack = findViewById(R.id.et_back)
        btnGuardar = findViewById(R.id.btn_guardar_flashcard)

        flashcardId = intent.getLongExtra("flashcard_id", -1).takeIf { it != -1L }

        if (flashcardId != null) {
            lifecycleScope.launch {
                val flashcard = AppBD.getDatabase(this@FlashcardFormularioActivity).flashcardDao().obtenerPorId(flashcardId!!)
                flashcard?.let {
                    etFront.setText(it.front)
                    etBack.setText(it.back)
                }
            }
        }

        btnGuardar.setOnClickListener {
            val front = etFront.text.toString()
            val back = etBack.text.toString()

            if (front.isBlank() || back.isBlank()) {
                Toast.makeText(this, "Completa ambos lados de la flashcard", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val dao = AppBD.getDatabase(this@FlashcardFormularioActivity).flashcardDao()
                if (flashcardId == null) {
                    dao.insertar(Flashcard(front = front, back = back))
                } else {
                    dao.actualizar(Flashcard(id = flashcardId!!, front = front, back = back))
                }
                finish()
            }
        }
    }
}
