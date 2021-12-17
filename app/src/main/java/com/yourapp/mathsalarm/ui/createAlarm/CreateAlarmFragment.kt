package com.yourapp.mathsalarm.ui.createAlarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.databinding.FragmentCreateAlarmBinding
import kotlinx.android.synthetic.main.fragment_create_alarm.view.*
import java.util.*

class CreateAlarmFragment : Fragment() {
    private lateinit var binding : FragmentCreateAlarmBinding
    private lateinit var viewModel : CreateAlarmViewModel
    val args : CreateAlarmFragmentArgs by navArgs()
    private var alarmArg : Alarm? = null

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

        if(args.alarm != null){
            alarmArg = args.alarm!!
            setView()
        }

        binding.recurringCheckbox.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            if(isChecked)
                binding.recurringOptions.visibility = View.VISIBLE
            else
                binding.recurringOptions.visibility = View.GONE
        }

        binding.scheduleAlarm.setOnClickListener {
            scheduleAlarm()
            navigate(it)
        }

        binding.trash.setOnClickListener {
            viewModel.delete(alarmArg!!)
            navigate(it)
        }

    }

    private fun setView(){
        binding.trash.visibility = View.VISIBLE

        binding.timePicker.currentHour = alarmArg!!.getHour()
        binding.timePicker.currentMinute = alarmArg!!.getMinute()

        binding.title.setText(alarmArg!!.getTitle())

        if(alarmArg!!.isRecurring()){
            binding.recurringCheckbox.isChecked = true
            binding.recurringOptions.visibility = View.VISIBLE
            binding.recurringOptions.check_monday.isChecked = alarmArg!!.getMonday()
            binding.recurringOptions.check_tuesday.isChecked = alarmArg!!.getTuesday()
            binding.recurringOptions.check_wednesday.isChecked = alarmArg!!.getWednesday()
            binding.recurringOptions.check_thursday.isChecked = alarmArg!!.getThursday()
            binding.recurringOptions.check_friday.isChecked = alarmArg!!.getFriday()
            binding.recurringOptions.check_saturday.isChecked = alarmArg!!.getSaturday()
            binding.recurringOptions.check_sunday.isChecked = alarmArg!!.getSunday()
        }

    }

    private fun navigate(view: View)
    = Navigation.findNavController(view).navigate(R.id.action_createAlarmFragment_to_listAlarmFragment)

    private fun scheduleAlarm(){
        if(alarmArg != null) {
            if(alarmArg!!.started)
            alarmArg!!.cancelAlarm(requireContext())
            viewModel.delete(alarmArg!!)
        }

        val alarm = getAlarm()
        viewModel.insert(alarm)

        alarm.schedule(requireContext())
    }

    private fun getAlarm(alarmId : Int = Random().nextInt(Integer.MAX_VALUE)) : Alarm{
        return Alarm(
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
    }
}