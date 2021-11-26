package com.yourapp.mathsalarm.ui.listAlarm

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.database.Alarm
import com.yourapp.mathsalarm.databinding.FragmentListAlarmBinding

class ListAlarmFragment : Fragment(), OnToggleAlarmListener {
    private lateinit var binding : FragmentListAlarmBinding
    private lateinit var viewModel : ListAlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(!Settings.canDrawOverlays(requireContext())){
                requestPermission()
            }
        }

        return inflater.inflate(R.layout.fragment_list_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListAlarmBinding.bind(view)

        viewModel = ViewModelProviders.of(this, ListAlarmViewModelFactory(requireActivity().application))
            .get(ListAlarmViewModel::class.java)
        val alarmRecyclerViewAdapter = AlarmRecyclerViewAdapter(this)

        viewModel.getAlarmsLiveData().observe(viewLifecycleOwner, { alarms ->
            alarmRecyclerViewAdapter.setAlarms(alarms)
        })

        val recyclerView = binding.recylerView
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = alarmRecyclerViewAdapter

        binding.addAlarm.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_listAlarmFragment_to_createAlarmFragment)
        }
    }

    override fun onToggle(alarm: Alarm) {
        if(alarm.started)
            alarm.cancelAlarm(requireContext())
        else
            alarm.schedule(requireContext())
        viewModel.update(alarm)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity?.packageName))
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}