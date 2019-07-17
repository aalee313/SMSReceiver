package pl.defusadrian.smsreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.provider.Settings
import android.provider.Telephony
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.smsreceiver.R

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "SMS_Receiver"

        private const val ALIOR_SMS = "Kod SMS"
        private const val ING_SMS = "Kod do autoryzacji"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messagesFromIntent.forEach {
            val displayMessageBody = it.displayMessageBody

            val codeMessage = when {
                displayMessageBody.contains(ALIOR_SMS) -> getAliorCodeMessage(displayMessageBody)
                displayMessageBody.contains(ING_SMS) -> getINGCodeMessage(displayMessageBody)
                else -> ""
            }

            if (codeMessage.isNotEmpty()) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(codeMessage, codeMessage)
                clipboard.primaryClip = clip
                showNotification(context, codeMessage)
                Toast.makeText(context, codeMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAliorCodeMessage(message: String): String {
        val indexOfColon = message.indexOfLast { it == ':' }
        return message.substring(indexOfColon + 2, message.length)
    }

    private fun getINGCodeMessage(message: String): String {
        val indexOfColon = message.indexOf(':')
        val indexOfStars = message.indexOf('*')
        return message.substring(indexOfColon + 2, indexOfStars - 1)
    }

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