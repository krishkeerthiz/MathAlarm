package com.yourapp.mathsalarm.ui.createAlarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.databinding.FragmentCreateAlarmBinding
import java.util.*

class CreateAlarmFragment : Fragment() {
    private lateinit var binding : FragmentCreateAlarmBinding
    private lateinit var viewModel : CreateAlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(this, CreateAlarmViewModelFactory(requireActivity().application))
            .get(CreateAlarmViewModel::class.java)

        return inflater.inflate(R.layout.fragment_create_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateAlarmBinding.bind(view)

        binding.recurringCheckbox.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            if(isChecked)
                binding.recurringOptions.visibility = View.VISIBLE
            else
                binding.recurringOptions.visibility = View.GONE
        }

        binding.scheduleAlarm.setOnClickListener {
            scheduleAlarm()
            Navigation.findNavController(view).navigate(R.id.action_createAlarmFragment_to_listAlarmFragment)
        }
    }

    private fun scheduleAlarm(){
        val alarmId = Random().nextInt(Integer.MAX_VALUE)

        val alarm = Alarm(
            alarmId,
            binding.timePicker.currentHour,
            binding.timePicker.currentMinute,
            binding.title.text.toString().capitalize(Locale.ROOT),
            System.currentTimeMillis(),
            true,
            binding.recurringCheckbox.isChecked,
            binding.checkMonday.isChecked,
            binding.checkTuesday.isChecked,
            binding.checkWednesday.isChecked,
            binding.checkThursday.isChecked,
            binding.checkFriday.isChecked,
            binding.checkSaturday.isChecked,
            binding.checkSunday.isChecked
        )

        viewModel.insert(alarm)
        alarm.schedule(requireContext())
    }
}