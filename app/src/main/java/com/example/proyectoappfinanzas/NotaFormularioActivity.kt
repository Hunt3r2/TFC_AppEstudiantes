package com.example.proyectoappfinanzas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Nota
import kotlinx.coroutines.launch
import java.util.*

class NotaFormularioActivity : AppCompatActivity() {
    private lateinit var etTitulo: EditText
    private lateinit var etContenido: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    //ID de la nota que se va a editar (null si es una nueva)
    private var notaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota_formulario)

        etTitulo = findViewById(R.id.etTitulo)
        etContenido = findViewById(R.id.etContenido)
        btnGuardar = findViewById(R.id.btnGuardarNota)
        btnEliminar = findViewById(R.id.btnEliminarNota)

        notaId = intent.getIntExtra("nota_id", -1).takeIf { it != -1 }
        if (notaId != null) {
            cargarNota(notaId!!)
            btnEliminar.visibility = Button.VISIBLE
        }

        btnGuardar.setOnClickListener {
            guardarNota()
        }

        btnEliminar.setOnClickListener {
            eliminarNota()
        }
    }

    private fun cargarNota(id: Int) {
        lifecycleScope.launch {
            val nota = AppBD.getDatabase(this@NotaFormularioActivity).notaDao().obtenerPorId(id)
            nota?.let {
                etTitulo.setText(it.titulo)
                etContenido.setText(it.contenido)
            }
        }
    }

    private fun guardarNota() {
        val titulo = etTitulo.text.toString()
        val contenido = etContenido.text.toString()

        //Validación de campos vacíos
        if (titulo.isBlank() || contenido.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val dao = AppBD.getDatabase(this@NotaFormularioActivity).notaDao()
            if (notaId == null) {
                dao.insertar(Nota(0, titulo, contenido, Date()))
            } else {
                dao.actualizar(Nota(notaId!!, titulo, contenido, Date()))
            }
            finish()
        }
    }

    private fun eliminarNota() {
        notaId?.let { id ->
            lifecycleScope.launch {
                val dao = AppBD.getDatabase(this@NotaFormularioActivity).notaDao()
                dao.eliminar(Nota(id, "", "", Date()))
                Toast.makeText(this@NotaFormularioActivity, "Nota eliminada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
