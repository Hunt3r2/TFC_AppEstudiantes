package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.database.AppBD
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FlashcardsActivity : AppCompatActivity() {

    private lateinit var recyclerFlashcards: RecyclerView
    private lateinit var adapter: FlashcardFlipAdapter
    private lateinit var fabAddFlashcard: FloatingActionButton
    private lateinit var btnFiltrarFlashcards: Button
    private lateinit var spinnerCategoria: Spinner
    private lateinit var spinnerEstado: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcards)

        //Inicialización de vistas desde el layout
        recyclerFlashcards = findViewById(R.id.recyclerFlashcards)
        fabAddFlashcard = findViewById(R.id.btnAgregarFlashcard)
        btnFiltrarFlashcards = findViewById(R.id.btnFiltrar)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaFlashcard)
        spinnerEstado = findViewById(R.id.spinnerEstadoFlashcard)

        //Configuración del adaptador de la lista, con opciones para editar o eliminar flashcards
        adapter = FlashcardFlipAdapter(
            onEditClick = { flashcard ->
                val intent = Intent(this, FlashcardFormularioActivity::class.java)
                intent.putExtra("flashcard_id", flashcard.id)
                startActivity(intent)

            },
            onDeleteClick = { flashcard ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar flashcard")
                    .setMessage("¿Estás seguro de que quieres eliminar esta flashcard?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            val dao = AppBD.getDatabase(this@FlashcardsActivity).flashcardDao()
                            dao.eliminar(flashcard)
                            cargarFlashcards()
                            cargarSpinners()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recyclerFlashcards.layoutManager = LinearLayoutManager(this)
        recyclerFlashcards.itemAnimator = DefaultItemAnimator()
        recyclerFlashcards.adapter = adapter

        fabAddFlashcard.setOnClickListener {
            val intent = Intent(this, FlashcardFormularioActivity::class.java)
            startActivity(intent)
        }

        btnFiltrarFlashcards.setOnClickListener {
            val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
            val estado = spinnerEstado.selectedItem?.toString() ?: ""
            filtrarFlashcardsPorCategoriaYEstado(categoria, estado)
        }

        cargarSpinners()

        val volverAtras: FloatingActionButton = findViewById(R.id.volver_atras)
        volverAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val botonInfo: FloatingActionButton = findViewById(R.id.boton_info)
        botonInfo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Información")
            val flashcardInfo = Html.fromHtml(getString(R.string.info_flashcards), Html.FROM_HTML_MODE_LEGACY)
            builder.setMessage(flashcardInfo)
            builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }

    }

    private fun cargarSpinners() {
        lifecycleScope.launch {
            val dao = AppBD.getDatabase(this@FlashcardsActivity).flashcardDao()
            val flashcards = dao.obtenerTodas()

            //Obtiene valores únicos y no vacíos
            val categorias = flashcards.map { it.categoria }.distinct().filter { it.isNotBlank() }.toMutableList()
            val estados = flashcards.map { it.estado }.distinct().filter { it.isNotBlank() }.toMutableList()

            categorias.add(0, "Todos")
            estados.add(0, "Todos")

            val categoriaAdapter = ArrayAdapter(this@FlashcardsActivity, android.R.layout.simple_spinner_item, categorias)
            val estadoAdapter = ArrayAdapter(this@FlashcardsActivity, android.R.layout.simple_spinner_item, estados)

            categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerCategoria.adapter = categoriaAdapter
            spinnerEstado.adapter = estadoAdapter
        }
    }

    private fun filtrarFlashcardsPorCategoriaYEstado(
        categoriaSeleccionada: String?,
        estadoSeleccionado: String?
    ) {
        lifecycleScope.launch {
            val dao = AppBD.getDatabase(this@FlashcardsActivity).flashcardDao()
            val todas = dao.obtenerTodas()

            val filtradas = todas.filter { flashcard ->
                val coincideCategoria = categoriaSeleccionada == "Todos" || flashcard.categoria == categoriaSeleccionada
                val coincideEstado = estadoSeleccionado == "Todos" || flashcard.estado == estadoSeleccionado
                coincideCategoria && coincideEstado
            }

            adapter.setFlashcards(filtradas)
        }
    }

    //Al volver a la actividad por ejemplo, después de editar, recarga la lista y los filtros
    override fun onResume() {
        super.onResume()
        cargarFlashcards()
        cargarSpinners()
    }

    private fun cargarFlashcards() {
        lifecycleScope.launch {
            val flashcards = AppBD.getDatabase(this@FlashcardsActivity).flashcardDao().obtenerTodas()
            adapter.setFlashcards(flashcards)
        }
    }

}
