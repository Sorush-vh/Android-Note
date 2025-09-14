package ir.sharif.androidsample.util

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.UUID

object ReminderScheduler {
  private const val CHANNEL_ID = "notes_reminders"

  fun schedule(
    context: Context,
    delayMillis: Long,
    title: String,
    message: String
  ): UUID {
    ensureChannel(context)
    val data = workDataOf(
      "title" to title,
      "message" to message
    )
    val req = OneTimeWorkRequestBuilder<ReminderWorker>()
      .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
      .setInputData(data)
      .build()
    WorkManager.getInstance(context).enqueue(req)
    return req.id
  }

  fun cancel(context: Context, id: UUID) {
    WorkManager.getInstance(context).cancelWorkById(id)
  }

  private fun ensureChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Notes Reminders",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply { description = "Reminders for your notes" }
      val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      nm.createNotificationChannel(channel)
    }
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  internal fun notify(ctx: Context, title: String, message: String) {
    val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setAutoCancel(true)
      .build()
    NotificationManagerCompat.from(ctx).notify(title.hashCode(), notif)
  }
}

class ReminderWorker(appContext: Context, params: WorkerParameters)
  : CoroutineWorker(appContext, params) {

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override suspend fun doWork(): Result {
    val title = inputData.getString("title") ?: "Note reminder"
    val message = inputData.getString("message") ?: "Your reminder is due."
    ReminderScheduler.notify(applicationContext, title, message)
    return Result.success()
  }
}
