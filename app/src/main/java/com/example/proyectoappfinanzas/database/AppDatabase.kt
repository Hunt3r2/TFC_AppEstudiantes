package com.example.proyectoappfinanzas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectoappfinanzas.modelos.Ingreso
import com.example.proyectoappfinanzas.modelos.Gasto

@Database(entities = [Ingreso::class, Gasto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingresoDao(): IngresoDao
    abstract fun gastoDao(): GastoDao

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
