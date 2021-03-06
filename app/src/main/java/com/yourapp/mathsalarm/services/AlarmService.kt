package com.yourapp.mathsalarm.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.yourapp.mathsalarm.BuildConfig
import com.yourapp.mathsalarm.R
import com.yourapp.mathsalarm.activities.RingActivity
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.ALARMID
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.RECURRING
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import com.yourapp.mathsalarm.database.AlarmRepository

class AlarmService : Service() {
    private lateinit var vibrator: Vibrator
    private lateinit var ringtone: MediaPlayer
    private lateinit var notificationManager: NotificationManager

    private val PRIMARY_CHANNEL_ID: String = BuildConfig.APPLICATION_ID + "PRIMARY_CHANNEL_ID"
    private val NOTIFICATION_ID = 0

    private val TAG :String = "AlarmService"

    override fun onCreate() {
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if(alarmUri == null)
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        ringtone = MediaPlayer.create(this, alarmUri)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ringIntent = Intent(this, RingActivity::class.java)
        val id = intent?.getIntExtra(ALARMID, 1)!!
        val title = intent?.getStringExtra(TITLE)
        val recurring = intent?.getBooleanExtra(RECURRING, true)

        //Turn off alarm after rang
        setAlarmCheckedOff(id, recurring)

        ringNVibrate()

        createNotificationChannel()

        ringIntent.putExtra(TITLE, title)
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val notification = getNotification(ringIntent, title)
        notificationManager.notify(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
        vibrator.cancel()
        notificationManager.cancel(NOTIFICATION_ID)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun setAlarmCheckedOff(id: Int, recurring : Boolean) {
        if(!recurring){
            val alarmRepository = AlarmRepository(application)
            alarmRepository.stopAlarm(id)
        }
    }

    private fun ringNVibrate() {
        val pattern = longArrayOf(0, 100, 1000)
        vibrator.vibrate(pattern, 0)
        ringtone.isLooping = true
        ringtone.start()
    }

    private fun createNotificationChannel(){
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Alarm Notification",
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Notification from Math Alarm"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotification(intent : Intent, title : String?): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var message = "Make it happen :)"
        if(title != "")
            message = title!!
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("Hey, It's Time")
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.alarm_notification))
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .build()
    }


}