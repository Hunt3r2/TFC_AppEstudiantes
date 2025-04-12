package com.example.proyectoappfinanzas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat

class PomodoroTemporizador(
    private val context: Context,
    private val tiempoTrabajo: Int,
    private val tiempoDescanso: Int,
    private val tiempoPausaLarga: Int,
    private val ciclos: Int,
    private val onTick: (Long) -> Unit,
    private val onFinish: () -> Unit
) {
    private var cicloActual = 0
    private var enTrabajo = true
    private var timer: CountDownTimer? = null

    fun iniciar() {
        cicloActual = 0
        iniciarCiclo()
    }

    private fun iniciarCiclo() {
        if (cicloActual >= ciclos) {
            onFinish()
            mostrarNotificacion("Pomodoro terminado", "Todos los ciclos completados")
            return
        }

        val duracion = if (enTrabajo) tiempoTrabajo else if ((cicloActual + 1) % 4 == 0) tiempoPausaLarga else tiempoDescanso

        timer = object : CountDownTimer(duracion * 60_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                mostrarNotificacion(
                    if (enTrabajo) "Trabajo terminado" else "Descanso terminado",
                    if (enTrabajo) "Hora de descansar" else "Hora de trabajar"
                )
                if (!enTrabajo) cicloActual++
                enTrabajo = !enTrabajo
                iniciarCiclo()
            }
        }.start()
    }

    fun detener() {
        timer?.cancel()
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val channelId = "pomodoro_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pomodoro Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notificacion = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}
