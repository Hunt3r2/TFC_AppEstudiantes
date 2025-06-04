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

    private var flashcardId: Int = -1
    private var flashcardActual: Flashcard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_formulario)

        etPregunta = findViewById(R.id.etPregunta)
        etRespuesta = findViewById(R.id.etRespuesta)
        etNuevaCategoria = findViewById(R.id.etNuevaCategoria)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaFlashcard)
        spinnerEstado = findViewById(R.id.spinnerEstadoFlashcard)
        btnGuardar = findViewById(R.id.btnGuardarFlashcard)

        val flashcardIdLong = intent.extras?.get("flashcard_id") as? Long
        flashcardId = flashcardIdLong?.toInt() ?: -1

        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasPredefinidas)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapterCategoria

        val adapterEstado = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapterEstado

        //Cargar flashcard si es edición
        if (flashcardId != -1) {
            lifecycleScope.launch {
                val dao = AppBD.getDatabase(this@FlashcardFormularioActivity).flashcardDao()
                flashcardActual = dao.obtenerPorId(flashcardId)

                flashcardActual?.let { flashcard ->
                    runOnUiThread {
                        etPregunta.setText(flashcard.pregunta)
                        etRespuesta.setText(flashcard.respuesta)

                        val catIndex = categoriasPredefinidas.indexOf(flashcard.categoria)
                        if (catIndex >= 0) {
                            spinnerCategoria.setSelection(catIndex)
                            etNuevaCategoria.setText("")
                        } else {
                            categoriasPredefinidas.add(flashcard.categoria)
                            adapterCategoria.notifyDataSetChanged()
                            etNuevaCategoria.setText(flashcard.categoria)
                        }

                        val estadoIndex = estados.indexOf(flashcard.estado)
                        spinnerEstado.setSelection(estadoIndex.coerceAtLeast(0))

                        btnGuardar.text = "Actualizar"
                    }
                }
            }
        }

        //Botón guardar
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

            lifecycleScope.launch {
                val dao = AppBD.getDatabase(this@FlashcardFormularioActivity).flashcardDao()

                if (flashcardActual != null) {
                    val flashcardEditada = flashcardActual!!.copy(
                        pregunta = pregunta,
                        respuesta = respuesta,
                        categoria = categoria,
                        estado = estado
                    )
                    dao.actualizar(flashcardEditada)
                } else {
                    val nuevaFlashcard = Flashcard(
                        pregunta = pregunta,
                        respuesta = respuesta,
                        categoria = categoria,
                        estado = estado
                    )
                    dao.insertar(nuevaFlashcard)
                }

                runOnUiThread {
                    Toast.makeText(this@FlashcardFormularioActivity, "Flashcard guardada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        //Botón volver
        findViewById<FloatingActionButton>(R.id.volver_atras).setOnClickListener {
            val destino = if (btnGuardar.text == "Actualizar") FlashcardsActivity::class.java else FlashcardsActivity::class.java
            startActivity(Intent(this, destino))
        }

        //Botón info
        findViewById<FloatingActionButton>(R.id.boton_info).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Información")
                .setMessage(Html.fromHtml(getString(R.string.info_kakebo), Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
