package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.GoogleSignInButton
import com.example.data.PresetCategories
import com.example.data.SavingsGoal
import com.example.data.Wallet
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

fun parseColorSafely(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        EmeraldPrimary
    }
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1411),
                        Color(0xFF0D261D),
                        Color(0xFF0A1411)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(2.dp, GoldAccent)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_guru_avatar_1794085706193),
                    contentDescription = "Mentor Guru ArusKas",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = GoldAccent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "FINANCIAL COCKPIT PRIBADI",
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ArusKas",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pantau penuh setiap rupiah di BCA, Bank Mas, DANA, ShopeePay & GoPay. Catat pengeluaran secepat kilat, kejar impian laptopmu, dan dapatkan teguran jujur dari Guru Finansial.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureRowItem(
                        title = "Kotak 3x3 Pengeluaran Cepat",
                        subtitle = "Satu layar langsung potong saldo dari dompet pilihan"
                    )
                    FeatureRowItem(
                        title = "Semua Dompet Terpantau",
                        subtitle = "Bank BCA, Bank Mas, DANA, ShopeePay, GoPay & Tunai"
                    )
                    FeatureRowItem(
                        title = "Target Impian & Guru Penegur",
                        subtitle = "Hitung otomatis tabungan laptop & teguran saat jajan bocor"
                    )
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = CoralExpense.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CoralExpense)
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFECDD3),
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            GoogleSignInButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                onAuthSuccess = onAuthSuccess,
                onAuthError = { errorMessage = it }
            )
        }
    }
}

