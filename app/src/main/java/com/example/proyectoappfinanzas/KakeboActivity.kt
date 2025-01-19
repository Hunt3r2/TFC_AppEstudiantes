package com.example.proyectoappfinanzas

import BaseDatosKakebo
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class KakeboActivity : AppCompatActivity() {
    private lateinit var dbHelper: BaseDatosKakebo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kakebo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volverAtras: FloatingActionButton = findViewById(R.id.volver_atras)

        // Configura el listener para el botón
        volverAtras.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Inicia la nueva actividad
        }

        val botonInfo: FloatingActionButton = findViewById(R.id.boton_info)

        botonInfo.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Información")
            val kakeboInfo= Html.fromHtml(getString(R.string.info_kakebo), Html.FROM_HTML_MODE_LEGACY)
            builder.setMessage(kakeboInfo)
            builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }

            // Muestra el AlertDialog
            val dialog = builder.create()
            dialog.show()
        }

        lateinit var spinnerCategoria: Spinner
        val categorias = arrayOf("Salario", "Regalo", "Freelance", "Inversión")
        spinnerCategoria = findViewById(R.id.spinnerCategoria)

        // Configurar el adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter

        val btnMostrarIngresos: Button = findViewById(R.id.boton_mostrarIngresos)
        btnMostrarIngresos.setOnClickListener {
            mostrarIngresos()
        }

        dbHelper = BaseDatosKakebo(this)
        val editTextMonto: EditText = findViewById(R.id.dineroIngresado)
        val editTextDescripcion: EditText = findViewById(R.id.descIngreso)
        val btnGuardar: Button = findViewById(R.id.boton_guardarIngresos)

        btnGuardar.setOnClickListener {
            val monto = editTextMonto.text.toString().toDoubleOrNull()
            val descripcion = editTextDescripcion.text.toString()
            val categoria = spinnerCategoria.selectedItem.toString()

            if (monto != null) {
                // Llamar al método para agregar ingreso
                dbHelper.agregarIngreso(monto, descripcion, categoria)
                Toast.makeText(this, "Ingreso guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido", Toast.LENGTH_SHORT).show()
            }
        }

        val listaIngresos = dbHelper.obtenerIngresos()
        for (ingreso in listaIngresos) {
            // Aquí puedes mostrar los ingresos en la UI, por ejemplo, en un RecyclerView
            println("Ingreso: ${ingreso.descripcion}, Monto: ${ingreso.monto}, Categoría: ${ingreso.categoria}")
        }
    }

    private fun mostrarIngresos() {
        val listaIngresos = dbHelper.obtenerIngresos()
        val ingresosTexto = listaIngresos.map { "ID: ${it.id}, Descripción: ${it.descripcion}, Monto: ${it.monto}, Categoría: ${it.categoria}" }.toTypedArray()

        // Crear el AlertDialog con una lista de ingresos
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Lista de Ingresos")
        builder.setItems(ingresosTexto) { dialog, which ->
            // Eliminar el ingreso seleccionado
            val ingresoSeleccionado = listaIngresos[which]
            dbHelper.borrarIngreso(ingresoSeleccionado.id)
            Toast.makeText(this, "Ingreso eliminado: ${ingresoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        // Mostrar el AlertDialog
        builder.create().show()
    }

}