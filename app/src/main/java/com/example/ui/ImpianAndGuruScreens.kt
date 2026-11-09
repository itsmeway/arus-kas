package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SavingsGoal
import com.example.data.TransactionRecord
import com.example.data.Wallet
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import kotlin.math.ceil

@Composable
fun ImpianSavingsScreen(
    goals: List<SavingsGoal>,
    wallets: List<Wallet>,
    onBackToHome: () -> Unit,
    onAddGoal: (title: String, targetAmount: Long, dailyRec: Long, note: String, iconKey: String) -> Unit,
    onAllocateSavings: (goal: SavingsGoal, wallet: Wallet, amount: Long, note: String) -> Unit,
    onDeleteGoal: (SavingsGoal) -> Unit
) {
    BackHandler { onBackToHome() }

    var showCreateGoal by remember { mutableStateOf(false) }
    var activeAllocatingGoal by remember { mutableStateOf<SavingsGoal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tabungan Target Terpisah",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Otomatis memotong Saldo Siap Pakai • Simpan di tempat terpisah!",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent
                )
            }
            Button(
                onClick = { showCreateGoal = !showCreateGoal },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Color(0xFF231700)
                ),
                modifier = Modifier.testTag("add_goal_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showCreateGoal) "Tutup" else "Target Baru", fontWeight = FontWeight.Bold)
            }
        }

        AnimatedVisibility(visible = showCreateGoal) {
            CreateGoalFormCard(
                onCancel = { showCreateGoal = false },
                onSave = { title, target, daily, note ->
                    onAddGoal(title, target, daily, note, "laptop")
                    showCreateGoal = false
                }
            )
        }

        if (goals.isEmpty() && !showCreateGoal) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LaptopMac,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "Belum Ada Tabungan Target",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tambahkan Tabungan Target seperti 'Beli Laptop Kerja' lengkap dengan target setoran harian dan simpan uangnya di tempat/rekening terpisah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(goals, key = { it.id }) { goal ->
                val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
                val progress = (goal.savedAmount.toFloat() / goal.targetAmount.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                val estimatedDays = if (goal.dailyRecommendation > 0 && remaining > 0) {
                    ceil(remaining.toDouble() / goal.dailyRecommendation.toDouble()).toInt()
                } else 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldAccent.copy(alpha = 0.2f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.LaptopMac,
                                            contentDescription = goal.title,
                                            tint = GoldAccent
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = goal.note.ifBlank { "Target impian terpantau" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteGoal(goal) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Hapus impian",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            color = EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Terkumpul (${(progress * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ArusKasViewModel.formatRupiah(goal.savedAmount),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Target Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ArusKasViewModel.formatRupiah(goal.targetAmount),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Calculation Note Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (remaining == 0L) "IMPIAN TERCAPAI! 🎉"
                                        else "Kurang: ${ArusKasViewModel.formatRupiah(remaining)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (remaining == 0L) "Saldo tabungan sudah cukup untuk eksekusi!"
                                        else "Nabung ${ArusKasViewModel.formatRupiah(goal.dailyRecommendation)}/hari ≈ $estimatedDays hari lagi lunas",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (remaining > 0L && wallets.isNotEmpty()) {
                                    Button(
                                        onClick = { activeAllocatingGoal = goal },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldPrimary,
                                            contentColor = Color(0xFF042619)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Setor", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (activeAllocatingGoal != null) {
        AllocateGoalDialog(
            goal = activeAllocatingGoal!!,
            wallets = wallets,
            onDismiss = { activeAllocatingGoal = null },
            onConfirm = { wallet, amount, note ->
                onAllocateSavings(activeAllocatingGoal!!, wallet, amount, note)
                activeAllocatingGoal = null
            }
        )
    }
}

@Composable
private fun CreateGoalFormCard(
    onCancel: () -> Unit,
    onSave: (title: String, target: Long, daily: Long, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var dailyStr by remember { mutableStateOf("40000") }
    var note by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tambah Impian / Target Tabungan Baru",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Impian (mis: Beli Laptop Gaming / Kerja)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Harga (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = dailyStr,
                    onValueChange = { dailyStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Sanggup Nabung/Hari") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan Perhitungan / Sumber Dana") },
                placeholder = { Text("Misal: Dari ngojek 25rb + usaha 15rb tiap hari") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Batal") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val t = targetStr.toLongOrNull() ?: 0L
                        val d = dailyStr.toLongOrNull() ?: 25000L
                        if (title.isNotBlank() && t > 0L) {
                            onSave(title, t, d, note)
                        }
                    }
                ) {
                    Text("Simpan Target")
                }
            }
        }
    }
}

