package com.yourapp.mathsalarm.ui.listAlarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.database.AlarmRepository

class ListAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository : AlarmRepository = AlarmRepository(application)
    private val alarmsLiveData : LiveData<List<Alarm>> = alarmRepository.getAlarmsLiveData()

    fun update(alarm : Alarm){
        alarmRepository.update(alarm)
    }

    fun getAlarmsLiveData() = alarmsLiveData
}