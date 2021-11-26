package com.yourapp.mathsalarm.ui.listAlarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListAlarmViewModelFactory(val application : Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListAlarmViewModel(application) as T
    }
}