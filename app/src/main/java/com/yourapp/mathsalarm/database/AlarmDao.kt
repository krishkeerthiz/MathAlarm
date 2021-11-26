package com.yourapp.mathsalarm.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {
    @Insert
    fun insert(alarm : Alarm)

    @Query("delete from alarm_table")
    fun deleteAll()

    @Query("select * from alarm_table order by created asc")
    fun getAlarms() : LiveData<List<Alarm>>

    @Query("select * from alarm_table where alarmId == :alarmid")
    fun getAlarm(alarmid : Int) : Alarm

    @Update
    fun update(alarm : Alarm)
}