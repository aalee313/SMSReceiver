package pl.defusadrian.smsreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.provider.Settings
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import com.example.smsreceiver.R

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "SMS_Receiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messagesFromIntent.forEach {
            val displayMessageBody = it.displayMessageBody

            var codeMessage: String? = null
            for (i in 8 downTo 6) {
                codeMessage = findXDigitNumber(i).find(displayMessageBody)?.value
                if (codeMessage != null) break
            }

            codeMessage?.apply {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(this, this)
                clipboard.primaryClip = clip
                showNotification(context, this)
            }
        }
    }

    private fun findXDigitNumber(digitNumber: Int) = "(?<!\\d)\\d{$digitNumber}(?!\\d)".toRegex()

    private fun showNotification(context: Context, message: String) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT).apply {
            enableLights(true)
            enableVibration(true)
        }

        val notificationBuilder = NotificationCompat.Builder(context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Sms code")
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setChannelId(CHANNEL_ID)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notificationBuilder.build())
    }
}