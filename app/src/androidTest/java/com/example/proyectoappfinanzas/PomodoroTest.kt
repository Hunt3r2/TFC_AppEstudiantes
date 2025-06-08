import org.junit.Assert
import org.junit.Test

class PomodoroTest {

    @Test
    fun calcula_duracion_correcta_trabajo() {
        val enTrabajo = true
        val duracion = if (enTrabajo) 25 else 5
        Assert.assertEquals(25, duracion)
    }

    @Test
    fun verifica_duracion_invalida() {
        val tiempoTrabajo = -5
        val valido = tiempoTrabajo > 0
        Assert.assertFalse(valido)
    }
}
