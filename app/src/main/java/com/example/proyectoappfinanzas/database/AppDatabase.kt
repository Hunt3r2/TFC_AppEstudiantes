package com.example.proyectoappfinanzas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectoappfinanzas.Convertidores
import com.example.proyectoappfinanzas.dao.GastoDao
import com.example.proyectoappfinanzas.dao.IngresoDao
import com.example.proyectoappfinanzas.dao.NotaDao
import com.example.proyectoappfinanzas.dao.PomodoroDao
import com.example.proyectoappfinanzas.modelos.Ingreso
import com.example.proyectoappfinanzas.modelos.Gasto
import com.example.proyectoappfinanzas.modelos.Nota
import com.example.proyectoappfinanzas.modelos.Pomodoro

@Database(
    entities = [Ingreso::class, Gasto::class, Nota::class, Pomodoro::class],
    version = 1
)
@TypeConverters(Convertidores::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingresoDao(): IngresoDao
    abstract fun gastoDao(): GastoDao
    abstract fun notaDao(): NotaDao
    abstract fun pomodoroDao(): PomodoroDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kakebo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
