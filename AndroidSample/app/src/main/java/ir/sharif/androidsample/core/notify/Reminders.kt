package ir.sharif.androidsample.core.notify

import android.app.*
import android.content.*
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.sharif.androidsample.R

private const val CHANNEL_ID = "note_reminders"

object ReminderScheduler {

  fun schedule(
    context: Context,
    delayMillis: Long,
    title: String,
    message: String
  ) {
    if (delayMillis <= 0L) return
    ensureChannel(context)

    val intent = Intent(context, ReminderReceiver::class.java).apply {
      putExtra("title", title)
      putExtra("message", message)
    }
    val req = (System.currentTimeMillis() and 0x7FFFFFFF).toInt()
    val pi = PendingIntent.getBroadcast(
      context, req, intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val am = context.getSystemService(AlarmManager::class.java)
    val triggerAt = System.currentTimeMillis() + delayMillis

    // Android 12+ needs exact alarm privilege
    if (Build.VERSION.SDK_INT >= 31) {
      if (!am.canScheduleExactAlarms()) {
        // Ask once; also schedule an inexact alarm so the user still gets *something*.
        requestExactAlarmPermission(context)
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        return
      }
    }

    try {
      am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    } catch (_: SecurityException) {
      // Fallback to inexact instead of crashing
      am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }
  }

  private fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= 31) {
      val i = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      runCatching { context.startActivity(i) }
    }
  }

  private fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT < 26) return
    val nm = context.getSystemService(NotificationManager::class.java)
    if (nm.getNotificationChannel(CHANNEL_ID) != null) return
    nm.createNotificationChannel(
      NotificationChannel(
        CHANNEL_ID,
        "Note reminders",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply { description = "Alerts for note reminders" }
    )
  }
}

class ReminderReceiver : BroadcastReceiver() {
  @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
  override fun onReceive(context: Context, intent: Intent) {
    val title = intent.getStringExtra("title") ?: "Note reminder"
    val msg   = intent.getStringExtra("message") ?: "Don’t forget."

    val open = context.packageManager.getLaunchIntentForPackage(context.packageName)
    val contentPi = PendingIntent.getActivity(
      context, 0, open,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notif = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification) // ← see Manifest & vector below
      .setContentTitle(title)
      .setContentText(msg)
      .setAutoCancel(true)
      .setContentIntent(contentPi)
      .build()

    NotificationManagerCompat.from(context)
      .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notif)
  }
}
