package com.example.proyectoappfinanzas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectoappfinanzas.dao.GastoDao
import com.example.proyectoappfinanzas.dao.IngresoDao
import com.example.proyectoappfinanzas.dao.NotaDao
import com.example.proyectoappfinanzas.dao.PomodoroDao
import com.example.proyectoappfinanzas.modelos.Ingreso
import com.example.proyectoappfinanzas.modelos.Gasto

@Database(entities = [Ingreso::class, Gasto::class], version = 1)
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
                    "GestorDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
