package com.yourapp.mathsalarm.broadcastReceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.yourapp.mathsalarm.services.AlarmService
import com.yourapp.mathsalarm.services.RescheduleService
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG : String = "AlarmBroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {

        //Restart services on reboot
        if(Intent.ACTION_BOOT_COMPLETED == intent.action){
            startRescheduleAlarmsService(context,intent)
        }
        else{
            if(!intent.getBooleanExtra(RECURRING, false))
                startAlarmService(context, intent, false)
            else{
                if(alarmsIntent(intent))
                    startAlarmService(context, intent, true)
            }
        }
    }

    private fun alarmsIntent(intent: Intent) : Boolean{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        return when(calendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY ->{
                 intent.getBooleanExtra(MONDAY, false)
            }
            Calendar.TUESDAY ->{
                intent.getBooleanExtra(TUESDAY, false)
            }
            Calendar.WEDNESDAY ->{
                intent.getBooleanExtra(WEDNESDAY, false)
            }
            Calendar.THURSDAY ->{
                intent.getBooleanExtra(THURSDAY, false)
            }
            Calendar.FRIDAY ->{
                intent.getBooleanExtra(FRIDAY, false)
            }
            Calendar.SATURDAY ->{
                intent.getBooleanExtra(SATURDAY, false)
            }
            Calendar.SUNDAY ->{
                intent.getBooleanExtra(SUNDAY, false)
            }
            else -> false
        }
    }

    private fun startAlarmService(context: Context, intent : Intent, recurring : Boolean){
        val serviceIntent = Intent(context, AlarmService::class.java)
        val alarmId = intent.getIntExtra(ALARMID, 0)
        serviceIntent.putExtra(ALARMID, alarmId)
        serviceIntent.putExtra(TITLE, intent.getStringExtra(TITLE))
        serviceIntent.putExtra(RECURRING, recurring)

        startServiceIntent(context, serviceIntent)
    }

    private fun startRescheduleAlarmsService(context: Context, @Suppress("UNUSED_PARAMETER")intent : Intent) {
        val rescheduleIntent = Intent(context, RescheduleService::class.java)

        startServiceIntent(context, rescheduleIntent)
    }

    private fun startServiceIntent(context : Context, intent : Intent){
        context.startService(intent)
        Log.d(TAG, "Service started")
    }

    companion object{
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val TITLE = "TITLE"
        const val ALARMID = "ALARMID"
    }
}