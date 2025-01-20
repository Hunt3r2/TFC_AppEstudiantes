package com.example.proyectoappfinanzas

import BaseDatosKakebo
import android.annotation.SuppressLint
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
    @SuppressLint("MissingInflatedId")
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

        lateinit var spinnerCategoriaGasto: Spinner
        spinnerCategoriaGasto = findViewById(R.id.spinnerCategoriaGasto)

        // Configurar el adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
        spinnerCategoriaGasto.adapter = adapter

        val btnMostrarIngresos: Button = findViewById(R.id.boton_mostrarIngresos)
        btnMostrarIngresos.setOnClickListener {
            mostrarIngresos()
        }

        val btnMostrarGastos: Button = findViewById(R.id.boton_mostrarGastos)
        btnMostrarGastos.setOnClickListener {
            mostrarGastos()
        }

        dbHelper = BaseDatosKakebo(this)
        val editTextMonto: EditText = findViewById(R.id.dineroIngresado)
        val editTextDescripcion: EditText = findViewById(R.id.descIngreso)
        val editTextMontoGasto: EditText = findViewById(R.id.dineroGastado)
        val editTextDescripcionGasto: EditText = findViewById(R.id.descGasto)
        val btnGuardarTodo: Button = findViewById(R.id.boton_guardar)

        btnGuardarTodo.setOnClickListener {
//            // Guardar ingreso
            val montoIngreso = editTextMonto.text.toString().toDoubleOrNull()
            val descripcionIngreso = editTextDescripcion.text.toString()
            val categoriaIngreso = spinnerCategoria.selectedItem.toString()

            if (montoIngreso != null) {
                dbHelper.agregarIngreso(montoIngreso, descripcionIngreso, categoriaIngreso)
                Toast.makeText(this, "Ingreso guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el ingreso", Toast.LENGTH_SHORT).show()
            }

            // Guardar gasto
            val montoGasto = editTextMontoGasto.text.toString().toDoubleOrNull()
            val descripcionGasto = editTextDescripcionGasto.text.toString()
            val categoriaGasto = spinnerCategoriaGasto.selectedItem.toString()

            if (montoGasto != null) {
                dbHelper.agregarGasto(montoGasto, descripcionGasto, categoriaGasto)
                Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el gasto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarIngresos() {
        val listaIngresos = dbHelper.obtenerIngresos()
        val ingresosTexto = listaIngresos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria} \n-------------" }.toTypedArray()

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

    fun calcularTotalesPorCategoria(gastos: List<Modelos.Gasto>): Map<String, Float> {
        val totales = mutableMapOf<String, Float>()

        for (gasto in gastos) {
            totales[gasto.categoria] = (totales.getOrDefault(gasto.categoria, 0f) + gasto.monto).toFloat()
        }

        return totales
    }

    private fun mostrarGastos() {
        val listaGastos = dbHelper.obtenerGastos()
        val gastosTexto = listaGastos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria} \n-------------" }.toTypedArray()

        // Crear el AlertDialog con una lista de ingresos
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Lista de gastos")
        builder.setItems(gastosTexto) { dialog, which ->
            // Eliminar el ingreso seleccionado
            val gastoSeleccionado = listaGastos[which]
            dbHelper.borrarIngreso(gastoSeleccionado.id)
            Toast.makeText(this, "Gasto eliminado: ${gastoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        // Mostrar el AlertDialog
        builder.create().show()
    }




}