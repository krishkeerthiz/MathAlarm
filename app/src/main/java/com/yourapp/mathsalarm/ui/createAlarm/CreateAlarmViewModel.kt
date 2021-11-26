package com.yourapp.mathsalarm.ui.createAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.database.AlarmRepository

class CreateAlarmViewModel(application  :Application) : ViewModel() {
    private var alarmRepository : AlarmRepository = AlarmRepository(application)

    fun insert(alarm : Alarm){
        alarmRepository.insert(alarm)
    }

}