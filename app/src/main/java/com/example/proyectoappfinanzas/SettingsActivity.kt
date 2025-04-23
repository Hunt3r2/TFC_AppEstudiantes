package com.example.proyectoappfinanzas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchSonido: Switch
    private lateinit var switchVibracion: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchSonido = findViewById(R.id.switchSonido)
        switchVibracion = findViewById(R.id.switchVibracion)

        val prefs = getSharedPreferences("pomodoro_settings", Context.MODE_PRIVATE)

        switchSonido.isChecked = prefs.getBoolean("sonido", true)
        switchVibracion.isChecked = prefs.getBoolean("vibracion", true)

        switchSonido.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sonido", isChecked).apply()
        }

        switchVibracion.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("vibracion", isChecked).apply()
        }

        val buttonVolver: Button = findViewById(R.id.btnVolver)

        buttonVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
