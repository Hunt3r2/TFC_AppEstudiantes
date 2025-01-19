import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectoappfinanzas.Modelos

class BaseDatosKakebo(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kakebo.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_INGRESOS = "ingresos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createIngresosTable = """
            CREATE TABLE $TABLE_INGRESOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                monto REAL,
                descripcion TEXT,
                categoria TEXT
            )
        """.trimIndent()
        db.execSQL(createIngresosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INGRESOS")
        onCreate(db)
    }

    fun agregarIngreso(monto: Double, descripcion: String, categoria: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monto", monto)
            put("descripcion", descripcion)
            put("categoria", categoria)
        }
        db.insert(TABLE_INGRESOS, null, values)
        db.close()
    }

    fun obtenerIngresos(): List<Modelos.Ingreso> {
        val ingresos = mutableListOf<Modelos.Ingreso>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_INGRESOS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val monto = cursor.getDouble(cursor.getColumnIndex("monto"))
                val descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                val categoria = cursor.getString(cursor.getColumnIndex("categoria"))
                ingresos.add(Modelos.Ingreso(id, monto, descripcion, categoria))
            } while (cursor.moveToNext())
        }

        cursor.close() // Cerrar el cursor para liberar recursos
        db.close() // Cerrar la base de datos
        return ingresos
    }

    fun borrarIngreso(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_INGRESOS, "id = ?", arrayOf(id.toString()))
        db.close()
    }
}