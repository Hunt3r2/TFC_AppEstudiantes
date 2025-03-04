package com.example.proyectoappfinanzas

import BaseDatosKakebo
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
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
import java.util.Calendar


class KakeboActivity : AppCompatActivity() {
    private lateinit var dbHelper: BaseDatosKakebo
    private var mesSeleccionado: Int = 0
    private var anioSeleccionado: Int = 0


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
            // Validar si se ha seleccionado un mes y un año
            if (mesSeleccionado == 0 || anioSeleccionado == 0) {
                Toast.makeText(this, "Por favor, seleccione un mes y un año.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar ingreso
            val montoIngreso = editTextMonto.text.toString().toDoubleOrNull()
            val descripcionIngreso = editTextDescripcion.text.toString()
            val categoriaIngreso = spinnerCategoria.selectedItem.toString()

            if (montoIngreso != null) {
                dbHelper.agregarIngreso(montoIngreso, descripcionIngreso, categoriaIngreso, mesSeleccionado, anioSeleccionado)  // Añadir el año aquí
                Toast.makeText(this, "Ingreso guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el ingreso", Toast.LENGTH_SHORT).show()
            }

            // Guardar gasto
            val montoGasto = editTextMontoGasto.text.toString().toDoubleOrNull()
            val descripcionGasto = editTextDescripcionGasto.text.toString()
            val categoriaGasto = spinnerCategoriaGasto.selectedItem.toString()

            if (montoGasto != null) {
                dbHelper.agregarGasto(montoGasto, descripcionGasto, categoriaGasto, mesSeleccionado, anioSeleccionado)  // Añadir el año aquí
                Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el gasto", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSeleccionarFecha: Button = findViewById(R.id.btnSeleccionarFecha)

        btnSeleccionarFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) // Mes en base 0 (Enero es 0, Febrero es 1, etc.)

            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, _ ->
                // Al seleccionar una fecha, guarda el año y el mes
                anioSeleccionado = selectedYear
                mesSeleccionado = selectedMonth + 1 // Ajustar para que Enero sea 1, Febrero sea 2, etc.
                Toast.makeText(this, "Fecha seleccionada: $selectedMonth/$selectedYear", Toast.LENGTH_SHORT).show()
            }, year, month, 1) // El "1" es el día predeterminado (1 de cada mes)

            datePickerDialog.show()
        }


    }

    private fun mostrarIngresos() {
        // Pedir al usuario que seleccione un mes
        val meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        // Crear el AlertDialog para seleccionar un mes
        val builderMes = AlertDialog.Builder(this)
        builderMes.setTitle("Selecciona un mes")
        builderMes.setItems(meses) { dialog, which ->
            // Establecer el mes seleccionado
            mesSeleccionado = which + 1  // Ajustar índice para que Enero sea 1, Febrero 2, etc.

            // Ahora, pedir al usuario que seleccione un año
            val anios = arrayOf("2023", "2024", "2025", "2026") // Puedes agregar más años según tus necesidades

            val builderAnio = AlertDialog.Builder(this)
            builderAnio.setTitle("Selecciona un año")
            builderAnio.setItems(anios) { _, which ->
                // Establecer el año seleccionado
                anioSeleccionado = anios[which].toInt()  // Asignamos el valor del año seleccionado a la variable anioSeleccionado

                // Mostrar los ingresos para el mes y año seleccionados
                val listaIngresos = dbHelper.obtenerIngresosPorMesYAnio(mesSeleccionado, anioSeleccionado)

                if (listaIngresos.isEmpty()) {
                    Toast.makeText(this, "No hay ingresos para el mes y año seleccionados.", Toast.LENGTH_SHORT).show()
                } else {
                    // Mostrar los ingresos en un AlertDialog
                    val ingresosTexto = listaIngresos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria}, \nMes: ${meses[it.mes - 1]}, \nAño: ${it.anio}\n-------------" }.toTypedArray()

                    // Crear el AlertDialog con una lista de ingresos
                    val ingresosDialogBuilder = AlertDialog.Builder(this)
                    ingresosDialogBuilder.setTitle("Lista de ingresos de ${meses[mesSeleccionado - 1]} ${anioSeleccionado}")
                    ingresosDialogBuilder.setItems(ingresosTexto) { dialog, which ->
                        // Eliminar el ingreso seleccionado
                        val ingresoSeleccionado = listaIngresos[which]
                        dbHelper.borrarIngreso(ingresoSeleccionado.id)
                        Toast.makeText(this, "Ingreso eliminado: ${ingresoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
                    }
                    ingresosDialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                    // Mostrar el AlertDialog
                    ingresosDialogBuilder.create().show()
                }
            }
            builderAnio.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            builderAnio.create().show()  // Mostrar el dialog de selección de año
        }
        builderMes.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builderMes.create().show()  // Mostrar el dialog de selección de mes
    }




    fun calcularTotalesPorCategoria(gastos: List<Modelos.Gasto>): Map<String, Float> {
        val totales = mutableMapOf<String, Float>()

        for (gasto in gastos) {
            totales[gasto.categoria] = (totales.getOrDefault(gasto.categoria, 0f) + gasto.monto).toFloat()
        }

        return totales
    }

    private fun mostrarGastos() {
        // Pedir al usuario que seleccione un mes
        val meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        // Crear el AlertDialog para seleccionar un mes
        val builderMes = AlertDialog.Builder(this)
        builderMes.setTitle("Selecciona un mes")
        builderMes.setItems(meses) { dialog, which ->
            // Establecer el mes seleccionado
            mesSeleccionado = which + 1  // Ajustar índice para que Enero sea 1, Febrero 2, etc.

            // Ahora, pedir al usuario que seleccione un año
            val anios = arrayOf("2023", "2024", "2025", "2026") // Puedes agregar más años según tus necesidades

            val builderAnio = AlertDialog.Builder(this)
            builderAnio.setTitle("Selecciona un año")
            builderAnio.setItems(anios) { _, which ->
                // Establecer el año seleccionado
                anioSeleccionado = anios[which].toInt()  // Asignamos el valor del año seleccionado a la variable anioSeleccionado

                // Mostrar los gastos para el mes y año seleccionados
                val listaGastos = dbHelper.obtenerGastosPorMesYAnio(mesSeleccionado, anioSeleccionado)

                if (listaGastos.isEmpty()) {
                    Toast.makeText(this, "No hay gastos para el mes y año seleccionados.", Toast.LENGTH_SHORT).show()
                } else {
                    // Mostrar los gastos en un AlertDialog
                    val gastosTexto = listaGastos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria}, \nMes: ${meses[it.mes - 1]}, \nAño: ${it.anio}\n-------------" }.toTypedArray()

                    // Crear el AlertDialog con una lista de gastos
                    val gastosDialogBuilder = AlertDialog.Builder(this)
                    gastosDialogBuilder.setTitle("Lista de gastos de ${meses[mesSeleccionado - 1]} ${anioSeleccionado}")
                    gastosDialogBuilder.setItems(gastosTexto) { dialog, which ->
                        // Eliminar el gasto seleccionado
                        val gastoSeleccionado = listaGastos[which]
                        dbHelper.borrarGasto(gastoSeleccionado.id)
                        Toast.makeText(this, "Gasto eliminado: ${gastoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
                    }
                    gastosDialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                    // Mostrar el AlertDialog
                    gastosDialogBuilder.create().show()
                }
            }
            builderAnio.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            builderAnio.create().show()  // Mostrar el dialog de selección de año
        }
        builderMes.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builderMes.create().show()  // Mostrar el dialog de selección de mes
    }








}