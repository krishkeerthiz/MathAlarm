package com.yourapp.mathsalarm.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.yourapp.mathsalarm.BuildConfig
import com.yourapp.mathsalarm.activities.RingActivity
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.ALARMID
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.RECURRING
import com.yourapp.mathsalarm.broadcastReceivers.AlarmBroadcastReceiver.Companion.TITLE
import com.yourapp.mathsalarm.database.AlarmRepository

class AlarmService : Service() {
    private lateinit var vibrator: Vibrator
    private lateinit var ringtone: MediaPlayer

    override fun onCreate() {
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if(alarmUri == null)
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        ringtone = MediaPlayer.create(this, alarmUri)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ringIntent = Intent(this, RingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, ringIntent, 0)

        val id = intent?.getIntExtra(ALARMID, 1)!!
        val title = intent?.getStringExtra(TITLE)
        val recurring = intent?.getBooleanExtra(RECURRING, true)

        //Turn off alarm after rang
        setAlarmCheckedOff(id, recurring)

        ringNVibrate()

        val notification = setNotification(title, pendingIntent)
        startForeground(2, notification)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            ringIntent.putExtra(TITLE, title)
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(ringIntent)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
        vibrator.cancel()
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

    private fun setNotification(title: String?, pendingIntent: PendingIntent?): Notification {
        val notification: Notification

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val channelId: String = BuildConfig.APPLICATION_ID + "_notification_id"
                val channelName: String = BuildConfig.APPLICATION_ID + "_notification_name"
                var mChannel = notificationManager.getNotificationChannel(channelId)
                val builder = NotificationCompat.Builder(this, channelId)

                if (mChannel == null) {
                    mChannel = NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH)
                    mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                    notificationManager.createNotificationChannel(mChannel)
                }
                builder
                    .setContentTitle(title)
                    .setContentText("Ring Ring .. Ring Ring")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(openScreen(), true)
                    .setAutoCancel(true)
                    .setOngoing(true)
                notification = builder.build()

                notificationManager.notify(0, notification)
            }
            else -> {
                notification = NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText("Ring Ring .. Ring Ring")
                    .setContentIntent(pendingIntent)
                    .build()
            }
        }
        return notification
    }

    private fun openScreen(): PendingIntent? {
        val fullScreenIntent = Intent(this, RingActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

}