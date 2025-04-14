package com.example.proyectoappfinanzas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppDatabase
import com.example.proyectoappfinanzas.modelos.Nota
import kotlinx.coroutines.launch
import java.util.*

class NotaFormularioActivity : AppCompatActivity() {
    private lateinit var etTitulo: EditText
    private lateinit var etContenido: EditText
    private lateinit var btnGuardar: Button

    private var notaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota_formulario)

        etTitulo = findViewById(R.id.etTitulo)
        etContenido = findViewById(R.id.etContenido)
        btnGuardar = findViewById(R.id.btnGuardarNota)

        notaId = intent.getIntExtra("nota_id", -1).takeIf { it != -1 }
        if (notaId != null) {
            cargarNota(notaId!!)
        }

        btnGuardar.setOnClickListener {
            guardarNota()
        }
    }

    private fun cargarNota(id: Int) {
        lifecycleScope.launch {
            val nota = AppDatabase.getDatabase(this@NotaFormularioActivity).notaDao().obtenerPorId(id)
            nota?.let {
                etTitulo.setText(it.titulo)
                etContenido.setText(it.contenido)
            }
        }
    }

    private fun guardarNota() {
        val titulo = etTitulo.text.toString()
        val contenido = etContenido.text.toString()

        if (titulo.isBlank() || contenido.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(this@NotaFormularioActivity).notaDao()
            if (notaId == null) {
                dao.insertar(Nota(0, titulo, contenido, Date()))
            } else {
                dao.actualizar(Nota(notaId!!, titulo, contenido, Date()))
            }
            finish()
        }
    }
}
