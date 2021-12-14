package com.yourapp.mathsalarm.ui.listAlarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.databinding.AlarmCardViewBinding

class AlarmRecyclerViewAdapter(private val listener: OnToggleAlarmListener,
private val itemClick : (alarm: Alarm) -> Unit) :
    RecyclerView.Adapter<AlarmViewHolder>() {
    private var alarms : List<Alarm> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = AlarmCardViewBinding.inflate(view, parent, false)
        return AlarmViewHolder(binding, listener, itemClick)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount() = alarms.size

    fun setAlarms(_alarms : List<Alarm>){
        alarms = _alarms
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: AlarmViewHolder) {
        super.onViewRecycled(holder)
        holder.alarmStarted.setOnCheckedChangeListener(null)
    }
}