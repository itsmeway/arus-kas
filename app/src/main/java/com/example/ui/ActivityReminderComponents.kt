package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ActivityReminderNotificationHelper
import com.example.data.PresetCategories
import com.example.data.RoutineReminderType
import com.example.data.RoutineScheduleOption
import com.example.data.UserFinancialProfile
import com.example.ui.theme.BcaCobaltBlue
import com.example.ui.theme.BcaElectricCyan
import com.example.ui.theme.BcaRoyalBlue
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue

/**
 * Banner In-App Elegan yang muncul sesaat setelah pengguna mencatat aktivitas transaksi
 * untuk mengingatkan mereka mengecek catatan & sisa limit kategori.
 */
@Composable
fun PostActivityReviewBanner(
    visible: Boolean,
    activityTitle: String,
    amountFormatted: String,
    categoryLabel: String,
    remainingAllowance: String,
    onCheckRecords: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = glassmorphicBorderStroke(accentColor = BcaRoyalBlue, width = 1.2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .glassmorphicCardEffect(cornerRadius = 18.dp, accentTint = BcaRoyalBlue)
                .testTag("post_activity_review_banner")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BcaRoyalBlue.copy(alpha = 0.14f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Tercatat",
                                    tint = BcaRoyalBlue,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Aktivitas Berhasil Dicatat!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = BcaRoyalBlue
                            )
                            Text(
                                text = "$activityTitle • $amountFormatted ($categoryLabel)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("dismiss_post_activity_banner")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BcaRoyalBlue.copy(alpha = 0.08f),
                    border = BorderStroke(0.8.dp, BcaRoyalBlue.copy(alpha = 0.22f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Sisa Plafon / Saldo Pos Ini:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = remainingAllowance,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = BcaRoyalBlue
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Sudah Cukup",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = onCheckRecords,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BcaRoyalBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_check_records_after_activity")
                    ) {
                        Text(
                            text = "🔍 Cek Riwayat & Limit",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kartu Dashboard Pemantau Pengingat Rutinitas Harian (Bebas Bentrok).
 * Menampilkan jadwal jam pengingat harian, status proteksi hening saat jam kerja,
 * tombol uji notifikasi langsung, serta tombol pengaturan jadwal.
 */
@Composable
fun ActivityReminderDashboardCard(
    profile: UserFinancialProfile,
    onOpenScheduleSettings: () -> Unit,
    onTestNotificationTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = glassmorphicBorderStroke(accentColor = BcaCobaltBlue, width = 1.1.dp),
        modifier = modifier
            .fillMaxWidth()
            .glassmorphicCardEffect(cornerRadius = 18.dp, accentTint = BcaCobaltBlue)
            .testTag("activity_reminder_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccent.copy(alpha = 0.18f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Pengingat Aktivitas",
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = "PENGINGAT HABIS AKTIVITAS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                    FeatureHelpButton(
                        content = FeatureHelpDatabase.PENGINGAT_AKTIVITAS,
                        iconSize = 11,
                        tint = GoldAccent,
                        testTag = "help_pengingat_aktivitas"
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (profile.isSmartScheduledReminderEnabled) "Anti-Bentrok Aktif ✓" else "Nonaktif",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (profile.isSmartScheduledReminderEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            // 3 Waktu Pengingat Sehabis Aktivitas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Habis Makan Siang
                ReminderSlotPill(
                    icon = "🍱",
                    title = "Habis Siang",
                    timeLabel = String.format("%02d:%02d", profile.lunchBreakHour, profile.lunchBreakMinute),
                    modifier = Modifier.weight(1f)
                )
                // 2. Selesai Kerja / Pulang
                ReminderSlotPill(
                    icon = "🛵",
                    title = "Selesai Kerja",
                    timeLabel = String.format("%02d:%02d", profile.workEndHour, profile.workEndMinute),
                    modifier = Modifier.weight(1f)
                )
                // 3. Rekap Malam
                ReminderSlotPill(
                    icon = "🌙",
                    title = "Rekap Malam",
                    timeLabel = String.format("%02d:%02d", profile.eveningReviewHour, profile.eveningReviewMinute),
                    modifier = Modifier.weight(1f)
                )
            }

            // Banner Proteksi Jam Sibuk
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = BorderStroke(0.7.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Proteksi Anti-Bentrok",
                        tint = SkyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "Jam Kerja: ${String.format("%02d:%02d", profile.workStartHour, profile.workStartMinute)} - ${String.format("%02d:%02d", profile.workEndHour, profile.workEndMinute)} (${when (profile.activeDaysType) {
                                "SENIN_JUMAT" -> "Senin-Jumat"
                                "SENIN_SABTU" -> "Senin-Sabtu"
                                else -> "Setiap Hari"
                            }})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "🛡️ Notifikasi otomatis DIBUNGKAM saat jam kerja agar tidak mengganggu fokusmu.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 13.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tombol Aksi: Tes Notifikasi & Atur Jam Harian
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        ActivityReminderNotificationHelper.sendRoutineNotification(
                            context = context,
                            type = RoutineReminderType.TEST_PREVIEW,
                            profile = profile
                        )
                        onTestNotificationTriggered()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_test_notification_reminder")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Tes Notifikasi",
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tes Notif HP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Button(
                    onClick = onOpenScheduleSettings,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent.copy(alpha = 0.2f),
                        contentColor = GoldAccent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_configure_daily_routine_schedule")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Atur Jam Harian",
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Atur Jam Harian",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderSlotPill(
    icon: String,
    title: String,
    timeLabel: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = icon, fontSize = 13.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                fontWeight = FontWeight.ExtraBold,
                color = GoldAccent
            )
        }
    }
}

/**
 * Dialog Pengaturan Jadwal Rutinitas & Pengingat Harian (Anti-Bentrok).
 * Pengguna dapat memilih shift kerja/kuliah, jam istirahat siang, jam pulang,
 * jam rekap malam, hari kerja, dan menyalakan/mematikan pengingat sehabis aktivitas.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineScheduleSettingsDialog(
    initialProfile: UserFinancialProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (UserFinancialProfile) -> Unit
) {
    var selectedShiftType by remember { mutableStateOf(initialProfile.workShiftType) }
    var workStartHour by remember { mutableIntStateOf(initialProfile.workStartHour) }
    var workStartMinute by remember { mutableIntStateOf(initialProfile.workStartMinute) }
    var lunchHour by remember { mutableIntStateOf(initialProfile.lunchBreakHour) }
    var lunchMinute by remember { mutableIntStateOf(initialProfile.lunchBreakMinute) }
    var workEndHour by remember { mutableIntStateOf(initialProfile.workEndHour) }
    var workEndMinute by remember { mutableIntStateOf(initialProfile.workEndMinute) }
    var eveningHour by remember { mutableIntStateOf(initialProfile.eveningReviewHour) }
    var eveningMinute by remember { mutableIntStateOf(initialProfile.eveningReviewMinute) }
    var activeDaysType by remember { mutableStateOf(initialProfile.activeDaysType) }
    var isPostActivityEnabled by remember { mutableStateOf(initialProfile.isPostActivityReminderEnabled) }
    var isSmartScheduledEnabled by remember { mutableStateOf(initialProfile.isSmartScheduledReminderEnabled) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("routine_schedule_settings_dialog"),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jadwal & Pengingat Harian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pengingat pintar tanpa bentrok dengan jam sibukmu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider()

                // 1. Pola Jam Kerja / Aktivitas Harian
                Text(
                    text = "1. Pola Jam Kerja / Kuliah / Aktivitas Utama",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PresetCategories.defaultRoutineSchedules.forEach { opt ->
                        val isSel = selectedShiftType == opt.key
                        Surface(
                            onClick = {
                                selectedShiftType = opt.key
                                workStartHour = opt.startHour
                                workStartMinute = opt.startMinute
                                lunchHour = opt.lunchHour
                                lunchMinute = opt.lunchMinute
                                workEndHour = opt.endHour
                                workEndMinute = opt.endMinute
                                eveningHour = opt.eveningHour
                                eveningMinute = opt.eveningMinute
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) GoldAccent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                width = if (isSel) 1.2.dp else 0.8.dp,
                                color = if (isSel) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) GoldAccent else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = opt.timeRangeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 2. Jam Istirahat Siang
                Text(
                    text = "2. Jam Istirahat / Selesai Makan Siang",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pengingat catat makan siang dikirim SEHABIS jam istirahat ini selesai.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        (12 to 0) to "12:00",
                        (12 to 30) to "12:30 (Rekomendasi)",
                        (13 to 0) to "13:00",
                        (13 to 30) to "13:30"
                    ).forEach { (timePair, label) ->
                        val isSel = lunchHour == timePair.first && lunchMinute == timePair.second
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                lunchHour = timePair.first
                                lunchMinute = timePair.second
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // 3. Jam Santai Malam / Sebelum Tidur
                Text(
                    text = "3. Jam Santai Malam / Rekap Catatan Harian",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        (20 to 0) to "20:00",
                        (21 to 0) to "21:00 (Santai)",
                        (21 to 30) to "21:30",
                        (22 to 0) to "22:00"
                    ).forEach { (timePair, label) ->
                        val isSel = eveningHour == timePair.first && eveningMinute == timePair.second
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                eveningHour = timePair.first
                                eveningMinute = timePair.second
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // 4. Hari Aktif / Hari Kerja
                Text(
                    text = "4. Hari Aktif Utama / Hari Kerja",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "SENIN_JUMAT" to "Senin - Jumat",
                        "SENIN_SABTU" to "Senin - Sabtu",
                        "SETIAP_HARI" to "Setiap Hari"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = activeDaysType == key,
                            onClick = { activeDaysType = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // 5. Opsi Toggle Pengingat
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pengingat Langsung Habis Transaksi",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Munculkan banner & notif setelah baru selesai catat pengeluaran/belanja",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isPostActivityEnabled,
                                onCheckedChange = { isPostActivityEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pengingat Berjadwal Anti-Bentrok",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ingatkan sehabis makan siang, sehabis pulang kerja, dan malam (otomatis hening saat kerja)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isSmartScheduledEnabled,
                                onCheckedChange = { isSmartScheduledEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent)
                            )
                        }
                    }
                }

                // Tombol Simpan
                Button(
                    onClick = {
                        val updatedProfile = initialProfile.copy(
                            workShiftType = selectedShiftType,
                            workStartHour = workStartHour,
                            workStartMinute = workStartMinute,
                            lunchBreakHour = lunchHour,
                            lunchBreakMinute = lunchMinute,
                            workEndHour = workEndHour,
                            workEndMinute = workEndMinute,
                            eveningReviewHour = eveningHour,
                            eveningReviewMinute = eveningMinute,
                            activeDaysType = activeDaysType,
                            isPostActivityReminderEnabled = isPostActivityEnabled,
                            isSmartScheduledReminderEnabled = isSmartScheduledEnabled
                        )
                        onSaveProfile(updatedProfile)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BcaRoyalBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_routine_schedule")
                ) {
                    Text(
                        text = "Simpan Jadwal Pengingat ✓",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
