package com.example.proyectoappfinanzas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Ingreso
import com.example.proyectoappfinanzas.modelos.Gasto
import java.util.Calendar

class KakeboActivity : AppCompatActivity() {
    private lateinit var db: AppBD
    private var mesSeleccionado: Int = 0
    private var anioSeleccionado: Int = 0
    private var diaSeleccionado: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kakebo)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)


        db = AppBD.getDatabase(this)

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

        val categorias = arrayOf("Salario", "Regalo", "Freelance", "Inversión")
        val spinnerCategoria: Spinner = findViewById(R.id.spinnerCategoria)
        val spinnerCategoriaGasto: Spinner = findViewById(R.id.spinnerCategoriaGasto)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
        spinnerCategoriaGasto.adapter = adapter

        val editTextMonto: EditText = findViewById(R.id.dineroIngresado)
        val editTextDescripcion: EditText = findViewById(R.id.descIngreso)
        val editTextMontoGasto: EditText = findViewById(R.id.dineroGastado)
        val editTextDescripcionGasto: EditText = findViewById(R.id.descGasto)
        val btnGuardarTodo: Button = findViewById(R.id.boton_guardar)

        btnGuardarTodo.setOnClickListener {
            if (mesSeleccionado == 0 || anioSeleccionado == 0 || diaSeleccionado == 0) {
                Toast.makeText(this, "Por favor, seleccione un mes y un año.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val montoIngreso = editTextMonto.text.toString().toDoubleOrNull()
            val descripcionIngreso = editTextDescripcion.text.toString()
            val categoriaIngreso = spinnerCategoria.selectedItem.toString()

            if (montoIngreso != null) {
                lifecycleScope.launch {
                    db.ingresoDao().agregarIngreso(Ingreso(monto = montoIngreso, descripcion = descripcionIngreso, categoria = categoriaIngreso, mes = mesSeleccionado, anio = anioSeleccionado, dia = diaSeleccionado))
                    runOnUiThread {
                        Toast.makeText(this@KakeboActivity, "Ingreso guardado", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el ingreso", Toast.LENGTH_SHORT).show()
            }

            val montoGasto = editTextMontoGasto.text.toString().toDoubleOrNull()
            val descripcionGasto = editTextDescripcionGasto.text.toString()
            val categoriaGasto = spinnerCategoriaGasto.selectedItem.toString()

            if (montoGasto != null) {
                lifecycleScope.launch {
                    db.gastoDao().agregarGasto(Gasto(monto = montoGasto, descripcion = descripcionGasto, categoria = categoriaGasto, mes = mesSeleccionado, anio = anioSeleccionado, dia = diaSeleccionado))
                    runOnUiThread {
                        Toast.makeText(this@KakeboActivity, "Gasto guardado", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un monto válido para el gasto", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSeleccionarFecha: Button = findViewById(R.id.btnSeleccionarFecha)
        btnSeleccionarFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dia = calendar.get(Calendar.DAY_OF_MONTH)
            val anio = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)

            val dialog = DatePickerDialog(this, { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                this.anioSeleccionado = anioSeleccionado
                this.mesSeleccionado = mesSeleccionado + 1
                this.diaSeleccionado = diaSeleccionado;
                Toast.makeText(this, "Fecha seleccionada: $diaSeleccionado/$mesSeleccionado/$anioSeleccionado", Toast.LENGTH_SHORT).show()
            }, anio, mes, dia)

            dialog.show()
        }

        val btnMostrarIngresos: Button = findViewById(R.id.boton_mostrarIngresos)
        btnMostrarIngresos.setOnClickListener {
            mostrarIngresos()
        }

        val btnMostrarGastos: Button = findViewById(R.id.boton_mostrarGastos)
        btnMostrarGastos.setOnClickListener {
            mostrarGastos()
        }
    }

    private fun mostrarIngresos() {
        val meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        val builderMes = AlertDialog.Builder(this)
        builderMes.setTitle("Selecciona un mes")
        builderMes.setItems(meses) { dialog, which ->
            mesSeleccionado = which + 1

            val anios = arrayOf("2025", "2026", "2027")
            val builderAnio = AlertDialog.Builder(this)
            builderAnio.setTitle("Selecciona un año")
            builderAnio.setItems(anios) { _, which ->
                anioSeleccionado = anios[which].toInt()

                lifecycleScope.launch {
                    val listaIngresos = db.ingresoDao().obtenerIngresosPorMesYAnio(mesSeleccionado, anioSeleccionado)
                    runOnUiThread {
                        if (listaIngresos.isEmpty()) {
                            Toast.makeText(this@KakeboActivity, "No hay ingresos para el mes y año seleccionados.", Toast.LENGTH_SHORT).show()
                        } else {
                            val ingresosTexto = listaIngresos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria}, \n${it.dia} de ${meses[it.mes - 1]} de ${it.anio}\n-------------" }.toTypedArray()

                            val ingresosDialogBuilder = AlertDialog.Builder(this@KakeboActivity)
                            ingresosDialogBuilder.setTitle("Lista de ingresos de ${meses[mesSeleccionado - 1]} $anioSeleccionado")
                            ingresosDialogBuilder.setItems(ingresosTexto) { dialog, which ->
                                val ingresoSeleccionado = listaIngresos[which]
                                lifecycleScope.launch {
                                    db.ingresoDao().borrarIngreso(ingresoSeleccionado.id)
                                    runOnUiThread {
                                        Toast.makeText(this@KakeboActivity, "Ingreso eliminado: ${ingresoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            ingresosDialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                            ingresosDialogBuilder.create().show()
                        }
                    }
                }
            }
            builderAnio.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            builderAnio.create().show()
        }
        builderMes.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builderMes.create().show()
    }

    private fun mostrarGastos() {
        val meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        val builderMes = AlertDialog.Builder(this)
        builderMes.setTitle("Selecciona un mes")
        builderMes.setItems(meses) { dialog, which ->
            mesSeleccionado = which + 1

            val anios = arrayOf("2025", "2026", "2027")
            val builderAnio = AlertDialog.Builder(this)
            builderAnio.setTitle("Selecciona un año")
            builderAnio.setItems(anios) { _, which ->
                //establecer el año seleccionado
                anioSeleccionado = anios[which].toInt()

                //mostrar los gastos para el mes y año seleccionados
                lifecycleScope.launch {
                    val listaGastos = db.gastoDao().obtenerGastosPorMesYAnio(mesSeleccionado, anioSeleccionado)
                    runOnUiThread {
                        if (listaGastos.isEmpty()) {
                            Toast.makeText(this@KakeboActivity, "No hay gastos para el mes y año seleccionados.", Toast.LENGTH_SHORT).show()
                        } else {
                            //mostrar los gastos en un AlertDialog
                            val gastosTexto = listaGastos.map { "\nDescripción: ${it.descripcion}, \nMonto: ${it.monto}, \nCategoría: ${it.categoria}, \n ${it.dia} de ${meses[it.mes - 1]} de ${it.anio}\n-------------" }.toTypedArray()

                            //crear el AlertDialog con una lista de gastos
                            val gastosDialogBuilder = AlertDialog.Builder(this@KakeboActivity)
                            gastosDialogBuilder.setTitle("Lista de gastos de ${meses[mesSeleccionado - 1]} $anioSeleccionado")
                            gastosDialogBuilder.setItems(gastosTexto) { dialog, which ->
                                //eliminar el gasto seleccionado
                                val gastoSeleccionado = listaGastos[which]
                                lifecycleScope.launch {
                                    db.gastoDao().borrarGasto(gastoSeleccionado.id)
                                    runOnUiThread {
                                        Toast.makeText(this@KakeboActivity, "Gasto eliminado: ${gastoSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            gastosDialogBuilder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                            gastosDialogBuilder.create().show()
                        }
                    }
                }
            }
            builderAnio.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            //mostrar el dialog de selección de año
            builderAnio.create().show()
        }
        builderMes.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        //mostrar el dialog de selección de mes
        builderMes.create().show()
    }
}