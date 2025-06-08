package com.example.proyectoappfinanzas

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.proyectoappfinanzas.dao.FlashcardsDao
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Flashcard
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FlashcardsDaoTest {

    private lateinit var db: AppBD
    private lateinit var dao: FlashcardsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppBD::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.flashcardDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertarYLeerFlashcard() = runTest {
        val flashcard = Flashcard(
            pregunta = "¿Capital de España?",
            respuesta = "Madrid",
            categoria = "Geografía",
            estado = "Nueva"
        )

        dao.insertar(flashcard)
        val resultado = dao.obtenerTodas()

        assertEquals(1, resultado.size)
        assertEquals("Madrid", resultado[0].respuesta)
    }
}
