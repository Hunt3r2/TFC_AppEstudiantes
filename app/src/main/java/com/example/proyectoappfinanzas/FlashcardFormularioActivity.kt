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

    private lateinit var etPregunta: EditText
    private lateinit var etRespuesta: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_formulario)

        etPregunta = findViewById(R.id.etPregunta)
        etRespuesta = findViewById(R.id.etRespuesta)
        btnGuardar = findViewById(R.id.btnGuardarFlashcard)


        btnGuardar.setOnClickListener {
            val pregunta = etPregunta.text.toString().trim()
            val respuesta = etRespuesta.text.toString().trim()


            if (pregunta.isBlank() || respuesta.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val flashcard = Flashcard(pregunta = pregunta, respuesta = respuesta)

            lifecycleScope.launch {
                AppBD.getDatabase(this@FlashcardFormularioActivity).flashcardDao().insertar(flashcard)
                runOnUiThread {
                    Toast.makeText(this@FlashcardFormularioActivity, "Flashcard guardada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