@Composable
private fun AllocateGoalDialog(
    goal: SavingsGoal,
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (wallet: Wallet, amount: Long, note: String) -> Unit
) {
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var amountStr by remember { mutableStateOf(goal.dailyRecommendation.coerceAtLeast(25000L).toString()) }
    var noteStr by remember { mutableStateOf("Sisihkan buat ${goal.title}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Setor Tabungan: ${goal.title}", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pilih saldo dompet mana yang mau diamankan ke tabungan impian ini:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    wallets.forEach { w ->
                        val isSel = w.id == selectedWalletId
                        Surface(
                            onClick = { selectedWalletId = w.id },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) EmeraldPrimary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(w.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(ArusKasViewModel.formatCompactRupiah(w.balance), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Setor (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    label = { Text("Catatan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()
                    val amt = amountStr.toLongOrNull() ?: 0L
                    if (w != null && amt > 0L) {
                        onConfirm(w, amt, noteStr)
                    }
                }
            ) {
                Text("Amankan Sekarang")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun GuruFinancialMentorScreen(
    wallets: List<Wallet>,
    transactions: List<TransactionRecord>,
    goals: List<SavingsGoal>,
    guruState: GuruAdviceState,
    isSignedIn: Boolean = false,
    onOpenSignInSheet: () -> Unit = {},
    onBackToHome: () -> Unit,
    onRequestEvaluation: () -> Unit,
    onSignOut: () -> Unit
) {
    BackHandler { onBackToHome() }

    LaunchedEffect(Unit) {
        if (guruState is GuruAdviceState.Idle && (wallets.isNotEmpty() || transactions.isNotEmpty())) {
            onRequestEvaluation()
        }
    }

    val monthStart = remember { ArusKasViewModel.getStartOfMonthMillis() }
    val mendadakTotal = remember(transactions, monthStart) {
        transactions.filter { it.type == "EXPENSE" && it.frequencyTag == "MENDADAK" && it.transactionDateMillis >= monthStart }
            .sumOf { it.amount }
    }
    val totalExpenseMonth = remember(transactions, monthStart) {
        transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= monthStart }
            .sumOf { it.amount }
    }
    val totalIncomeMonth = remember(transactions, monthStart) {
        transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= monthStart }
            .sumOf { it.amount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(2.dp, GoldAccent),
                    modifier = Modifier.size(56.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_guru_avatar_1794085706193),
                        contentDescription = "Guru ArusKas",
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = "Guru Penegur & Motivator",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Evaluasi jujur kondisi duit & progres impianmu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSignedIn) {
                IconButton(onClick = onSignOut) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Keluar Akun",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onOpenSignInSheet,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Masuk Cloud", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Real-time Automatic Health Check Card (Always instant, zero latency)
        val mendadakRatio = if (totalExpenseMonth > 0) (mendadakTotal * 100 / totalExpenseMonth) else 0
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(
                1.dp,
                if (mendadakRatio > 30) CoralExpense else EmeraldPrimary.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
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
                    Text(
                        text = "RADAR KEBOCORAN BULAN INI",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (mendadakRatio > 30) CoralExpense else EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$mendadakRatio% dari total keluar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Pengeluaran Tak Terduga (Jajan / Ada Tamu / Mendadak): ${ArusKasViewModel.formatRupiah(mendadakTotal)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (mendadakRatio > 30) {
                        "Awas! Lebih dari 30% uang keluar dipakai buat jajan/tamu/mendadak. Kalau diterusin, tabungan laptopmu bakal mundur jauh!"
                    } else {
                        "Bagus! Pengeluaran tak terduga masih terkendali. Pertahankan supaya saldo BCA, Bank Mas & e-wallet tetap aman."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // AI Guru Deep Analysis Card (Powered by Firebase AI Logic Gemini 3.8 Flash)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "NASIHAT GURU ARUSKAS",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onRequestEvaluation,
                        enabled = guruState !is GuruAdviceState.Loading,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("ask_guru_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Minta Evaluasi Baru", style = MaterialTheme.typography.labelMedium)
                    }
                }

                when (guruState) {
                    is GuruAdviceState.Idle -> {
                        Text(
                            text = "Ketuk 'Minta Evaluasi Baru' agar Guru memeriksa seluruh saldo BCA, Bank Mas, DANA, ShopeePay, GoPay, riwayat pengeluaran, dan target impianmu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is GuruAdviceState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = GoldAccent,
                                strokeWidth = 2.5.dp
                            )
                            Text(
                                text = "Guru sedang memeriksa aliran keluar-masuk uangmu...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is GuruAdviceState.Success -> {
                        Surface(
                            color = GoldAccent.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = guruState.statusBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Teguran Jujur:",
                                style = MaterialTheme.typography.labelLarge,
                                color = CoralExpense,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = guruState.teguran,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Motivasi Impian:",
                                style = MaterialTheme.typography.labelLarge,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = guruState.motivasi,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Langkah Konkret Hari Ini:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = guruState.langkahKonkret,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    is GuruAdviceState.Error -> {
                        Surface(
                            color = CoralExpense.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CoralExpense)
                        ) {
                            Text(
                                text = guruState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFECDD3),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
