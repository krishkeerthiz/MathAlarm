package com.yourapp.mathsalarm.database

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.FRIDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.MONDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.RECURRING
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.SATURDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.SUNDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.THURSDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TUESDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.WEDNESDAY
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.ALARMID
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import java.io.Serializable
import java.util.*

@Entity(tableName = "alarm_table")
class Alarm(
    @PrimaryKey
    @NonNull
    private var alarmId: Int,
    private var hour: Int,
    private var minute: Int,
    private var title : String,
    private var created: Long,
    var started: Boolean,
    private var recurring: Boolean,
    private var monday: Boolean,
    private var tuesday: Boolean,
    private var wednesday: Boolean,
    private var thursday: Boolean,
    private var friday: Boolean,
    private var saturday: Boolean,
    private var sunday: Boolean,
) : Serializable {

    fun schedule(context : Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Broadcast receiver
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(RECURRING, recurring)
        intent.putExtra(MONDAY, monday)
        intent.putExtra(TUESDAY, tuesday)
        intent.putExtra(WEDNESDAY, wednesday)
        intent.putExtra(THURSDAY, thursday)
        intent.putExtra(FRIDAY, friday)
        intent.putExtra(SATURDAY, saturday)
        intent.putExtra(SUNDAY, sunday)
        intent.putExtra(ALARMID, alarmId)
        intent.putExtra(TITLE, title)

        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(calendar.timeInMillis < System.currentTimeMillis())
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)

        val timeDifference = calendar.timeInMillis - System.currentTimeMillis()

        Toast.makeText(context, calculateHoursAndMinutes(timeDifference), Toast.LENGTH_SHORT).show()

        // Set alarm once
        if(!recurring){
//            val toastMessage = "Alarm set at ${formatHour(hour)} : $minute ${getMeridian()}"
//            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeDifference,
                pendingIntent
            )
        }
        // Set repeating alarm
        else{
//            val toastMessage = "Repeated Alarm set at ${formatHour(hour)} : $minute ${getMeridian()} on ${getRecurringDays()}"
//            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            val runDaily = 24.toLong()*60*60*1000
            alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + timeDifference,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        started = true
    }

    fun calculateHoursAndMinutes(millis : Long) : String{
        val hour = (millis/(1000*60*60))
        val minutes = (millis/(1000*60))%60 + 1

        val timeString = "$hour hours and $minutes minutes left"
        return timeString
    }

    fun cancelAlarm(context : Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(pendingIntent)
        started = false
//        Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show()
    }

     fun getRecurringDays() : String?{
        if (!recurring) {
            return null
        }

        var days = ""
        if (monday) {
            days += "Mo "
        }
        if (tuesday) {
            days += "Tu "
        }
        if (wednesday) {
            days += "We "
        }
        if (thursday) {
            days += "Th "
        }
        if (friday) {
            days += "Fr "
        }
        if (saturday) {
            days += "Sa "
        }
        if (sunday) {
            days += "Su "
        }

        return days
    }

    fun getAlarmId() = alarmId
    fun getTitle() = title
    fun getHour() = hour
    fun getMinute() = minute
    fun isRecurring() = recurring
    fun getCreated() = created
    fun getMonday() = monday
    fun getTuesday() = tuesday
    fun getWednesday() = wednesday
    fun getThursday() = thursday
    fun getFriday() = friday
    fun getSaturday() = saturday
    fun getSunday() = sunday
    fun getMeridian() : String{
        return if(hour >= 12) "PM"
        else
            "AM"
    }

    private fun formatHour(hour : Int) : Int{
        return when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
    }
}