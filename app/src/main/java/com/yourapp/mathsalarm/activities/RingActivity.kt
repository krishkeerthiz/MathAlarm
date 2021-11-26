package com.yourapp.mathsalarm.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import com.yourapp.mathsalarm.databinding.ActivityRingBinding
import com.yourapp.mathsalarm.services.AlarmService
import com.yourapp.mathsalarm.util.MathProblem
import java.util.*

class RingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        binding = ActivityRingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Generate math problem
        val mathProblem = MathProblem()
        val resultText = binding.resultTextNumber

        //Show title
        val title = intent.getStringExtra(TITLE)
        if(title != "")
            binding.titleTextView.text = title

        //To display in lock screen
        setFullScreen()

        binding.operand1.text = mathProblem.getOperand1().toString()
        binding.operand2.text = mathProblem.getOperand2().toString()
        binding.operator.text = mathProblem.getOperator()
        binding.timeTextView.text = getCurrentTime()

        //Result listener
        val listener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                binding.alarmStopButton.isEnabled = resultText.text.toString().isNotBlank()
            }
        }
        resultText.addTextChangedListener(listener)

        //Stop button
        binding.alarmStopButton.setOnClickListener {
            val enteredValue = resultText.text.toString().toInt()
            if(enteredValue == mathProblem.getResult()){
                Toast.makeText(this, getString(R.string.right_answer), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AlarmService::class.java)
                this.stopService(intent)
                finish()
            }
        }

        animateClock()
    }

    private fun setFullScreen() {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = calendar.get(Calendar.AM_PM)
        val meridian = if(amPm == Calendar.AM)
            "AM"
        else
            "PM"
        if(hour == 0)
            hour = 12
        val minuteString = if(minute < 10) "0$minute" else "$minute"
        return "$hour : $minuteString $meridian"
    }

    private fun animateClock() {
        val rotateAnimation = ObjectAnimator.ofFloat(binding.clockImageView, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }
}