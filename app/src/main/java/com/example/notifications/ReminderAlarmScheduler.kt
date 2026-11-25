package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.MedicineReminder
import java.util.Calendar

private const val CHANNEL_ID = "drxguard_medicine_reminders"
private const val CHANNEL_NAME = "Medicine Dose Reminders"
private const val TAG = "DRxReminderAlarm"

const val EXTRA_REMINDER_ID = "extra_reminder_id"
const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
const val EXTRA_DOSE = "extra_dose"
const val EXTRA_INSTRUCTIONS = "extra_instructions"
const val EXTRA_HOUR = "extra_hour"
const val EXTRA_MINUTE = "extra_minute"
const val EXTRA_SLOT_INDEX = "extra_slot_index"

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: "med_reminder"
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Scheduled Medicine"
        val dose = intent.getStringExtra(EXTRA_DOSE) ?: "Take prescribed dose"
        val instructions = intent.getStringExtra(EXTRA_INSTRUCTIONS).orEmpty()
        val hour = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)
        val slotIndex = intent.getIntExtra(EXTRA_SLOT_INDEX, 0)

        ReminderAlarmScheduler.showMedicineNotification(
            context = context,
            notificationId = (reminderId.hashCode() + slotIndex) and 0x7FFFFFFF,
            medicineName = medicineName,
            dose = dose,
            instructions = instructions
        )

        // Automatically schedule the next day's reminder at the same time
        if (hour in 0..23 && minute in 0..59) {
            ReminderAlarmScheduler.scheduleSingleSlot(
                context = context,
                reminderId = reminderId,
                medicineName = medicineName,
                dose = dose,
                instructions = instructions,
                hour = hour,
                minute = minute,
                slotIndex = slotIndex,
                forceTomorrow = true
            )
        }
    }
}

object ReminderAlarmScheduler {

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts for scheduled DRxGuard medicine doses"
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun showMedicineNotification(
        context: Context,
        notificationId: Int,
        medicineName: String,
        dose: String,
        instructions: String
    ) {
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted; skipping system notification.")
            return
        }
        createNotificationChannel(context)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bodyText = buildString {
            append("Dose: $dose")
            if (instructions.isNotBlank()) {
                append(" • $instructions")
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Medicine Reminder: $medicineName")
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    fun scheduleReminder(context: Context, reminder: MedicineReminder) {
        val reminderKey = reminder.id.ifBlank { reminder.medicineName }
        cancelReminder(context, reminderKey)

        if (reminder.status == "TAKEN" || reminder.status == "SKIPPED") {
            return
        }

        val times = reminder.reminderTimes.ifEmpty { listOf("08:00") }
        times.take(4).forEachIndexed { index, rawTime ->
            val parsed = parseHourMinute(rawTime) ?: return@forEachIndexed
            scheduleSingleSlot(
                context = context,
                reminderId = reminderKey,
                medicineName = "${reminder.medicineName} ${reminder.strength}".trim(),
                dose = reminder.prescribedDose.ifBlank { reminder.dosageForm },
                instructions = reminder.instructions,
                hour = parsed.first,
                minute = parsed.second,
                slotIndex = index,
                forceTomorrow = false
            )
        }
    }

    fun scheduleSingleSlot(
        context: Context,
        reminderId: String,
        medicineName: String,
        dose: String,
        instructions: String,
        hour: Int,
        minute: Int,
        slotIndex: Int,
        forceTomorrow: Boolean
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
                putExtra(EXTRA_MEDICINE_NAME, medicineName)
                putExtra(EXTRA_DOSE, dose)
                putExtra(EXTRA_INSTRUCTIONS, instructions)
                putExtra(EXTRA_HOUR, hour)
                putExtra(EXTRA_MINUTE, minute)
                putExtra(EXTRA_SLOT_INDEX, slotIndex)
            }
            val requestCode = ((reminderId.hashCode() * 31) + slotIndex) and 0x7FFFFFFF
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (forceTomorrow || timeInMillis <= System.currentTimeMillis() + 5_000L) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerCal.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            Log.w(TAG, "Unable to schedule reminder alarm", e)
        }
    }

    fun cancelReminder(context: Context, reminderId: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (slotIndex in 0 until 4) {
                val intent = Intent(context, ReminderAlarmReceiver::class.java)
                val requestCode = ((reminderId.hashCode() * 31) + slotIndex) and 0x7FFFFFFF
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to cancel reminder alarm", e)
        }
    }

    private fun parseHourMinute(raw: String): Pair<Int, Int>? {
        val clean = raw.trim().uppercase()
        val isPm = clean.endsWith("PM")
        val isAm = clean.endsWith("AM")
        val timePart = clean.removeSuffix("AM").removeSuffix("PM").trim()
        val parts = timePart.split(":")
        if (parts.size < 2) return null
        var hour = parts[0].trim().toIntOrNull() ?: return null
        val minute = parts[1].trim().take(2).toIntOrNull() ?: return null
        if (isPm && hour in 1..11) hour += 12
        if (isAm && hour == 12) hour = 0
        if (hour !in 0..23 || minute !in 0..59) return null
        return hour to minute
    }
}
