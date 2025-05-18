package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.database.AppBD
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class NotasActivity : AppCompatActivity() {
    private lateinit var recyclerNotas: RecyclerView
    private lateinit var fabAgregarNota: FloatingActionButton
    private lateinit var notaAdapter: NotaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        recyclerNotas = findViewById(R.id.recyclerNotas)
        fabAgregarNota = findViewById(R.id.fabAgregarNota)

        notaAdapter = NotaAdapter(onClick = { nota ->
            val intent = Intent(this, NotaFormularioActivity::class.java)
            intent.putExtra("nota_id", nota.id)
            startActivity(intent)
        })

        recyclerNotas.layoutManager = LinearLayoutManager(this)
        recyclerNotas.adapter = notaAdapter

        fabAgregarNota.setOnClickListener {
            startActivity(Intent(this, NotaFormularioActivity::class.java))
        }

        val volverAtras: FloatingActionButton = findViewById(R.id.volver_atras)
        volverAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val botonInfo: FloatingActionButton = findViewById(R.id.boton_info)
        botonInfo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Información")
            val notasInfo = Html.fromHtml(getString(R.string.info_notas), Html.FROM_HTML_MODE_LEGACY)
            builder.setMessage(notasInfo)
            builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNotas()
    }

    private fun cargarNotas() {
        lifecycleScope.launch {
            val notas = AppBD.getDatabase(this@NotasActivity).notaDao().obtenerTodas()
            notaAdapter.submitList(notas)
        }
    }
}
