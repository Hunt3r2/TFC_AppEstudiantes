package com.example.proyectoappfinanzas

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class PomodoroActivity : AppCompatActivity() {
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    private var isRunning = false
    private var isBreakTime = false // Nuevo flag para indicar si es tiempo de descanso
    private var timeLeftInMillis: Long = 25 * 60 * 1000  // 25 minutos en milisegundos
    private var timer: Timer? = null
    private val handler = android.os.Handler()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        tvTimer = findViewById(R.id.tv_timer)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            if (!isRunning) {
                startPomodoro()
            }
        }

        btnStop.setOnClickListener {
            stopPomodoro()
        }

        updateTimerText()
    }

    private fun startPomodoro() {
        isRunning = true
        btnStart.isEnabled = false
        btnStop.isEnabled = true

        // Si no es tiempo de descanso, empieza el cronómetro de trabajo
        if (!isBreakTime) {
            timeLeftInMillis = 25 * 60 * 1000 // 25 minutos
        } else {
            timeLeftInMillis = 5 * 60 * 1000 // 5 minutos de descanso
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (timeLeftInMillis > 0) {
                    timeLeftInMillis -= 1000
                    handler.post {
                        updateTimerText()
                    }
                } else {
                    if (!isBreakTime) {
                        // Cuando termine el tiempo de trabajo, inicia el descanso
                        startBreak()
                    } else {
                        // Cuando termine el descanso, reinicia el ciclo de trabajo
                        startPomodoro()
                    }
                }
            }
        }, 0, 1000)  // 1 segundo
    }

    private fun startBreak() {
        isBreakTime = true
        timeLeftInMillis = 5 * 60 * 1000 // 5 minutos de descanso
        handler.post {
            updateTimerText()
        }
        btnStart.isEnabled = true  // Habilitar el botón de inicio para el siguiente ciclo
    }

    private fun stopPomodoro() {
        isRunning = false
        isBreakTime = false
        btnStart.isEnabled = true
        btnStop.isEnabled = false
        timer?.cancel()
        timer = null
        timeLeftInMillis = 25 * 60 * 1000 // Reseteamos el tiempo a 25 minutos
        updateTimerText()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }
}
