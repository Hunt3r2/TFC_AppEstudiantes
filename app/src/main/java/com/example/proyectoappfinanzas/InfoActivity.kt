package com.example.proyectoappfinanzas

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoappfinanzas.KakeboActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volverAtras: FloatingActionButton = findViewById(R.id.volver_atras)

        volverAtras.setOnClickListener {
            val intent = Intent(this, KakeboActivity::class.java)
            startActivity(intent)
        }

    }
}