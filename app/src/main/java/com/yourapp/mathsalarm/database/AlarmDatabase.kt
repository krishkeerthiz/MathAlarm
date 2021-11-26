package com.yourapp.mathsalarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.concurrent.Executors

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun getAlarmDao() : AlarmDao

    companion object{
        private const val TAG = "AlarmDatabase"
        const val VERSION = 1
        private const val DATABASE_NAME = "alarm_database.db"

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        private var instance : AlarmDatabase? = null

        fun getInstance(context : Context) : AlarmDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also{ instance = it}
            }

        private fun buildDatabase(context : Context) : AlarmDatabase{
            val filesDir = context.getExternalFilesDir(null)
            val dataDir = File(filesDir, "data")

            if(!dataDir.exists())
                dataDir.mkdir()

            val builder = Room.databaseBuilder(
                context,
                AlarmDatabase::class.java,
                File(dataDir, DATABASE_NAME).toString()
            ).fallbackToDestructiveMigration()

            return builder.build()
        }

        fun getDatabase(context: Context): AlarmDatabase? {
            if (instance == null) {
                synchronized(AlarmDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AlarmDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return instance
        }
    }
}