package com.example.proyectoappfinanzas

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Ingreso
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class KakeboTest {

    private lateinit var db: AppBD

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppBD::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertarYLeerIngresoPorMesYAnio() = runTest {
        val ingreso = Ingreso(
            monto = 1000.0,
            descripcion = "Sueldo",
            categoria = "Salario",
            mes = 6,
            anio = 2025,
            dia = 3
        )

        db.ingresoDao().agregarIngreso(ingreso)
        val ingresos = db.ingresoDao().obtenerIngresosPorMesYAnio(6, 2025)

        assertEquals(1, ingresos.size)
        assertEquals("Sueldo", ingresos[0].descripcion)
        assertEquals(1000.0, ingresos[0].monto, 0.01)
    }
}
