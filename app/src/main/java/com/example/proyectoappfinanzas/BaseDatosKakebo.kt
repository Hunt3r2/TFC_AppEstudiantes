import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.proyectoappfinanzas.Modelos
import com.example.proyectoappfinanzas.R

class BaseDatosKakebo(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kakebo.db"
        private const val DATABASE_VERSION = 3
        const val TABLE_INGRESOS = "ingresos"
        const val TABLE_GASTOS = "gastos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createIngresosTable = """
        CREATE TABLE $TABLE_INGRESOS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            monto REAL,
            descripcion TEXT,
            categoria TEXT,
            mes INTEGER,
            anio INTEGER 
        )
    """.trimIndent()
        val createGastosTable = """
        CREATE TABLE $TABLE_GASTOS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            monto REAL,
            descripcion TEXT,
            categoria TEXT,
            mes INTEGER,
            anio INTEGER 
        )
    """.trimIndent()
        db.execSQL(createIngresosTable)
        db.execSQL(createGastosTable)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {  // Asegúrate de verificar la versión correcta
            // Agregar columna 'mes' a la tabla 'ingresos'
            db.execSQL("ALTER TABLE ingresos ADD COLUMN mes INTEGER")

            // Agregar columna 'mes' a la tabla 'gastos'
            db.execSQL("ALTER TABLE gastos ADD COLUMN mes INTEGER")
        }
    }


    fun agregarIngreso(monto: Double, descripcion: String, categoria: String, mes: Int, anio: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monto", monto)
            put("mes", mes)
            put("anio", anio)
            put("descripcion", descripcion)
            put("categoria", categoria)
        }
        db.insert(TABLE_INGRESOS, null, values)
    }

    @SuppressLint("Range")
    fun obtenerIngresosPorMesYAnio(mes: Int, anio: Int): List<Modelos.Ingreso> {
        val ingresos = mutableListOf<Modelos.Ingreso>()
        val db = this.readableDatabase

        // Query para obtener ingresos de un mes y año específico
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_INGRESOS WHERE mes = ? AND anio = ?", arrayOf(mes.toString(), anio.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val monto = cursor.getDouble(cursor.getColumnIndex("monto"))
                val descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                val categoria = cursor.getString(cursor.getColumnIndex("categoria"))
                val mesGuardado = cursor.getInt(cursor.getColumnIndexOrThrow("mes"))
                val anioGuardado = cursor.getInt(cursor.getColumnIndexOrThrow("anio"))  // Aseguramos que estamos obteniendo el año

                // Agregar el ingreso a la lista
                ingresos.add(Modelos.Ingreso(id, monto, descripcion, categoria, mesGuardado, anioGuardado))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return ingresos
    }


    fun borrarIngreso(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_INGRESOS, "id = ?", arrayOf(id.toString()))

    }

    fun agregarGasto(monto: Double, descripcion: String, categoria: String, mes: Int, anio: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monto", monto)
            put("descripcion", descripcion)
            put("categoria", categoria)
            put("mes", mes)
            put("anio", anio)
        }
        db.insert(TABLE_GASTOS, null, values)
    }

    @SuppressLint("Range")
    fun obtenerGastosPorMesYAnio(mes: Int, anio: Int): List<Modelos.Gasto> {
        val listaGastos = mutableListOf<Modelos.Gasto>()
        val db = this.readableDatabase

        // Consulta SQL para obtener los gastos filtrados por mes y año
        val cursor = db.rawQuery(
            "SELECT * FROM gastos WHERE mes = ? AND anio = ?",
            arrayOf(mes.toString(), anio.toString())  // Se pasan los valores mes y año como parámetros
        )

        if (cursor.moveToFirst()) {
            do {
                // Crear el objeto Gasto con los valores obtenidos del cursor
                val gasto = Modelos.Gasto(
                    id = cursor.getInt(cursor.getColumnIndex("id")),
                    monto = cursor.getDouble(cursor.getColumnIndex("monto")),
                    descripcion = cursor.getString(cursor.getColumnIndex("descripcion")),
                    categoria = cursor.getString(cursor.getColumnIndex("categoria")),
                    mes = cursor.getInt(cursor.getColumnIndex("mes")),
                    anio = cursor.getInt(cursor.getColumnIndex("anio"))
                )
                // Añadir el gasto a la lista
                listaGastos.add(gasto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listaGastos
    }



    fun borrarGasto(id: Int) {
        val db = this.writableDatabase
        db.delete("gastos", "id = ?", arrayOf(id.toString()))

    }
}