@Composable
private fun FeatureRowItem(title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = EmeraldPrimary.copy(alpha = 0.2f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalBottomSheet(
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(2.dp, GoldAccent)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_guru_avatar_1794085706193),
                    contentDescription = "Mentor Guru ArusKas",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Cadangkan & Sinkronkan ArusKas",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Masuk dengan akun Google agar semua catatan saldo BCA, Bank Mas, DANA, ShopeePay, GoPay, dan progres tabungan laptopmu tersimpan aman di Cloud.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (errorMessage != null) {
                Surface(
                    color = CoralExpense.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CoralExpense)
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFECDD3),
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            GoogleSignInButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onAuthSuccess = onAuthSuccess,
                onAuthError = { errorMessage = it }
            )

            TextButton(onClick = onDismiss) {
                Text("Lanjut Pakai Dashboard Tanpa Masuk")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WalletsBreakdownBottomSheet(
    wallets: List<Wallet>,
    totalTabunganSaved: Long = 0L,
    emergencyBalance: Long = 0L,
    onDismiss: () -> Unit,
    onSaveWallet: (walletId: String?, name: String, type: String, balance: Long, colorHex: String) -> Unit,
    onDeleteWallet: (Wallet) -> Unit,
    onTransferWallet: (fromWallet: Wallet, toWallet: Wallet, amount: Long, adminFee: Long, note: String) -> Unit = { _, _, _, _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editingWallet by remember { mutableStateOf<Wallet?>(null) }
    var showAddForm by remember { mutableStateOf(false) }
    var showTransferForm by remember { mutableStateOf(false) }

    val activeBalance = wallets.sumOf { it.balance }
    val grandTotalInPocket = activeBalance + totalTabunganSaved + emergencyBalance

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Semua Duit di Kantongmu",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Total Gabungan Utuh: ${ArusKasViewModel.formatRupiah(grandTotalInPocket)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (wallets.size >= 2) {
                        OutlinedButton(
                            onClick = {
                                showTransferForm = !showTransferForm
                                showAddForm = false
                                editingWallet = null
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("transfer_wallet_sheet_button")
                        ) {
                            Text(if (showTransferForm) "Tutup" else "⇄ Pindah Saldo", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Button(
                        onClick = {
                            editingWallet = null
                            showTransferForm = false
                            showAddForm = !showAddForm
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_wallet_sheet_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAddForm) "Tutup" else "Dompet", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Summary of 3 Separated Pockets so user knows where every Rupiah is
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1. Saldo Siap Pakai (${wallets.size} Dompet Harian)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = ArusKasViewModel.formatRupiah(activeBalance),
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "2. Tabungan Target (Simpan Terpisah)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = ArusKasViewModel.formatRupiah(totalTabunganSaved),
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "3. Simpanan Darurat (Simpan Terpisah)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = ArusKasViewModel.formatRupiah(emergencyBalance),
                            style = MaterialTheme.typography.labelMedium,
                            color = CoralExpense,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "💡 Top-up E-Wallet atau Tarik Tunai gunakan tombol '⇄ Pindah Saldo' agar pengeluaran bulananmu tidak bengkak palsu.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = showTransferForm && wallets.size >= 2) {
                WalletTransferForm(
                    wallets = wallets,
                    onCancel = { showTransferForm = false },
                    onConfirmTransfer = { fromW, toW, amt, fee, note ->
                        onTransferWallet(fromW, toW, amt, fee, note)
                        showTransferForm = false
                    }
                )
            }

            AnimatedVisibility(visible = showAddForm || editingWallet != null) {
                WalletEditorForm(
                    initialWallet = editingWallet,
                    existingWallets = wallets,
                    onCancel = {
                        showAddForm = false
                        editingWallet = null
                    },
                    onSave = { id, name, type, balance, colorHex ->
                        onSaveWallet(id, name, type, balance, colorHex)
                        showAddForm = false
                        editingWallet = null
                    }
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(320.dp)
            ) {
                items(wallets, key = { it.id }) { wallet ->
                    val brandColor = parseColorSafely(wallet.colorHex)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        ),
                        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = brandColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (wallet.walletType == "BANK") Icons.Default.AccountBalance else Icons.Default.AccountBalanceWallet,
                                            contentDescription = wallet.name,
                                            tint = brandColor
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = wallet.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when (wallet.walletType) {
                                            "BANK" -> "Rekening Bank"
                                            "CASH" -> "Uang Tunai (Cash)"
                                            else -> "E-Wallet / Dompet Digital"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ArusKasViewModel.formatRupiah(wallet.balance),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (wallet.balance < 0) CoralExpense else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        editingWallet = wallet
                                        showAddForm = false
                                        showTransferForm = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Ubah saldo ${wallet.name}",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (wallets.size > 1) {
                                    IconButton(
                                        onClick = { onDeleteWallet(wallet) },
                                        modifier = Modifier.testTag("delete_wallet_button_${wallet.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus ${wallet.name}",
                                            tint = CoralExpense.copy(alpha = 0.8f),
                                            modifier = Modifier.size(18.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WalletTransferForm(
    wallets: List<Wallet>,
    onCancel: () -> Unit,
    onConfirmTransfer: (fromWallet: Wallet, toWallet: Wallet, amount: Long, adminFee: Long, note: String) -> Unit
) {
    var fromWalletId by remember(wallets) { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var toWalletId by remember(wallets) {
        mutableStateOf(wallets.getOrNull(1)?.id ?: wallets.firstOrNull()?.id ?: "")
    }
    var amountStr by remember { mutableStateOf("") }
    var adminFeeStr by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⇄ Pindah Saldo / Top-Up E-Wallet / Tarik Tunai",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
            Text(
                text = "Pilih Dompet Asal (Sumber Dana):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                wallets.forEach { w ->
                    FilterChip(
                        selected = fromWalletId == w.id,
                        onClick = {
                            fromWalletId = w.id
                            if (toWalletId == w.id) {
                                toWalletId = wallets.firstOrNull { it.id != w.id }?.id ?: w.id
                            }
                        },
                        label = { Text("${w.name} (${ArusKasViewModel.formatCompactRupiah(w.balance)})", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Text(
                text = "Pilih Dompet Tujuan (Penerima):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                wallets.filter { it.id != fromWalletId }.forEach { w ->
                    FilterChip(
                        selected = toWalletId == w.id,
                        onClick = { toWalletId = w.id },
                        label = { Text(w.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Pindah (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("transfer_amount_input")
                )
                OutlinedTextField(
                    value = adminFeeStr,
                    onValueChange = { adminFeeStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Biaya Admin") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("transfer_admin_fee_input")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text("Batal")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val fromW = wallets.find { it.id == fromWalletId }
                        val toW = wallets.find { it.id == toWalletId }
                        val amt = amountStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
                        val fee = adminFeeStr.filter { it.isDigit() }.toLongOrNull() ?: 0L
                        if (fromW != null && toW != null && fromW.id != toW.id && amt > 0L) {
                            onConfirmTransfer(fromW, toW, amt, fee, note)
                        }
                    },
                    modifier = Modifier.testTag("confirm_transfer_wallet_button")
                ) {
                    Text("Pindahkan Saldo")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WalletEditorForm(
    initialWallet: Wallet?,
    existingWallets: List<Wallet> = emptyList(),
    onCancel: () -> Unit,
    onSave: (id: String?, name: String, type: String, balance: Long, colorHex: String) -> Unit
) {
    var name by remember(initialWallet) { mutableStateOf(initialWallet?.name ?: "") }
    var balanceStr by remember(initialWallet) { mutableStateOf(initialWallet?.balance?.toString() ?: "") }
    var walletType by remember(initialWallet) { mutableStateOf(initialWallet?.walletType ?: "EWALLET") }
    var colorHex by remember(initialWallet) { mutableStateOf(initialWallet?.colorHex ?: "#10B981") }

    val colorChoices = listOf("#0066AE", "#F59E0B", "#118EEA", "#EE4D2D", "#00AED6", "#10B981")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (initialWallet == null) "Tambah Dompet / Bank / E-Wallet Baru" else "Sesuaikan Saldo ${initialWallet.name}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (initialWallet == null) {
                val quickSuggestions = PresetCategories.availableBankAndWalletPresets.filter { preset ->
                    existingWallets.none { it.name.equals(preset.name, ignoreCase = true) }
                }.take(8)
                if (quickSuggestions.isNotEmpty()) {
                    Text(
                        text = "Pilih Cepat Bank / E-Wallet:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        quickSuggestions.forEach { preset ->
                            FilterChip(
                                selected = name.equals(preset.name, ignoreCase = true),
                                onClick = {
                                    name = preset.name
                                    walletType = preset.walletType
                                    colorHex = preset.colorHex
                                },
                                label = { Text("+ ${preset.name}", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama (mis: BCA, BRI, Mandiri, DANA, GoPay, Tunai)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = balanceStr,
                onValueChange = { input -> balanceStr = input.filter { it.isDigit() || it == '-' } },
                label = { Text("Saldo Saat Ini (Rp)") },
                prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                visualTransformation = RupiahThousandsVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = walletType == "BANK",
                    onClick = { walletType = "BANK" },
                    label = { Text("Bank") }
                )
                FilterChip(
                    selected = walletType == "EWALLET",
                    onClick = { walletType = "EWALLET" },
                    label = { Text("E-Wallet") }
                )
                FilterChip(
                    selected = walletType == "CASH",
                    onClick = { walletType = "CASH" },
                    label = { Text("Tunai") }
                )
                Spacer(modifier = Modifier.weight(1f))
                colorChoices.forEach { hex ->
                    val c = parseColorSafely(hex)
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(c)
                            .clickable { colorHex = hex }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text("Batal")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val cleanBalance = balanceStr.toLongOrNull() ?: 0L
                        if (name.isNotBlank()) {
                            onSave(initialWallet?.id, name.trim(), walletType, cleanBalance, colorHex)
                        }
                    }
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Composable
fun FirstTimeSetupDialog(
    onDismiss: () -> Unit,
    onConfirmSetup: (
        walletBalances: Map<String, Long>,
        goalTitle: String,
        goalTarget: Long,
        goalDaily: Long,
        goalNote: String
    ) -> Unit
) {
    val balances = remember {
        mutableStateMapOf(
            "wallet_bca" to "3500000",
            "wallet_mas" to "1500000",
            "wallet_dana" to "350000",
            "wallet_shopeepay" to "250000",
            "wallet_gopay" to "450000"
        )
    }
    var goalTitle by remember { mutableStateOf("Beli Laptop Impian") }
    var goalTarget by remember { mutableStateOf("10000000") }
    var goalDaily by remember { mutableStateOf("40000") }
    var goalNote by remember { mutableStateOf("Sisihkan dari gaji, ngojek online & usaha") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Atur Saldo Awal & Impianmu",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Sesuaikan saldo di BCA, Bank Mas, DANA, ShopeePay & GoPay agar langsung terpantau akurat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetCategories.defaultStarterWallets.forEach { preset ->
                    OutlinedTextField(
                        value = balances[preset.id] ?: "0",
                        onValueChange = { v -> balances[preset.id] = v.filter { it.isDigit() } },
                        label = { Text("Saldo ${preset.name} (Rp)") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        visualTransformation = RupiahThousandsVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                Text(
                    text = "Target Tabungan Utama",
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text("Nama Impian (mis: Beli Laptop)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goalTarget,
                        onValueChange = { goalTarget = it.filter { c -> c.isDigit() } },
                        label = { Text("Target (Rp)") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        visualTransformation = RupiahThousandsVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = goalDaily,
                        onValueChange = { goalDaily = it.filter { c -> c.isDigit() } },
                        label = { Text("Nabung/Hari") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        visualTransformation = RupiahThousandsVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = goalNote,
                    onValueChange = { goalNote = it },
                    label = { Text("Catatan Rencana Nabung") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedMap = balances.mapValues { it.value.toLongOrNull() ?: 0L }
                    onConfirmSetup(
                        parsedMap,
                        goalTitle.ifBlank { "Beli Laptop Impian" },
                        goalTarget.toLongOrNull() ?: 10_000_000L,
                        goalDaily.toLongOrNull() ?: 40_000L,
                        goalNote
                    )
                },
                modifier = Modifier.testTag("confirm_starter_setup_button")
            ) {
                Text("Mulai Pantau Uangku")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Nanti Saja")
            }
        }
    )
}
