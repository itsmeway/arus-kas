package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CategoryLimitConfig
import com.example.data.MandatoryBillItem
import com.example.data.PresetCategories
import com.example.data.UserFinancialProfile
import com.example.data.Wallet
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue

/**
 * Kuesioner Profil Keuangan Awal (Pre-Dashboard Financial Profiling)
 * Menanyakan profil pengguna (Laki-laki/Perempuan, sumber penghasilan, nominal gaji,
 * kondisi kosan/rumah, daftar tagihan wajib & jatuh tempo, serta opsi tambah/kurang
 * Bank & E-Wallet) agar begitu masuk ke Dashboard semua sudah siap tinggal pakai.
 * Kuesioner ini HANYA muncul otomatis 1x saat pertama kali buka aplikasi,
 * namun tetap tersedia kapan saja di Pengaturan / Tombol Atas Dashboard.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialProfileOnboardingDialog(
    initialProfile: UserFinancialProfile,
    currentWallets: List<Wallet>,
    onDismiss: () -> Unit,
    onOpenUserGuide: () -> Unit = {},
    onCompleteSetup: (
        profile: UserFinancialProfile,
        configuredWallets: List<Wallet>,
        configuredBills: List<MandatoryBillItem>,
        customLimits: Map<String, CategoryLimitConfig>
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Profil & Penghasilan, 2: Tagihan Wajib & Jatuh Tempo, 3: Tambah/Kurang Bank & E-Wallet + Limit Fleksibel

    // Step 1 State: Identitas, Gender, Sumber Penghasilan & Gaji
    var nickname by remember { mutableStateOf(initialProfile.nickname) }
    var gender by remember { mutableStateOf(initialProfile.gender) } // LAKI_LAKI, PEREMPUAN
    var livingStatus by remember { mutableStateOf(initialProfile.livingStatus) } // NGEKOS, RUMAH_ORTU, KELUARGA
    var incomeSource by remember { mutableStateOf(initialProfile.incomeSourcePrimary) }
    var monthlyIncomeStr by remember { mutableStateOf(initialProfile.monthlyIncomeAmount.toString()) }
    var controlMode by remember { mutableStateOf(initialProfile.controlMode) }
    var paydayDateStr by remember { mutableStateOf(initialProfile.paydayDate.toString()) }
    var transportMode by remember { mutableStateOf(initialProfile.transportMode) }
    var dependentsCount by remember { mutableIntStateOf(initialProfile.dependentsCount) }
    var primaryGoal by remember { mutableStateOf(initialProfile.primaryFinancialGoal) }
    var workShiftType by remember { mutableStateOf(initialProfile.workShiftType) }
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

    val selectedHabits = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("Bensin", transportMode == "MOTOR" || transportMode == "MOBIL")
            put("Makan Harian", true)
            put("Rokok / Vape", gender == "LAKI_LAKI")
            put("Skincare / Rawat Diri", gender == "PEREMPUAN")
            put("Kopi & Jajan", true)
            put("Kuota Internet", true)
            put("Bantu Ortu / Sosial", dependentsCount > 0)
        }
    }

    // Step 2 State: Tagihan Wajib & Tanggal Jatuh Tempo / Siklus Habis-Beli Lagi
    var hasKosanBill by remember { mutableStateOf(livingStatus == "NGEKOS") }
    var kosanAmountStr by remember { mutableStateOf("850000") }
    var kosanDueDayStr by remember { mutableStateOf("1") }

    var hasKuotaBill by remember { mutableStateOf(true) }
    var kuotaAmountStr by remember { mutableStateOf("100000") }
    var kuotaExpireDayStr by remember { mutableStateOf("15") }
    var kuotaBuyAgainDayStr by remember { mutableStateOf("16") }

    var hasListrikWifiBill by remember { mutableStateOf(true) }
    var listrikAmountStr by remember { mutableStateOf("250000") }
    var listrikDueDayStr by remember { mutableStateOf("20") }

    var hasCicilanOrOtherBill by remember { mutableStateOf(false) }
    var otherBillName by remember { mutableStateOf("Cicilan / Langganan") }
    var otherBillAmountStr by remember { mutableStateOf("350000") }
    var otherBillDueDayStr by remember { mutableStateOf("5") }

    // Step 3 State: Daftar Bank & E-Wallet yang bisa ditambah / dikurangi oleh pengguna + Saldo Awal
    val selectedWallets = remember {
        val baseList = if (currentWallets.isNotEmpty()) currentWallets else PresetCategories.defaultStarterWallets
        mutableStateListOf<Wallet>().apply { addAll(baseList) }
    }
    val walletBalancesMap = remember {
        mutableStateMapOf<String, String>().apply {
            val baseList = if (currentWallets.isNotEmpty()) currentWallets else PresetCategories.defaultStarterWallets
            baseList.forEach { w -> put(w.id, w.balance.toString()) }
        }
    }
    var showCustomWalletCreator by remember { mutableStateOf(false) }
    var customWalletName by remember { mutableStateOf("") }
    var customWalletType by remember { mutableStateOf("BANK") } // BANK, EWALLET, CASH
    var customWalletBalanceStr by remember { mutableStateOf("") }

    val parsedIncome = (monthlyIncomeStr.filter { it.isDigit() }.toLongOrNull() ?: 4_500_000L).coerceAtLeast(500_000L)

    // Auto-calculate realistic flexible limits based on user's monthly income & profile
    val calcBillsTotal = remember(
        hasKosanBill, kosanAmountStr,
        hasKuotaBill, kuotaAmountStr,
        hasListrikWifiBill, listrikAmountStr,
        hasCicilanOrOtherBill, otherBillAmountStr
    ) {
        var total = 0L
        if (hasKosanBill) total += kosanAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
        if (hasKuotaBill) total += kuotaAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
        if (hasListrikWifiBill) total += listrikAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
        if (hasCicilanOrOtherBill) total += otherBillAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
        total.coerceAtLeast(200_000L)
    }

    val remainingAfterBills = (parsedIncome - calcBillsTotal).coerceAtLeast(500_000L)
    val autoLimitHarianBensinMakan = ((remainingAfterBills * 42L) / 100L).coerceAtLeast(300_000L)
    val autoLimitJajanNgopi = ((remainingAfterBills * 18L) / 100L).coerceAtLeast(150_000L)
    val autoLimitRawatDiri = ((remainingAfterBills * (if (gender == "PEREMPUAN") 15L else 10L)) / 100L).coerceAtLeast(100_000L)
    val autoLimitSosialOrtu = ((remainingAfterBills * 10L) / 100L).coerceAtLeast(100_000L)
    val autoLimitServisKerja = ((remainingAfterBills * 10L) / 100L).coerceAtLeast(100_000L)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 18.dp)
                .testTag("financial_profile_onboarding_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.2.dp, EmeraldPrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "KENALI KEUANGANMU • LANGKAH $step/3",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        TextButton(onClick = onDismiss) {
                            Text("Lewati", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Text(
                        text = when (step) {
                            1 -> "Sesuaikan Profil & Penghasilanmu"
                            2 -> "Atur Jadwal Tagihan Wajib & Kebiasaan Harian"
                            else -> "Cek Saldo Dompet & Limit Fleksibel Otomatis"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    LinearProgressIndicator(
                        progress = { step / 3f },
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }

                // Scrollable Body per Step
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (step == 1) {
                        // 1. Jenis Kelamin (Laki-laki / Perempuan)
                        Text(
                            text = "1. Kamu Laki-laki atau Perempuan?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Menyesuaikan pintasan pengeluaran harian (misal: Bensin/Rokok/Barber atau Skincare/Dapur/Jajan).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                "LAKI_LAKI" to "👨 Laki-laki",
                                "PEREMPUAN" to "👩 Perempuan"
                            ).forEach { (key, label) ->
                                val isSel = gender == key
                                Surface(
                                    onClick = {
                                        gender = key
                                        if (key == "LAKI_LAKI") {
                                            selectedHabits["Rokok / Vape"] = true
                                        } else {
                                            selectedHabits["Skincare / Rawat Diri"] = true
                                            selectedHabits["Rokok / Vape"] = false
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("gender_option_$key")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Sumber Penghasilan
                        Text(
                            text = "2. Penghasilan Utama Kamu dari Mana?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Gaji Bulanan",
                                "Harian / Ngojek Online",
                                "Usaha / Dagang",
                                "Freelance / Proyek",
                                "Kiriman / Uang Saku"
                            ).forEach { src ->
                                FilterChip(
                                    selected = incomeSource == src,
                                    onClick = { incomeSource = src },
                                    label = { Text(src, style = MaterialTheme.typography.labelMedium) }
                                )
                            }
                        }

                        // 3. Nominal Penghasilan / Gaji per Bulan
                        Text(
                            text = "3. Rata-rata Penghasilan / Gaji per Bulan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = monthlyIncomeStr,
                            onValueChange = { monthlyIncomeStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Penghasilan / Gaji Bulanan (Rp)") },
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                            visualTransformation = RupiahThousandsVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_income_input")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2_000_000L, 3_500_000L, 4_500_000L, 6_000_000L, 8_000_000L, 12_000_000L).forEach { preset ->
                                Surface(
                                    onClick = { monthlyIncomeStr = preset.toString() },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (parsedIncome == preset) EmeraldPrimary else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Text(
                                        text = ArusKasViewModel.formatCompactRupiah(preset),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (parsedIncome == preset) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // 4. Kondisi Tempat Tinggal
                        Text(
                            text = "4. Status Tempat Tinggal Saat Ini",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "NGEKOS" to "🏠 Ngekos/Kontrak",
                                "RUMAH_ORTU" to "🏡 Rumah Ortu",
                                "KELUARGA" to "👨‍👩‍👧 Rumah Sendiri"
                            ).forEach { (key, label) ->
                                val isSel = livingStatus == key
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        livingStatus = key
                                        hasKosanBill = (key == "NGEKOS")
                                    },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // 5. Tanggal Gajian / Siklus Cair Uang
                        Text(
                            text = "5. Tanggal Berapa Gajian atau Uang Masuk? (1 - 31)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aplikasi akan otomatis mengingatkan untuk mengecek sisa tagihan wajib menjelang tanggal ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = paydayDateStr,
                            onValueChange = { paydayDateStr = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Tanggal Gajian / Uang Cair (misal: 25 atau 1)") },
                            prefix = { Text("Tgl ", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 6. Kendaraan / Transportasi Harian
                        Text(
                            text = "6. Transportasi Utama untuk Aktivitas Sehari-hari",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "MOTOR" to "🛵 Motor Pribadi",
                                "MOBIL" to "🚗 Mobil Pribadi",
                                "TRANSPORT_UMUM" to "🚆 Ojol / Kereta / Bus",
                                "JALAN_KAKI" to "🚶 Dekat / Jalan Kaki"
                            ).forEach { (key, label) ->
                                FilterChip(
                                    selected = transportMode == key,
                                    onClick = { transportMode = key },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // 7. Jumlah Tanggungan Keluarga / Orang Tua
                        Text(
                            text = "7. Jumlah Tanggungan yang Perlu Dinafkahi / Dibantu",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                0 to "Sendiri (0)",
                                1 to "1 Orang",
                                2 to "2 Orang",
                                3 to "3+ Orang"
                            ).forEach { (cnt, label) ->
                                FilterChip(
                                    selected = dependentsCount == cnt,
                                    onClick = { dependentsCount = cnt },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // 8. Target Keuangan Utama
                        Text(
                            text = "8. Target Keuangan Terpentingmu Saat Ini",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "BEBAS_BONCOS" to "🛡️ Setop Boncos Harian",
                                "BISA_MENABUNG" to "🎯 Punya Tabungan Impian",
                                "LUNASI_CICILAN" to "💳 Bayar Tagihan Tertib",
                                "DANA_DARURAT" to "🆘 Kumpulkan Pos Darurat"
                            ).forEach { (key, label) ->
                                FilterChip(
                                    selected = primaryGoal == key,
                                    onClick = { primaryGoal = key },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // 9. Jam Kerja / Aktivitas Harian (Anti-Bentrok)
                        Text(
                            text = "9. Pola Jam Kerja / Kuliah / Aktivitas Utama Harian",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aplikasi otomatis hening saat jam sibuk ini agar pengingat tidak bentrok dengan meeting/pekerjaanmu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PresetCategories.defaultRoutineSchedules.forEach { opt ->
                                val isSel = workShiftType == opt.key
                                Surface(
                                    onClick = {
                                        workShiftType = opt.key
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
                                    color = if (isSel) GoldAccent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        width = if (isSel) 1.2.dp else 0.8.dp,
                                        color = if (isSel) GoldAccent else MaterialTheme.colorScheme.outline
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
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // 10. Jam Istirahat Siang
                        Text(
                            text = "10. Jam Berapa Biasanya Selesai Makan Siang?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pengingat cek catatan makan siang hanya akan dikirim SEHABIS jam istirahat ini selesai.",
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

                        // 11. Jam Santai Malam / Sebelum Tidur
                        Text(
                            text = "11. Jam Santai Malam / Rekap Catatan Harian",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Waktu tenang sebelum istirahat untuk memastikan catatan hari ini sudah rapi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

                        // 12. Hari Aktif Utama / Hari Kerja
                        Text(
                            text = "12. Hari Kerja Utama Kamu",
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

                        // 13. Mode Pengingat Sehabis Aktivitas
                        Text(
                            text = "13. Pengingat Langsung Setiap Habis Aktivitas Belanja",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                true to "Aktif (Disarankan) ✓",
                                false to "Hanya Jam Rutin"
                            ).forEach { (bVal, label) ->
                                FilterChip(
                                    selected = isPostActivityEnabled == bVal,
                                    onClick = { isPostActivityEnabled = bVal },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    } else if (step == 2) {
                        // STEP 2: Daftar Tagihan Wajib & Kebiasaan Pengeluaran Harian Tak Terjadwal
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SkyBlue.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Atur tagihan rutin yang punya tanggal jatuh tempo (Kosan, Kuota, Listrik). Nanti di Dashboard tinggal kamu ceklist Lunas / Setengah!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        // Bill 1: Kosan / Kontrakan
                        OnboardingBillSetupCard(
                            checked = hasKosanBill,
                            onCheckedChange = { hasKosanBill = it },
                            title = "🏠 Bayar Kosan / Kontrakan",
                            amountStr = kosanAmountStr,
                            onAmountChange = { kosanAmountStr = it.filter { c -> c.isDigit() } },
                            scheduleLabel = "Bayar setiap tanggal berapa? (1-31)",
                            dayStr = kosanDueDayStr,
                            onDayChange = { kosanDueDayStr = it.filter { c -> c.isDigit() }.take(2) }
                        )

                        // Bill 2: Kuota Internet (Habis tgl sekian, beli lagi tgl sekian)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📶 Paket Kuota Internet / Data",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    FilterChip(
                                        selected = hasKuotaBill,
                                        onClick = { hasKuotaBill = !hasKuotaBill },
                                        label = { Text(if (hasKuotaBill) "Aktif ✓" else "Tidak") }
                                    )
                                }
                                if (hasKuotaBill) {
                                    OutlinedTextField(
                                        value = kuotaAmountStr,
                                        onValueChange = { kuotaAmountStr = it.filter { c -> c.isDigit() } },
                                        label = { Text("Harga Paket Kuota (Rp)") },
                                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                                        visualTransformation = RupiahThousandsVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = kuotaExpireDayStr,
                                            onValueChange = { kuotaExpireDayStr = it.filter { c -> c.isDigit() }.take(2) },
                                            label = { Text("Kuota Habis Tgl") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = kuotaBuyAgainDayStr,
                                            onValueChange = { kuotaBuyAgainDayStr = it.filter { c -> c.isDigit() }.take(2) },
                                            label = { Text("Beli Lagi Tgl") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Bill 3: Token Listrik / WiFi
                        OnboardingBillSetupCard(
                            checked = hasListrikWifiBill,
                            onCheckedChange = { hasListrikWifiBill = it },
                            title = "⚡ Token Listrik / WiFi / Air",
                            amountStr = listrikAmountStr,
                            onAmountChange = { listrikAmountStr = it.filter { c -> c.isDigit() } },
                            scheduleLabel = "Jatuh tempo / isi setiap tanggal:",
                            dayStr = listrikDueDayStr,
                            onDayChange = { listrikDueDayStr = it.filter { c -> c.isDigit() }.take(2) }
                        )

                        // Bill 4: Cicilan Motor / Gadget / Pinjaman Bulanan
                        OnboardingBillSetupCard(
                            checked = hasCicilanOrOtherBill,
                            onCheckedChange = { hasCicilanOrOtherBill = it },
                            title = "💳 Cicilan Kendaraan / Gadget / Pinjaman",
                            amountStr = otherBillAmountStr,
                            onAmountChange = { otherBillAmountStr = it.filter { c -> c.isDigit() } },
                            scheduleLabel = "Jatuh tempo cicilan setiap tanggal:",
                            dayStr = otherBillDueDayStr,
                            onDayChange = { otherBillDueDayStr = it.filter { c -> c.isDigit() }.take(2) }
                        )

                        // Kebiasaan Pengeluaran Harian (Tanpa jam/tanggal pasti)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Pengeluaran Harian yang Sering Kamu Beli (Tanpa Jam/Tanggal Pasti):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Beli bensin, rokok, atau makan tidak bisa dipatok jam/tanggalnya. ArusKas akan membuatkan kuota fleksibel yang otomatis menyesuaikan sisa hari!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            selectedHabits.keys.toList().forEach { habit ->
                                val isChecked = selectedHabits[habit] == true
                                FilterChip(
                                    selected = isChecked,
                                    onClick = { selectedHabits[habit] = !isChecked },
                                    label = { Text(habit) }
                                )
                            }
                        }
                    } else {
                        // STEP 3: Tambah / Kurangi Bank & E-Wallet + Saldo Awal & Preview Limit Fleksibel Otomatis
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SkyBlue.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✨ Pertanyaan ini HANYA muncul 1x di awal. Kamu bebas tambah/hapus Bank & E-Wallet yang kamu pakai!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = onOpenUserGuide,
                                    modifier = Modifier.testTag("onboarding_open_guide_button")
                                ) {
                                    Text("📖 Panduan", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(
                            text = "1. Atur Bank, E-Wallet & Uang Tunai yang Kamu Pakai (${selectedWallets.size} Aktif):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hapus (🗑️) bank/e-wallet yang tidak kamu miliki, atau tambahkan (+ BRI, Mandiri, BNI, SeaBank, OVO, Tunai, dll) di bawah:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Active Wallets List with Remove button & Rupiah Balance Input
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            selectedWallets.forEach { w ->
                                val typeBadge = when (w.walletType) {
                                    "BANK" -> "🏦 BANK"
                                    "CASH" -> "💵 TUNAI"
                                    else -> "📱 E-WALLET"
                                }
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(0.9.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_wallet_item_${w.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
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
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = EmeraldPrimary.copy(alpha = 0.16f)
                                                ) {
                                                    Text(
                                                        text = typeBadge,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = EmeraldPrimary,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Text(
                                                    text = w.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            if (selectedWallets.size > 1) {
                                                IconButton(
                                                    onClick = {
                                                        selectedWallets.removeAll { it.id == w.id }
                                                        walletBalancesMap.remove(w.id)
                                                    },
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .testTag("onboarding_remove_wallet_${w.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteOutline,
                                                        contentDescription = "Hapus ${w.name}",
                                                        tint = CoralExpense,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                        OutlinedTextField(
                                            value = walletBalancesMap[w.id] ?: "0",
                                            onValueChange = { v -> walletBalancesMap[w.id] = v.filter { c -> c.isDigit() } },
                                            label = { Text("Saldo ${w.name} Saat Ini (Rp)") },
                                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                                            visualTransformation = RupiahThousandsVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("onboarding_balance_input_${w.id}")
                                        )
                                    }
                                }
                            }
                        }

                        // Quick-Add Preset Banks / E-Wallets / Cash
                        val availableToAdd = PresetCategories.availableBankAndWalletPresets.filter { preset ->
                            selectedWallets.none { it.id == preset.id || it.name.equals(preset.name, ignoreCase = true) }
                        }
                        if (availableToAdd.isNotEmpty()) {
                            Text(
                                text = "➕ Tambah Cepat Bank / E-Wallet / Tunai:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                availableToAdd.forEach { preset ->
                                    Surface(
                                        onClick = {
                                            selectedWallets.add(preset)
                                            if (!walletBalancesMap.containsKey(preset.id)) {
                                                walletBalancesMap[preset.id] = "0"
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                                        modifier = Modifier.testTag("onboarding_quick_add_${preset.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = preset.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Custom Bank / E-Wallet Creator
                        OutlinedButton(
                            onClick = { showCustomWalletCreator = !showCustomWalletCreator },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_toggle_custom_wallet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showCustomWalletCreator) "Tutup Form Bank / E-Wallet Kustom" else "Tambah Bank / E-Wallet Lainnya (Bebas Ketik Nama)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        if (showCustomWalletCreator) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Tambah Bank / E-Wallet / Kantong Baru",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(
                                            "BANK" to "🏦 Bank",
                                            "EWALLET" to "📱 E-Wallet",
                                            "CASH" to "💵 Tunai"
                                        ).forEach { (tKey, tLabel) ->
                                            FilterChip(
                                                selected = customWalletType == tKey,
                                                onClick = { customWalletType = tKey },
                                                label = { Text(tLabel, style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = customWalletName,
                                        onValueChange = { customWalletName = it },
                                        label = { Text("Nama Bank / E-Wallet (misal: Bank CIMB, Blu, Flip)") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("onboarding_custom_wallet_name_input")
                                    )
                                    OutlinedTextField(
                                        value = customWalletBalanceStr,
                                        onValueChange = { customWalletBalanceStr = it.filter { c -> c.isDigit() } },
                                        label = { Text("Saldo Awal (Rp)") },
                                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                                        visualTransformation = RupiahThousandsVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("onboarding_custom_wallet_balance_input")
                                    )
                                    Button(
                                        onClick = {
                                            val cleanName = customWalletName.trim()
                                            if (cleanName.isNotBlank()) {
                                                val newId = "wallet_custom_${System.currentTimeMillis()}"
                                                val bal = customWalletBalanceStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
                                                val colorHex = when (customWalletType) {
                                                    "BANK" -> "#0066AE"
                                                    "CASH" -> "#10B981"
                                                    else -> "#118EEA"
                                                }
                                                val iconKey = if (customWalletType == "BANK") "bank_bca" else "ewallet_dana"
                                                val newW = Wallet(
                                                    id = newId,
                                                    userId = "local_preview",
                                                    name = cleanName,
                                                    walletType = customWalletType,
                                                    balance = bal,
                                                    colorHex = colorHex,
                                                    iconKey = iconKey
                                                )
                                                selectedWallets.add(newW)
                                                walletBalancesMap[newId] = bal.toString()
                                                customWalletName = ""
                                                customWalletBalanceStr = ""
                                                showCustomWalletCreator = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("onboarding_add_custom_wallet_confirm")
                                    ) {
                                        Text("Tambahkan ke Daftar Dompetku", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "2. Rekomendasi Limit Fleksibel Otomatis dari Gaji ${ArusKasViewModel.formatRupiah(parsedIncome)}:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldPrimary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "• Tagihan Wajib (Ceklist): ${ArusKasViewModel.formatRupiah(calcBillsTotal)}/bln",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "• Bensin, Makan & Harian (Fleksibel Kapan Saja): ${ArusKasViewModel.formatRupiah(autoLimitHarianBensinMakan)}/bln (~${ArusKasViewModel.formatCompactRupiah(autoLimitHarianBensinMakan / 30)}/hari)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Jajan & Ngopi Fleksibel: ${ArusKasViewModel.formatRupiah(autoLimitJajanNgopi)}/bln",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Rawat Diri (${if (gender == "PEREMPUAN") "Skincare/Kebutuhan" else "Barber/Sabun"}): ${ArusKasViewModel.formatRupiah(autoLimitRawatDiri)}/bln",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "💡 Semua limit & dompet di atas bisa kamu ubah kapan saja di Pengaturan atau Dashboard!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step -= 1 },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Kembali")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step += 1
                            } else {
                                // Build final profile, bills, configured wallets, and limits
                                val paydayClean = (paydayDateStr.toIntOrNull() ?: 25).coerceIn(1, 31)
                                val debtClean = if (hasCicilanOrOtherBill) otherBillAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L else 0L

                                val finalProfile = UserFinancialProfile(
                                    isCompleted = true,
                                    nickname = nickname.trim(),
                                    gender = gender,
                                    livingStatus = livingStatus,
                                    incomeSourcePrimary = incomeSource,
                                    monthlyIncomeAmount = parsedIncome,
                                    paydayDate = paydayClean,
                                    dependentsCount = dependentsCount,
                                    transportMode = transportMode,
                                    monthlyDebtObligation = debtClean,
                                    primaryFinancialGoal = primaryGoal,
                                    dailyHabits = selectedHabits.filterValues { it }.keys.toList(),
                                    controlMode = controlMode,
                                    workShiftType = workShiftType,
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

                                val billsList = mutableListOf<MandatoryBillItem>()
                                if (hasKosanBill) {
                                    val amt = kosanAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 850_000L
                                    val d = (kosanDueDayStr.toIntOrNull() ?: 1).coerceIn(1, 31)
                                    billsList.add(
                                        MandatoryBillItem(
                                            id = "bill_kosan",
                                            name = "Bayar Kosan / Kontrakan",
                                            totalAmount = amt,
                                            paidAmount = 0L,
                                            dueScheduleLabel = "Bayar setiap tanggal $d",
                                            dueDayOfMonth = d,
                                            status = "UNPAID",
                                            billType = "KOSAN"
                                        )
                                    )
                                }
                                if (hasKuotaBill) {
                                    val amt = kuotaAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 100_000L
                                    val expD = (kuotaExpireDayStr.toIntOrNull() ?: 15).coerceIn(1, 31)
                                    val buyD = (kuotaBuyAgainDayStr.toIntOrNull() ?: (expD + 1).coerceAtMost(31)).coerceIn(1, 31)
                                    billsList.add(
                                        MandatoryBillItem(
                                            id = "bill_kuota",
                                            name = "Paket Kuota Internet",
                                            totalAmount = amt,
                                            paidAmount = 0L,
                                            dueScheduleLabel = "Kuota habis tgl $expD • Beli lagi tgl $buyD",
                                            dueDayOfMonth = expD,
                                            renewalDayOfMonth = buyD,
                                            status = "UNPAID",
                                            billType = "KUOTA"
                                        )
                                    )
                                }
                                if (hasListrikWifiBill) {
                                    val amt = listrikAmountStr.filter { it.isDigit() }.toLongOrNull() ?: 250_000L
                                    val d = (listrikDueDayStr.toIntOrNull() ?: 20).coerceIn(1, 31)
                                    billsList.add(
                                        MandatoryBillItem(
                                            id = "bill_listrik",
                                            name = "Token Listrik & WiFi",
                                            totalAmount = amt,
                                            paidAmount = 0L,
                                            dueScheduleLabel = "Jatuh tempo setiap tanggal $d",
                                            dueDayOfMonth = d,
                                            status = "UNPAID",
                                            billType = "LISTRIK"
                                        )
                                    )
                                }
                                if (hasCicilanOrOtherBill && debtClean > 0L) {
                                    val d = (otherBillDueDayStr.toIntOrNull() ?: 5).coerceIn(1, 31)
                                    billsList.add(
                                        MandatoryBillItem(
                                            id = "bill_cicilan",
                                            name = otherBillName.ifBlank { "Cicilan / Pinjaman" },
                                            totalAmount = debtClean,
                                            paidAmount = 0L,
                                            dueScheduleLabel = "Jatuh tempo setiap tanggal $d",
                                            dueDayOfMonth = d,
                                            status = "UNPAID",
                                            billType = "CICILAN"
                                        )
                                    )
                                }

                                val customLimits = mapOf(
                                    "tagihan_bulanan" to CategoryLimitConfig("tagihan_bulanan", calcBillsTotal, "BULANAN"),
                                    "kebutuhan_pokok" to CategoryLimitConfig("kebutuhan_pokok", autoLimitHarianBensinMakan, "BULANAN"),
                                    "rawat_diri" to CategoryLimitConfig("rawat_diri", autoLimitRawatDiri, "BULANAN"),
                                    "gaya_hidup" to CategoryLimitConfig("gaya_hidup", autoLimitJajanNgopi, "BULANAN"),
                                    "sosial_darurat" to CategoryLimitConfig("sosial_darurat", autoLimitSosialOrtu, "BULANAN"),
                                    "modal_produktif" to CategoryLimitConfig("modal_produktif", autoLimitServisKerja, "BULANAN")
                                )

                                val finalWallets = selectedWallets.map { w ->
                                    val bal = walletBalancesMap[w.id]?.filter { it.isDigit() }?.toLongOrNull() ?: w.balance
                                    w.copy(balance = bal)
                                }

                                onCompleteSetup(finalProfile, finalWallets, billsList, customLimits)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("onboarding_next_or_finish_button")
                    ) {
                        Text(
                            text = if (step < 3) "Lanjut Langkah ${step + 1} →" else "Terapkan & Masuk Dashboard 🚀",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingBillSetupCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    amountStr: String,
    onAmountChange: (String) -> Unit,
    scheduleLabel: String,
    dayStr: String,
    onDayChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                FilterChip(
                    selected = checked,
                    onClick = { onCheckedChange(!checked) },
                    label = { Text(if (checked) "Aktif ✓" else "Tidak") }
                )
            }
            if (checked) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = onAmountChange,
                        label = { Text("Nominal (Rp)") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        visualTransformation = RupiahThousandsVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = dayStr,
                        onValueChange = onDayChange,
                        label = { Text(scheduleLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Panel Ceklist Tagihan Wajib (Kosan, Kuota, Listrik, WiFi, Cicilan)
 * Menampilkan:
 * - Nama Tagihan
 * - Jatuh temponya kapan / Kuota habis tanggal berapa & beli lagi tanggal berapa
 * - Status bisa di-ceklist langsung: Belum Lunas | Baru Setengah (50%) | Sudah Lunas ✅
 * - Bar Batas Limit Tagihan & Bar Tagihan Terpakai/Terbayar
 */
