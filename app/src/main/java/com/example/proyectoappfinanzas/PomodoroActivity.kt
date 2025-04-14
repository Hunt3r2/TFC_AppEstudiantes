package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectoappfinanzas.database.AppDatabase
import com.example.proyectoappfinanzas.modelos.Pomodoro
import com.example.proyectoappfinanzas.PomodoroTemporizador
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class PomodoroActivity : AppCompatActivity() {
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnResume: Button
    private lateinit var btnStop: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var etDescripcion: EditText
    private lateinit var etTrabajo: EditText
    private lateinit var etDescanso: EditText
    private lateinit var etPausaLarga: EditText
    private lateinit var etRepeticiones: EditText
    private lateinit var spinnerPomodoros: Spinner

    private var listaPomodoros: List<Pomodoro> = listOf()
    private var timer: PomodoroTemporizador? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        tvTimer = findViewById(R.id.tv_timer)
        btnStart = findViewById(R.id.btn_start)
        btnPause = findViewById(R.id.btn_pause)
        btnResume = findViewById(R.id.btn_resume)
        btnStop = findViewById(R.id.btn_stop)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnEliminar = Button(this).apply { text = "Eliminar Pomodoro" }
        etDescripcion = findViewById(R.id.et_descripcion)
        etTrabajo = findViewById(R.id.et_trabajo)
        etDescanso = findViewById(R.id.et_descanso)
        etPausaLarga = findViewById(R.id.et_pausa_larga)
        etRepeticiones = findViewById(R.id.et_repeticiones)
        spinnerPomodoros = findViewById(R.id.spinner_pomodoros)

        (spinnerPomodoros.parent as LinearLayout).addView(btnEliminar)

        btnStart.isEnabled = false

        cargarPomodorosGuardados()

        btnGuardar.setOnClickListener {
            guardarConfiguracion()
        }

        btnEliminar.setOnClickListener {
            val seleccionado = spinnerPomodoros.selectedItemPosition
            if (seleccionado in listaPomodoros.indices) {
                val pomodoro = listaPomodoros[seleccionado]
                lifecycleScope.launch {
                    AppDatabase.getDatabase(this@PomodoroActivity).pomodoroDao().eliminar(pomodoro)
                    cargarPomodorosGuardados()
                    Toast.makeText(this@PomodoroActivity, "Pomodoro eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnStart.setOnClickListener {
            val seleccionado = spinnerPomodoros.selectedItemPosition
            if (listaPomodoros.isEmpty() || seleccionado !in listaPomodoros.indices) {
                Toast.makeText(this, "Selecciona una configuración válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            iniciarTemporizador(listaPomodoros[seleccionado])
        }

        btnPause.setOnClickListener {
            timer?.pausar()
            btnPause.visibility = View.GONE
            btnResume.visibility = View.VISIBLE
        }

        btnResume.setOnClickListener {
            timer?.reanudar()
            btnResume.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }

        btnStop.setOnClickListener {
            detenerTemporizador()
        }

        spinnerPomodoros.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position in listaPomodoros.indices) {
                    val pomodoro = listaPomodoros[position]
                    etDescripcion.setText(pomodoro.descripcion)
                    etTrabajo.setText(pomodoro.tiempo_trabajo.toString())
                    etDescanso.setText(pomodoro.tiempo_descanso.toString())
                    etPausaLarga.setText(pomodoro.tiempo_pausa_larga.toString())
                    etRepeticiones.setText(pomodoro.repetir_pomodoro.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

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
    }

    private fun guardarConfiguracion() {
        val descripcion = etDescripcion.text.toString()
        val trabajo = etTrabajo.text.toString().toIntOrNull() ?: 25
        val descanso = etDescanso.text.toString().toIntOrNull() ?: 5
        val pausa = etPausaLarga.text.toString().toIntOrNull() ?: 15
        val repeticiones = etRepeticiones.text.toString().toIntOrNull() ?: 4

        val pomodoro = Pomodoro(
            descripcion = descripcion,
            tiempo_trabajo = trabajo,
            tiempo_descanso = descanso,
            tiempo_pausa_larga = pausa,
            repetir_pomodoro = repeticiones
        )

        lifecycleScope.launch {
            AppDatabase.getDatabase(this@PomodoroActivity).pomodoroDao().insertar(pomodoro)
            cargarPomodorosGuardados()
            Toast.makeText(this@PomodoroActivity, "Pomodoro guardado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarPomodorosGuardados() {
        lifecycleScope.launch {
            listaPomodoros = AppDatabase.getDatabase(this@PomodoroActivity).pomodoroDao().obtenerTodos()
            val descripciones = listaPomodoros.map { it.descripcion }
            val adapter = ArrayAdapter(this@PomodoroActivity, android.R.layout.simple_spinner_item, descripciones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPomodoros.adapter = adapter
            btnStart.isEnabled = listaPomodoros.isNotEmpty()
        }
    }

    private fun iniciarTemporizador(p: Pomodoro) {
        detenerTemporizador()
        btnStart.isEnabled = false
        btnStop.isEnabled = true
        btnPause.visibility = View.VISIBLE
        btnResume.visibility = View.GONE

        timer = PomodoroTemporizador(
            context = this,
            tiempoTrabajo = p.tiempo_trabajo,
            tiempoDescanso = p.tiempo_descanso,
            tiempoPausaLarga = p.tiempo_pausa_larga,
            ciclos = p.repetir_pomodoro,
            onTick = { millis ->
                handler.post {
                    val min = (millis / 1000) / 60
                    val sec = (millis / 1000) % 60
                    tvTimer.text = String.format("%02d:%02d", min, sec)
                }
            },
            onFinish = {
                handler.post {
                    Toast.makeText(this, "Pomodoro completado", Toast.LENGTH_SHORT).show()
                    tvTimer.text = "00:00"
                    btnStart.isEnabled = true
                    btnStop.isEnabled = false
                    btnPause.visibility = View.GONE
                    btnResume.visibility = View.GONE
                }
            }
        )
        timer?.iniciar()
    }

    private fun detenerTemporizador() {
        timer?.detener()
        btnStart.isEnabled = true
        btnStop.isEnabled = false
        btnPause.visibility = View.GONE
        btnResume.visibility = View.GONE
    }
}
