package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Flashcard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FlashcardFormularioActivity : AppCompatActivity() {

    private lateinit var etPregunta: EditText
    private lateinit var etRespuesta: EditText
    private lateinit var etNuevaCategoria: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnGuardar: Button

    private val categoriasPredefinidas = mutableListOf("General", "Matemáticas", "Historia")
    private val estados = listOf("Nuevo", "Aprendido", "Revisar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_formulario)

        etPregunta = findViewById(R.id.etPregunta)
        etRespuesta = findViewById(R.id.etRespuesta)
        etNuevaCategoria = findViewById(R.id.etNuevaCategoria)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaFlashcard)
        spinnerEstado = findViewById(R.id.spinnerEstadoFlashcard)
        btnGuardar = findViewById(R.id.btnGuardarFlashcard)

        val volverAtras: FloatingActionButton = findViewById(R.id.volver_atras)
        volverAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val botonInfo: FloatingActionButton = findViewById(R.id.boton_info)
        botonInfo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Información")
            val kakeboInfo = Html.fromHtml(getString(R.string.info_kakebo), Html.FROM_HTML_MODE_LEGACY)
            builder.setMessage(kakeboInfo)
            builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }

        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasPredefinidas)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapterCategoria

        val adapterEstado = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapterEstado

        btnGuardar.setOnClickListener {
            val pregunta = etPregunta.text.toString().trim()
            val respuesta = etRespuesta.text.toString().trim()
            val nuevaCategoria = etNuevaCategoria.text.toString().trim()

            if (pregunta.isBlank() || respuesta.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoria = if (nuevaCategoria.isNotBlank()) {
                if (!categoriasPredefinidas.contains(nuevaCategoria)) {
                    categoriasPredefinidas.add(nuevaCategoria)
                    adapterCategoria.notifyDataSetChanged()
                }
                nuevaCategoria
            } else {
                spinnerCategoria.selectedItem.toString()
            }

            val estado = spinnerEstado.selectedItem.toString()

            val flashcard = Flashcard(pregunta = pregunta, respuesta = respuesta, categoria = categoria, estado = estado)

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
