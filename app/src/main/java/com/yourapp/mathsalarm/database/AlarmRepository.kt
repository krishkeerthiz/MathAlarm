package com.yourapp.mathsalarm.database

import android.app.Application
import androidx.lifecycle.LiveData

class AlarmRepository(val application: Application) {
    private var alarmDao : AlarmDao
    private var alarmsLiveData : LiveData<List<Alarm>>

    init {
        val alarmDb = AlarmDatabase.getDatabase(application)
        alarmDao = alarmDb!!.getAlarmDao()
        alarmsLiveData = alarmDao.getAlarms()
    }

     fun stopAlarm(alarmId : Int) {
        AlarmDatabase.databaseWriteExecutor.execute{
            val alarm = alarmDao.getAlarm(alarmId)
            alarm.started = false
            alarmDao.update(alarm)
        }
    }

    fun insert(alarm : Alarm){
        AlarmDatabase.databaseWriteExecutor.execute {
            alarmDao.insert(alarm)
        }

    }

    fun update(alarm: Alarm){
        AlarmDatabase.databaseWriteExecutor.execute {
            alarmDao.update(alarm)
        }
    }

    fun getAlarmsLiveData() = alarmsLiveData

    fun getAlarm(alarmId : Int) = alarmDao.getAlarm(alarmId)

    fun delete(alarm: Alarm){
        AlarmDatabase.databaseWriteExecutor.execute{
            alarmDao.delete(alarm)
        }
    }
}