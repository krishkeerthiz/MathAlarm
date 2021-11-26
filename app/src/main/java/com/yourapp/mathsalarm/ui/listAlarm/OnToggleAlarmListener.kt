package com.yourapp.mathsalarm.ui.listAlarm

import com.yourapp.mathsalarm.database.Alarm

interface OnToggleAlarmListener {
    fun onToggle(alarm : Alarm)
}