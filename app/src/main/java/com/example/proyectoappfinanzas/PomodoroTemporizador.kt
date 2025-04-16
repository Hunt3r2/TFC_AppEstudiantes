package com.example.proyectoappfinanzas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

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
    private var tiempoRestante: Int = 0
    private var job: Job? = null
    private var pausado = false

    private val preferencias = context.getSharedPreferences("pomodoro_settings", Context.MODE_PRIVATE)
    private val sonidoActivado = preferencias.getBoolean("sonido", true)
    private val vibracionActivada = preferencias.getBoolean("vibracion", true)

    fun iniciar() {
        cicloActual = 0
        enTrabajo = true
        iniciarCiclo()
    }

    private fun iniciarCiclo() {
        if (cicloActual >= ciclos) {
            onFinish()
            notificar("Pomodoro terminado", "Todos los ciclos completados")
            return
        }

        val duracion = if (enTrabajo) tiempoTrabajo else if ((cicloActual + 1) % 4 == 0) tiempoPausaLarga else tiempoDescanso
        if (duracion <= 0) {
            notificar("Error de configuración", "Duración no puede ser cero o negativa")
            onFinish()
            return
        }

        tiempoRestante = duracion * 60

        job = CoroutineScope(Dispatchers.Main).launch {
            while (tiempoRestante > 0 && !pausado) {
                delay(1000L)
                tiempoRestante--
                onTick(tiempoRestante * 1000L)
            }

            if (tiempoRestante == 0) {
                notificar(
                    if (enTrabajo) "Trabajo terminado" else "Descanso terminado",
                    if (enTrabajo) "Hora de descansar" else "Hora de trabajar"
                )
                reproducirSonido()
                vibrar()

                if (!enTrabajo) cicloActual++
                enTrabajo = !enTrabajo
                iniciarCiclo()
            }
        }
    }

    fun pausar() {
        pausado = true
        job?.cancel()
    }

    fun reanudar() {
        pausado = false
        job = CoroutineScope(Dispatchers.Main).launch {
            while (tiempoRestante > 0 && !pausado) {
                delay(1000L)
                tiempoRestante--
                onTick(tiempoRestante * 1000L)
            }

            if (tiempoRestante == 0) {
                notificar(
                    if (enTrabajo) "Trabajo terminado" else "Descanso terminado",
                    if (enTrabajo) "Hora de descansar" else "Hora de trabajar"
                )
                reproducirSonido()
                vibrar()

                if (!enTrabajo) cicloActual++
                enTrabajo = !enTrabajo
                iniciarCiclo()
            }
        }
    }

    fun detener() {
        job?.cancel()
    }

    private fun notificar(titulo: String, mensaje: String) {
        val channelId = "pomodoro_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pomodoro Notifications", NotificationManager.IMPORTANCE_DEFAULT)
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

    private fun vibrar() {
        if (!vibracionActivada) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun reproducirSonido() {
        if (!sonidoActivado) return
        val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}

