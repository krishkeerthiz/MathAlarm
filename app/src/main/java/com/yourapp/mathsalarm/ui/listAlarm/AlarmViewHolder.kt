package com.yourapp.mathsalarm.ui.listAlarm

import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.databinding.AlarmCardViewBinding

class AlarmViewHolder(binding : AlarmCardViewBinding, private val listener: OnToggleAlarmListener)
    : RecyclerView.ViewHolder(binding.root) {
    private val alarmTime: TextView = binding.itemAlarmTime
    private val alarmRecurring: ImageView = binding.itemAlarmRecurring
    private val alarmRecurringDays: TextView = binding.itemAlarmRecurringDays
    private val alarmTitle: TextView = binding.itemAlarmTitle
    val alarmStarted : SwitchMaterial = binding.itemAlarmStarted

    fun bind(alarm : Alarm){
        val alarmText = String.format("%02d:%02d ${alarm.getMeridian()}", formatHour(alarm.getHour()), alarm.getMinute())
        alarmTime.text = alarmText
        alarmTitle.text = alarm.getTitle()
        alarmStarted.isChecked = alarm.started

        if(alarm.isRecurring()){
            alarmRecurring.setImageResource(R.drawable.ic_repeat_white_24dp)
            alarmRecurringDays.text = alarm.getRecurringDays()
        }
        else{
            alarmRecurring.setImageResource(android.R.color.transparent)
            alarmRecurringDays.text = ""
        }

        alarmStarted.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            listener.onToggle(alarm)
        }
    }

    private fun formatHour(hour : Int) : Int{
        return when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
    }
}