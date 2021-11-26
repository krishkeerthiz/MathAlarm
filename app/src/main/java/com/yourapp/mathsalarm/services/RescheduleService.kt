package com.yourapp.mathsalarm.services

import androidx.lifecycle.LifecycleService
import android.content.Intent
import android.os.IBinder
import com.yourapp.mathsalarm.database.AlarmRepository

class RescheduleService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmRepository = AlarmRepository(application)

        alarmRepository.getAlarmsLiveData().observeForever { alarms ->
            for(alarm in alarms){
                if(alarm.started)
                    alarm.schedule(applicationContext)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}