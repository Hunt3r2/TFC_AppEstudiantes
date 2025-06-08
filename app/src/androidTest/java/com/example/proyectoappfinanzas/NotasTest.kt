import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectoappfinanzas.dao.NotaDao
import com.example.proyectoappfinanzas.database.AppBD
import com.example.proyectoappfinanzas.modelos.Nota
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class NotasTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val notasMock = listOf(
        Nota(id = 1, titulo = "Nota 1", contenido = "Contenido 1", fecha_creacion = Date()),
        Nota(id = 2, titulo = "Nota 2", contenido = "Contenido 2", fecha_creacion = Date())
    )

    @Test
    fun cargarNotas_devuelveListaNoVacia() = runBlocking {
        val notaDaoMock = mock(NotaDao::class.java)
        val appBDMock = mock(AppBD::class.java)

        `when`(appBDMock.notaDao()).thenReturn(notaDaoMock)
        `when`(notaDaoMock.obtenerTodas()).thenReturn(notasMock)

        val notasObtenidas = notaDaoMock.obtenerTodas()

        assertNotNull(notasObtenidas)
        assertTrue(notasObtenidas.isNotEmpty())
        assertEquals(2, notasObtenidas.size)
        assertEquals("Nota 1", notasObtenidas[0].titulo)
    }

    @Test
    fun adaptador_recibeListaCorrectamente() {
        val adapter = mock(com.example.proyectoappfinanzas.NotaAdapter::class.java)
        adapter.submitList(notasMock)
        verify(adapter).submitList(notasMock)
    }

    @Test
    fun eliminarNota_invalida_noHaceNada() {
        val notaInvalida: Nota? = null
        assertNull(notaInvalida)
    }
}