@Composable
fun MandatoryBillsChecklistSection(
    bills: List<MandatoryBillItem>,
    selectedWallet: Wallet?,
    onChangeBillStatus: (bill: MandatoryBillItem, newStatus: String) -> Unit,
    onAddBillClick: () -> Unit,
    onEditBillClick: (MandatoryBillItem) -> Unit,
    onDeleteBillClick: (MandatoryBillItem) -> Unit
) {
    val totalBillsLimit = remember(bills) { bills.sumOf { it.totalAmount } }
    val totalPaid = remember(bills) { bills.sumOf { it.paidAmount } }
    val paidRatio = if (totalBillsLimit > 0L) (totalPaid.toFloat() / totalBillsLimit.toFloat()).coerceIn(0f, 1f) else 0f
    val paidCount = remember(bills) { bills.count { it.status == "PAID" } }
    val halfCount = remember(bills) { bills.count { it.status == "HALF_PAID" } }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = glassmorphicBorderStroke(accentColor = SkyBlue, width = 1.2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCardEffect(cornerRadius = 20.dp, accentTint = SkyBlue)
            .testTag("mandatory_bills_checklist_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header + Tombol Tambah Tagihan + Simbol Panduan Melayang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Daftar Tagihan Wajib",
                            tint = SkyBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CEKLIST TAGIHAN WAJIB & JATUH TEMPO",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SkyBlue
                        )
                        FeatureHelpButton(
                            content = FeatureHelpDatabase.TAGIHAN_WAJIB,
                            iconSize = 13,
                            tint = SkyBlue,
                            testTag = "help_mandatory_bills_checklist"
                        )
                    }
                    Text(
                        text = "Lunas: $paidCount/${bills.size} tagihan" +
                            (if (halfCount > 0) " • $halfCount baru setengah" else "") +
                            (if (selectedWallet != null) " • Potong: ${selectedWallet.name}" else ""),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = onAddBillClick,
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("add_mandatory_bill_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Tagihan",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Tagihan",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dual Bar Summary: Bar Batas Total Tagihan vs Bar Sudah Dibayar
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bar Terbayar: ${ArusKasViewModel.formatRupiah(totalPaid)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Batas Tagihan: ${ArusKasViewModel.formatRupiah(totalBillsLimit)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    // Visual Bar Limit & Bar Terpakai
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(paidRatio)
                                .height(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    when {
                                        paidRatio >= 1f -> EmeraldPrimary
                                        paidRatio >= 0.5f -> GoldAccent
                                        else -> SkyBlue
                                    }
                                )
                        )
                    }
                }
            }

            // List Item Tagihan Wajib
            bills.forEach { bill ->
                val statusColor = when (bill.status) {
                    "PAID" -> EmeraldPrimary
                    "HALF_PAID" -> GoldAccent
                    else -> CoralExpense
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.08f),
                    border = BorderStroke(0.9.dp, statusColor.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandatory_bill_row_${bill.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Checkbox Icon (Toggle Unpaid <-> Paid directly)
                                Icon(
                                    imageVector = if (bill.status == "PAID") Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = "Ceklist ${bill.name}",
                                    tint = statusColor,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            val next = if (bill.status == "PAID") "UNPAID" else "PAID"
                                            onChangeBillStatus(bill, next)
                                        }
                                        .testTag("checkbox_bill_${bill.id}")
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bill.name,
                                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (bill.status == "PAID") TextDecoration.LineThrough else TextDecoration.None,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    // Info Jatuh Tempo / Habis Tanggal Sekian Beli Lagi Tanggal Sekian
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = bill.dueScheduleLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.2.sp),
                                            color = GoldAccent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Nominal & Edit/Delete
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = ArusKasViewModel.formatRupiah(bill.totalAmount),
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = statusColor
                                    )
                                    if (bill.status == "HALF_PAID") {
                                        Text(
                                            text = "Sisa ${ArusKasViewModel.formatCompactRupiah(bill.remainingAmount)}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.2.sp),
                                            color = GoldAccent
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onEditBillClick(bill) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Ubah ${bill.name}",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteBillClick(bill) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Hapus ${bill.name}",
                                        tint = CoralExpense.copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // 3-Status Selector Bar (Belum Lunas | Baru Setengah | Sudah Lunas ✅)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "UNPAID" to "Belum Lunas",
                                "HALF_PAID" to "Baru Setengah (50%)",
                                "PAID" to "Sudah Lunas ✅"
                            ).forEach { (statusKey, statusText) ->
                                val isCurrent = bill.status == statusKey
                                val chipColor = when (statusKey) {
                                    "PAID" -> EmeraldPrimary
                                    "HALF_PAID" -> GoldAccent
                                    else -> CoralExpense
                                }
                                Surface(
                                    onClick = {
                                        if (!isCurrent) {
                                            onChangeBillStatus(bill, statusKey)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCurrent) chipColor.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (isCurrent) 1.2.dp else 0.7.dp,
                                        color = if (isCurrent) chipColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("bill_status_${bill.id}_$statusKey")
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = statusText,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                            color = if (isCurrent) chipColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog Tambah / Edit Item Tagihan Wajib
 * Mendukung pengaturan:
 * 1. Bayar rutin setiap tanggal X (misal: Bayar kosan setiap tanggal 1)
 * 2. Habis tanggal X, harus beli lagi tanggal Y (misal: Kuota habis tgl 15, beli lagi tgl 16)
 */
@Composable
fun MandatoryBillEditorDialog(
    initialBill: MandatoryBillItem?,
    onDismiss: () -> Unit,
    onSave: (
        billId: String?,
        name: String,
        totalAmount: Long,
        dueScheduleLabel: String,
        dueDay: Int,
        renewalDay: Int?,
        billType: String
    ) -> Unit
) {
    var name by remember(initialBill) { mutableStateOf(initialBill?.name ?: "") }
    var amountStr by remember(initialBill) { mutableStateOf(initialBill?.totalAmount?.toString() ?: "") }
    var scheduleType by remember(initialBill) {
        mutableStateOf(if (initialBill?.renewalDayOfMonth != null || initialBill?.billType == "KUOTA") "HABIS_BELI_LAGI" else "TANGGAL_TETAP")
    }
    var dueDayStr by remember(initialBill) { mutableStateOf((initialBill?.dueDayOfMonth ?: 1).toString()) }
    var renewalDayStr by remember(initialBill) { mutableStateOf((initialBill?.renewalDayOfMonth ?: 16).toString()) }
    var customScheduleNote by remember(initialBill) { mutableStateOf(initialBill?.dueScheduleLabel ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialBill == null) "Tambah Tagihan Wajib" else "Atur Tagihan: ${initialBill.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Tagihan (mis: Bayar Kosan, Kuota, Listrik)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bill_editor_name_input")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Tagihan (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bill_editor_amount_input")
                )

                Text(
                    text = "Pilih Pola Jatuh Tempo:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = scheduleType == "TANGGAL_TETAP",
                        onClick = { scheduleType = "TANGGAL_TETAP" },
                        label = { Text("Setiap Tanggal X (Kosan/WiFi)", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = scheduleType == "HABIS_BELI_LAGI",
                        onClick = { scheduleType = "HABIS_BELI_LAGI" },
                        label = { Text("Habis Tgl X • Beli Lagi Tgl Y", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                if (scheduleType == "TANGGAL_TETAP") {
                    OutlinedTextField(
                        value = dueDayStr,
                        onValueChange = {
                            dueDayStr = it.filter { c -> c.isDigit() }.take(2)
                            val d = dueDayStr.toIntOrNull() ?: 1
                            customScheduleNote = "Bayar setiap tanggal $d"
                        },
                        label = { Text("Jatuh Tempo Setiap Tanggal (1 - 31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dueDayStr,
                            onValueChange = {
                                dueDayStr = it.filter { c -> c.isDigit() }.take(2)
                                val exp = dueDayStr.toIntOrNull() ?: 15
                                val buy = renewalDayStr.toIntOrNull() ?: (exp + 1)
                                customScheduleNote = "Habis tgl $exp • Beli lagi tgl $buy"
                            },
                            label = { Text("Habis Tanggal (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = renewalDayStr,
                            onValueChange = {
                                renewalDayStr = it.filter { c -> c.isDigit() }.take(2)
                                val exp = dueDayStr.toIntOrNull() ?: 15
                                val buy = renewalDayStr.toIntOrNull() ?: 16
                                customScheduleNote = "Habis tgl $exp • Beli lagi tgl $buy"
                            },
                            label = { Text("Harus Beli Lagi Tgl") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = customScheduleNote,
                    onValueChange = { customScheduleNote = it },
                    label = { Text("Keterangan Jadwal (Otomatis / Bebas Ubah)") },
                    placeholder = { Text("Cth: Bayar setiap tanggal 1 / Kuota habis tgl 15 beli lagi tgl 16") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
                    val d = (dueDayStr.toIntOrNull() ?: 1).coerceIn(1, 31)
                    val ren = if (scheduleType == "HABIS_BELI_LAGI") (renewalDayStr.toIntOrNull() ?: d).coerceIn(1, 31) else null
                    val autoLabel = customScheduleNote.ifBlank {
                        if (ren != null) "Habis tgl $d • Beli lagi tgl $ren" else "Bayar setiap tanggal $d"
                    }
                    if (name.isNotBlank() && amt > 0L) {
                        onSave(
                            initialBill?.id,
                            name,
                            amt,
                            autoLabel,
                            d,
                            ren,
                            if (ren != null) "KUOTA" else "KOSAN"
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("save_mandatory_bill_button")
            ) {
                Text("Simpan Tagihan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
