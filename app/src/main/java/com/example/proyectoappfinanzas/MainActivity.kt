package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonKakebo: Button = findViewById(R.id.boton_kakebo)

        // Configura el listener para el botón
        buttonKakebo.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, KakeboActivity::class.java)
            startActivity(intent) // Inicia la nueva actividad
        }
    }
}