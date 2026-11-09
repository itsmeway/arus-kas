package com.example.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

enum class RoutineReminderType(
    val title: String,
    val defaultMessage: String,
    val notificationId: Int
) {
    AFTER_LUNCH(
        title = "🍱 Habis Makan Siang? Cek Catatan!",
        defaultMessage = "Sudah selesai makan siang? Yuk catat pengeluaran makan/minummu sekarang agar saldo tetap presisi!",
        notificationId = 2001
    ),
    AFTER_WORK(
        title = "🛵 Selesai Aktivitas Kerja? Cek Catatan!",
        defaultMessage = "Aktivitas kerja hari ini selesai! Cek apakah ada bensin, parkir, atau jajan sore yang belum dicatat.",
        notificationId = 2002
    ),
    EVENING_REVIEW(
        title = "🌙 Evaluasi Catatan Malam Ini",
        defaultMessage = "Luangkan 1 menit sebelum tidur untuk rekap catatan keuangan harianmu. Dompet aman, pikiran tenang!",
        notificationId = 2003
    ),
    POST_ACTIVITY_INSTANT(
        title = "📝 Catatan Baru Selesai Dicatat!",
        defaultMessage = "Aktivitas pengeluaran baru saja disimpan. Cek catatan & sisa limit posmu agar tetap terkontrol.",
        notificationId = 2004
    ),
    TEST_PREVIEW(
        title = "🔔 Pengingat Cerdas ArusKas Siap Aktif!",
        defaultMessage = "Pengingat aktivitas berjalan normal. Aplikasi otomatis hening saat jam sibuk agar tidak bentrok dengan kerjamu!",
        notificationId = 2005
    )
}

object ActivityReminderNotificationHelper {
    const val CHANNEL_ID_ACTIVITY_REMINDERS = "aruskas_activity_reminders"
    private const val CHANNEL_NAME = "Pengingat Catatan & Aktivitas Harian"
    private const val CHANNEL_DESC = "Pengingat sehabis aktivitas untuk mengecek catatan keuangan tanpa bentrok dengan jam sibuk."

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_ACTIVITY_REMINDERS,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Memeriksa apakah waktu saat ini berada pada rentang JAM SIBUK KERJA pengguna.
     * Jika berada di jam sibuk, pengingat berjadwal WAJIB DIBUNGKAM agar tidak mengganggu fokus.
     */
    fun isWithinBusyWorkHours(
        profile: UserFinancialProfile,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = when (profile.activeDaysType) {
            "SENIN_JUMAT" -> dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
            "SENIN_SABTU" -> dayOfWeek == Calendar.SUNDAY
            else -> false
        }
        if (isWeekend) return false // Hari libur, tidak ada jam sibuk kerja

        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val workStart = profile.workStartHour * 60 + profile.workStartMinute
        val lunchStart = profile.lunchBreakHour * 60 + profile.lunchBreakMinute
        val lunchEnd = lunchStart + 60 // Durasi makan siang 60 menit
        val workEnd = profile.workEndHour * 60 + profile.workEndMinute

        // Jam sibuk pagi: dari mulai kerja sampai sebelum istirahat siang
        if (currentMinutes in workStart until lunchStart) return true
        // Jam sibuk siang: sehabis istirahat siang sampai selesai kerja
        if (currentMinutes in lunchEnd until workEnd) return true

        return false
    }

    /**
     * Mengirim notifikasi Android pengingat setelah pengguna mencatat aktivitas transaksi.
     */
    fun sendPostActivityNotification(
        context: Context,
        activityTitle: String,
        amountFormatted: String,
        categoryLabel: String,
        note: String,
        remainingAllowance: String
    ): Boolean {
        initNotificationChannel(context)
        if (!hasNotificationPermission(context)) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val detailText = "Catatan: ${note.ifBlank { activityTitle }} • Sisa Plafon: $remainingAllowance"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACTIVITY_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📝 Selesai Catat $activityTitle ($amountFormatted)")
            .setContentText("Yuk cek catatan & sisa limitmu: $detailText")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Aktivitas baru selesai dicatat!\n• Pos: $categoryLabel\n• Pengeluaran: $amountFormatted\n• Catatan: ${note.ifBlank { activityTitle }}\n• Sisa Kuota/Limit: $remainingAllowance\n\n👉 Buka ArusKas untuk cek rincian saldo dan pastikan keuangan tetap aman.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                RoutineReminderType.POST_ACTIVITY_INSTANT.notificationId,
                notification
            )
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    /**
     * Mengirim notifikasi berjadwal rutin sehabis aktivitas harian (makan siang, pulang kerja, rekap malam, atau tes).
     */
    fun sendRoutineNotification(
        context: Context,
        type: RoutineReminderType,
        profile: UserFinancialProfile
    ): Boolean {
        initNotificationChannel(context)
        if (!hasNotificationPermission(context)) return false

        // Jika bukan mode tes, cegah notifikasi masuk saat jam sibuk kerja
        if (type != RoutineReminderType.TEST_PREVIEW && isWithinBusyWorkHours(profile)) {
            return false // Dibungkam otomatis demi kenyamanan pengguna
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            type.notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val scheduleHint = when (type) {
            RoutineReminderType.AFTER_LUNCH -> "Sehabis jam istirahat siang (${String.format("%02d:%02d", profile.lunchBreakHour, profile.lunchBreakMinute)})"
            RoutineReminderType.AFTER_WORK -> "Sehabis jam selesai kerja (${String.format("%02d:%02d", profile.workEndHour, profile.workEndMinute)})"
            RoutineReminderType.EVENING_REVIEW -> "Waktu santai malam (${String.format("%02d:%02d", profile.eveningReviewHour, profile.eveningReviewMinute)})"
            RoutineReminderType.TEST_PREVIEW -> "Jadwal Kerjamu: ${String.format("%02d:%02d", profile.workStartHour, profile.workStartMinute)} - ${String.format("%02d:%02d", profile.workEndHour, profile.workEndMinute)} (Otomatis Hening)"
            else -> ""
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACTIVITY_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(type.title)
            .setContentText(type.defaultMessage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${type.defaultMessage}\n\n⏱️ $scheduleHint\n🛡️ Proteksi Anti-Bentrok: Notifikasi dibungkam saat kamu sedang fokus kerja/kuliah.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(type.notificationId, notification)
            return true
        } catch (e: SecurityException) {
            return false
        }
    }
}
