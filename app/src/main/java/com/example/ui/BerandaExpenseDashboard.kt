package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import com.example.R
import com.example.data.AppLanguage
import com.example.data.AppLocalization
import com.example.data.AppSettings
import com.example.data.CategoryLimitConfig
import com.example.data.CurrencyFormatOption
import com.example.data.EmergencyFundConfig
import com.example.data.GenZQuotesDatabase
import com.example.data.MandatoryBillItem
import com.example.data.PresetCategories
import com.example.data.QuickCategoryItem
import com.example.data.SavingsGoal
import com.example.data.TransactionRecord
import com.example.data.UserFinancialProfile
import com.example.data.Wallet
import com.example.ui.theme.BcaCobaltBlue
import com.example.ui.theme.BcaElectricCyan
import com.example.ui.theme.BcaRoyalBlue
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

fun iconForCategory(key: String): ImageVector {
    return when (key) {
        "pakai_darurat" -> Icons.Default.HealthAndSafety
        "tagihan_bulanan", "listrik_kos", "kuota_pulsa" -> Icons.Default.ElectricBolt
        "kebutuhan_pokok", "belanja_dapur", "makan_harian", "bensin_ojek" -> Icons.Default.ShoppingBag
        "rawat_diri" -> Icons.Default.Spa
        "gaya_hidup", "jajan_cemilan" -> Icons.Default.Favorite
        "sosial_darurat", "ada_tamu", "darurat_lain" -> Icons.Default.Groups
        "modal_produktif", "modal_usaha" -> Icons.Default.Storefront
        "ngojek_online" -> Icons.Default.DirectionsBike
        else -> Icons.Default.Bolt
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BerandaExpenseDashboard(
    wallets: List<Wallet>,
    transactions: List<TransactionRecord>,
    goals: List<SavingsGoal> = emptyList(),
    isSubmitting: Boolean,
    guruState: GuruAdviceState = GuruAdviceState.Idle,
    sessionSeed: Int = 1,
    onRefreshBestieAnalysis: () -> Unit = {},
    categoryLimits: Map<String, CategoryLimitConfig> = PresetCategories.defaultCategoryLimits,
    onUpdateCategoryLimit: (categoryKey: String, categoryLabel: String, limitAmount: Long, period: String) -> Unit = { _, _, _, _ -> },
    mandatoryBills: List<MandatoryBillItem> = PresetCategories.defaultMandatoryBills,
    onUpdateMandatoryBillStatus: (bill: MandatoryBillItem, newStatus: String, wallet: Wallet?) -> Unit = { _, _, _ -> },
    onUpsertMandatoryBill: (billId: String?, name: String, totalAmount: Long, dueScheduleLabel: String, dueDay: Int, renewalDay: Int?, billType: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteMandatoryBill: (String) -> Unit = {},
    userProfile: UserFinancialProfile = UserFinancialProfile(),
    onOpenFinancialProfileWizard: () -> Unit = {},
    emergencyFund: EmergencyFundConfig = EmergencyFundConfig(),
    onUpdateEmergencyFund: (sourceWallet: Wallet?, addedAmount: Long, newLimit: Long, period: String, storageNote: String) -> Unit = { _, _, _, _, _ -> },
    onAddGoal: (title: String, targetAmount: Long, dailyRec: Long, note: String, iconKey: String) -> Unit = { _, _, _, _, _ -> },
    onAllocateSavings: (goal: SavingsGoal, wallet: Wallet, amount: Long, note: String) -> Unit = { _, _, _, _ -> },
    onDeleteGoal: (SavingsGoal) -> Unit = {},
    onOpenWalletsSheet: () -> Unit,
    onOpenStarterSetup: () -> Unit,
    onOpenUserGuide: () -> Unit = {},
    onOpenPaywall: () -> Unit = {},
    onNavigateToGuru: () -> Unit = {},
    appSettings: AppSettings = AppSettings(),
    onOpenSettings: () -> Unit = {},
    postActivityReminder: ArusKasViewModel.PostActivityReminderEvent? = null,
    onDismissPostActivityReminder: () -> Unit = {},
    onOpenRoutineSettings: () -> Unit = {},
    onRecordTransaction: (
        wallet: Wallet,
        type: String,
        amount: Long,
        categoryKey: String,
        categoryLabel: String,
        frequencyTag: String,
        note: String
    ) -> Unit,
    onDeleteTransaction: (TransactionRecord) -> Unit
) {
    val lang = appSettings.language
    val hideBalance = appSettings.hideNominalOnDashboard
    val isCompactFormat = appSettings.currencyFormat == CurrencyFormatOption.COMPACT

    fun formatMoneyDisplay(amount: Long): String {
        return when {
            hideBalance -> "Rp ••••••"
            isCompactFormat -> ArusKasViewModel.formatCompactRupiah(amount)
            else -> ArusKasViewModel.formatRupiah(amount)
        }
    }

    val totalBalance = remember(wallets) { wallets.sumOf { it.balance } }
    val totalTabunganSaved = remember(goals) { goals.sumOf { it.savedAmount } }
    val totalSemuaDuitDiKantong = remember(totalBalance, totalTabunganSaved, emergencyFund.emergencyBalance) {
        totalBalance + totalTabunganSaved + emergencyFund.emergencyBalance
    }

    var selectedCategory by remember { mutableStateOf(PresetCategories.expenseGrid3x3.first()) }
    var editingLimitCategory by remember { mutableStateOf<QuickCategoryItem?>(null) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showBillEditorDialog by remember { mutableStateOf(false) }
    var editingBillItem by remember { mutableStateOf<MandatoryBillItem?>(null) }
    var activeDepositGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var showCreateTargetDialog by remember { mutableStateOf(false) }
    var selectedGoalId by remember(goals) { mutableStateOf(goals.firstOrNull()?.id ?: "") }
    LaunchedEffect(goals) {
        if (selectedGoalId.isBlank() || goals.none { it.id == selectedGoalId }) {
            selectedGoalId = goals.firstOrNull()?.id ?: ""
        }
    }

    var selectedWalletId by remember(wallets) {
        mutableStateOf(wallets.firstOrNull()?.id ?: "")
    }
    LaunchedEffect(wallets) {
        if (selectedWalletId.isBlank() || wallets.none { it.id == selectedWalletId }) {
            selectedWalletId = wallets.firstOrNull()?.id ?: ""
        }
    }

    var nominalText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var showGraphSummary by remember { mutableStateOf(false) }
    var graphMode by remember { mutableStateOf("HARIAN") } // HARIAN, BULANAN, ADU_CUAN
    var showTargetSummary by remember { mutableStateOf(false) }
    var targetRadarMode by remember { mutableStateOf("SETOR") } // SETOR, SIMULASI, KELOLA

    val todayStart = remember { ArusKasViewModel.getStartOfDayMillis() }
    val monthStart = remember { ArusKasViewModel.getStartOfMonthMillis() }

    val emergencySpent = remember(transactions, emergencyFund.limitPeriod) {
        ArusKasViewModel.calculateSpentForCategoryLimit(
            categoryKey = "pakai_darurat",
            period = emergencyFund.limitPeriod,
            transactions = transactions
        )
    }

    val todayExpense = remember(transactions, todayStart) {
        transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= todayStart }.sumOf { it.amount }
    }
    val todayIncome = remember(transactions, todayStart) {
        transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= todayStart }.sumOf { it.amount }
    }
    val monthExpense = remember(transactions, monthStart) {
        transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= monthStart }.sumOf { it.amount }
    }
    val monthIncomeTransactions = remember(transactions, monthStart) {
        transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= monthStart }
    }
    val monthIncome = remember(monthIncomeTransactions) {
        monthIncomeTransactions.sumOf { it.amount }
    }
    var inspectingIncomeCategoryKey by remember { mutableStateOf<String?>(null) }

    val genZAnalysis = remember(
        totalBalance,
        totalTabunganSaved,
        emergencyFund,
        todayExpense,
        monthExpense,
        monthIncome,
        selectedCategory,
        categoryLimits,
        transactions,
        goals,
        sessionSeed,
        guruState,
        lang
    ) {
        val successGuru = guruState as? GuruAdviceState.Success
        val rawAiQuote = successGuru?.nominalBoxAnalysis?.takeIf { it.isNotBlank() }
        GenZQuotesDatabase.generatePersonaAnalysis(
            totalBalance = totalBalance,
            totalTabunganSaved = totalTabunganSaved,
            emergencyBalance = emergencyFund.emergencyBalance,
            todayExpense = todayExpense,
            monthExpense = monthExpense,
            monthIncome = monthIncome,
            selectedCategory = selectedCategory,
            categoryLimits = categoryLimits,
            transactions = transactions,
            goals = goals,
            sessionSeed = sessionSeed,
            language = lang,
            aiOverrideText = rawAiQuote,
            aiColorLevel = successGuru?.colorLevel
        )
    }

    val typedAmount = nominalText.toLongOrNull() ?: 0L
    val isIncomeMode = selectedCategory.type == "INCOME"
    val isEmergencyExpenseSelected = selectedCategory.key == "pakai_darurat"

    val liveTypedRoast = remember(
        typedAmount,
        isIncomeMode,
        isEmergencyExpenseSelected,
        selectedCategory,
        totalBalance,
        emergencyFund.emergencyBalance,
        lang
    ) {
        GenZQuotesDatabase.liveTypingGenZRoast(
            typedAmount = typedAmount,
            isIncomeMode = isIncomeMode,
            isEmergencyExpenseSelected = isEmergencyExpenseSelected,
            selectedCategoryLabel = selectedCategory.label,
            totalBalance = totalBalance,
            emergencyBalance = emergencyFund.emergencyBalance,
            language = lang
        )
    }

    val nominalIndicatorLineColor = liveTypedRoast?.first ?: genZAnalysis.nominalLineColor
    val singleAnalysisMessage = liveTypedRoast?.second ?: genZAnalysis.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 0. TOP APP BAR: BRAND & AKSI UTAMA (myBCA Clean Minimalist Frosted Glass Pills)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.2.dp, BcaRoyalBlue.copy(alpha = 0.35f)),
                    modifier = Modifier.size(30.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lifesaver_guard_logo_1794106392181),
                        contentDescription = "ArusKas Logo",
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                    )
                }
                Column {
                    Text(
                        text = "ArusKas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BcaRoyalBlue,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            // Right Action Controls: Frosted Glass Pills (PRO + Panduan + Profil + Setelan)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val subConfig = appSettings.subscription
                val isProActive = subConfig.isProActive
                val isTrialActive = subConfig.isTrialActive
                val remainingDays = subConfig.remainingTrialDays
                val proBadgeLabel = when {
                    isProActive -> "PRO ⭐"
                    isTrialActive -> "PRO ${remainingDays}h"
                    else -> "PRO"
                }
                Surface(
                    onClick = onOpenPaywall,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = glassmorphicBorderStroke(accentColor = BcaRoyalBlue, width = 1.dp),
                    modifier = Modifier.testTag("top_pro_subscription_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "ArusKas PRO",
                            tint = BcaRoyalBlue,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = proBadgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = BcaRoyalBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Tombol Panduan Cara Kerja
                Surface(
                    onClick = onOpenUserGuide,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = glassmorphicBorderStroke(accentColor = BcaCobaltBlue, width = 1.dp),
                    modifier = Modifier.testTag("open_user_guide_top_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = AppLocalization.get("btn_user_guide", lang),
                            tint = BcaCobaltBlue,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = AppLocalization.get("btn_user_guide", lang),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = BcaCobaltBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tombol Profil Keuangan
                Surface(
                    onClick = onOpenFinancialProfileWizard,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = glassmorphicBorderStroke(accentColor = SkyBlue, width = 1.dp),
                    modifier = Modifier.testTag("open_financial_profile_top_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Profil Keuangan",
                            tint = SkyBlue,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Profil",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = SkyBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tombol Pengaturan (Settings Button)
                Surface(
                    onClick = onOpenSettings,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = glassmorphicBorderStroke(accentColor = BcaRoyalBlue, width = 1.dp),
                    modifier = Modifier.testTag("open_settings_button")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppLocalization.get("btn_settings", lang),
                            tint = BcaRoyalBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 1. AREA 1: KOTAK TOTAL SALDO (Tanpa Teks 'Uang Bebas Pakai (Aman Buat Harian)' — Bersih & Fokus)
        val emLimit = emergencyFund.emergencyLimit
        val emRatio = if (emLimit > 0L) (emergencySpent.toFloat() / emLimit.toFloat()) else 0f
        val emStatusColor = when {
            emLimit <= 0L -> SkyBlue
            emRatio >= 1f -> CoralExpense
            emRatio >= 0.75f -> GoldAccent
            else -> EmeraldPrimary
        }
        val emBadgeText = when {
            emLimit <= 0L -> "BEBAS"
            emRatio >= 1f -> "OVER!"
            emRatio >= 0.75f -> "AWAS"
            else -> "AMAN"
        }
        val activeGoal = goals.find { it.id == selectedGoalId } ?: goals.firstOrNull()

        MyBcaHeroLiquidCard(
            onClick = onOpenWalletsSheet,
            accentLineColor = genZAnalysis.saldoLineColor,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("top_minimalist_balance_pill")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // INDIKATOR SIMBOL GARIS WARNA AREA 1 (Kotak Total Saldo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    BcaElectricCyan,
                                    Color.White.copy(alpha = 0.35f)
                                )
                            )
                        )
                        .testTag("saldo_color_line_indicator")
                )

                // Main Total Saldo Row: Left side is Balance, Right side is Frosted Glass Grafik Button
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
                            color = Color.White.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Total Saldo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = formatMoneyDisplay(totalBalance),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = AppLocalization.get("see_all_wallet_details", lang),
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(17.dp)
                                )
                                FeatureHelpButton(
                                    content = FeatureHelpDatabase.TOTAL_SALDO,
                                    iconSize = 13,
                                    tint = Color.White,
                                    testTag = "help_total_saldo_button"
                                )
                            }
                            Text(
                                text = String.format(
                                    Locale("in", "ID"),
                                    AppLocalization.get("total_wallet_money", lang),
                                    formatMoneyDisplay(totalSemuaDuitDiKantong)
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                color = Color(0xFFD9F0FF),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Frosted Glass Grafik button on the right of the myBCA Hero Card
                    Surface(
                        onClick = {
                            showGraphSummary = !showGraphSummary
                            if (showGraphSummary) showTargetSummary = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (showGraphSummary) Color.White else Color.White.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
                        modifier = Modifier.testTag("toggle_graphic_summary_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Grafik",
                                tint = if (showGraphSummary) BcaRoyalBlue else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = AppLocalization.get("btn_graphic", lang),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (showGraphSummary) BcaRoyalBlue else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // MICRO STRIP GLASSMORPHISM: LIMIT & SIMPANAN DARURAT DI BAWAH TOTAL SALDO
                Surface(
                    onClick = { showEmergencyDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.38f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("under_balance_emergency_limit_row")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = "Limit Pengeluaran Darurat",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "Limit Darurat: ${ArusKasViewModel.formatCompactRupiah(emergencySpent)}/${ArusKasViewModel.formatCompactRupiah(emLimit)} • Simpanan: ${ArusKasViewModel.formatCompactRupiah(emergencyFund.emergencyBalance)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = emBadgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.2.sp),
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                FeatureHelpButton(
                                    content = FeatureHelpDatabase.POS_DARURAT,
                                    iconSize = 11,
                                    tint = Color.White,
                                    testTag = "help_pos_darurat_button"
                                )
                            }

                            Text(
                                text = "+ Darurat",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.2.sp),
                                color = Color(0xFFD9F0FF),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        if (emLimit > 0L) {
                            LinearProgressIndicator(
                                progress = { emRatio.coerceIn(0f, 1f) },
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                    }
                }
            }
        }

        // If user has no wallets yet, offer 1-tap starter setup for BCA, Bank Mas, DANA, ShopeePay, GoPay
        if (wallets.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.14f)),
                border = BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocalization.get("starter_title", lang),
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocalization.get("starter_desc", lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onOpenUserGuide,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_guide_from_starter_card")
                        ) {
                            Text(
                                text = AppLocalization.get("btn_user_guide", lang),
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = onOpenStarterSetup,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF231700)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_starter_setup_button")
                        ) {
                            Text(
                                text = AppLocalization.get("starter_btn", lang),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. BARIS MINI 3-CHIP SEJAJAR: HARI INI • BULAN INI • TABUNGAN TARGET MINI (Semua Membuka Radar Visual Bergaya Sama)
        // 2. BARIS KONTROL STRIP: METRIK PENGELUARAN & TOMBOL BULAT TABUNGAN TARGET (BENTUK CELENGAN MINIMALIS)
        val goalProgress = if (activeGoal != null) {
            (activeGoal.savedAmount.toFloat() / activeGoal.targetAmount.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
        } else 0f
        val goalPct = (goalProgress * 100).toInt()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sisi Kiri: 2 Metric Chips Vertikal (Hari Ini & Bulan Ini) yang kompak & hemat tempat
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SummaryMetricChip(
                    modifier = Modifier.fillMaxWidth(),
                    label = AppLocalization.get("label_today", lang),
                    value = formatMoneyDisplay(todayExpense),
                    accentColor = CoralExpense,
                    onClick = {
                        graphMode = "HARIAN"
                        showTargetSummary = false
                        showGraphSummary = true
                    }
                )
                SummaryMetricChip(
                    modifier = Modifier.fillMaxWidth(),
                    label = AppLocalization.get("label_this_month", lang),
                    value = formatMoneyDisplay(monthExpense),
                    accentColor = GoldAccent,
                    onClick = {
                        graphMode = "BULANAN"
                        showTargetSummary = false
                        showGraphSummary = true
                    }
                )
            }

            // Sisi Kanan: TOMBOL BULAT CELENGAN / TABUNGAN TARGET (Ultra-Minimalis, Jelas Terlihat & Estetik)
            Box(contentAlignment = Alignment.TopEnd) {
                TabunganTargetRoundButton(
                    activeGoal = activeGoal,
                    progress = goalProgress,
                    percent = goalPct,
                    isSelected = showTargetSummary,
                    lang = lang,
                    onClick = {
                        showTargetSummary = !showTargetSummary
                        if (showTargetSummary) showGraphSummary = false
                    }
                )
                FeatureHelpButton(
                    content = FeatureHelpDatabase.TABUNGAN_TARGET,
                    iconSize = 11,
                    tint = GoldAccent,
                    testTag = "help_tabungan_target_button",
                    modifier = Modifier.padding(top = 2.dp, end = 2.dp)
                )
            }
        }

        AnimatedVisibility(visible = showGraphSummary) {
            GraphicalExpenseSummaryCard(
                transactions = transactions,
                mode = graphMode,
                onModeChange = { graphMode = it },
                onClose = { showGraphSummary = false },
                totalBalance = totalBalance,
                totalTabunganSaved = totalTabunganSaved,
                emergencyBalance = emergencyFund.emergencyBalance,
                categoryLimits = categoryLimits
            )
        }

        AnimatedVisibility(visible = showTargetSummary) {
            TabunganTargetVisualRadarCard(
                activeGoal = activeGoal,
                allGoals = goals,
                wallets = wallets,
                transactions = transactions,
                mode = targetRadarMode,
                onModeChange = { targetRadarMode = it },
                onSelectGoal = { selectedGoalId = it.id },
                onAllocateSavings = { goal, wallet, amount, note ->
                    onAllocateSavings(goal, wallet, amount, note)
                },
                onOpenCreateTarget = { showCreateTargetDialog = true },
                onDeleteGoal = { goal -> onDeleteGoal(goal) },
                onClose = { showTargetSummary = false }
            )
        }

        // 3. AREA 2: KOTAK MASUKAN NOMINAL (Dengan Indikator Simbol Garis Warna Saja, Tanpa Kotak Teks Analisis di Dalamnya)
        val activeAccent = when {
            isIncomeMode -> EmeraldPrimary
            isEmergencyExpenseSelected -> GoldAccent
            else -> CoralExpense
        }
        val availableCategories = if (isIncomeMode) {
            PresetCategories.incomeQuickPills
        } else {
            PresetCategories.expenseGrid3x3 + PresetCategories.emergencyExpenseCategory
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = glassmorphicBorderStroke(accentColor = activeAccent, width = 1.3.dp),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphicCardEffect(
                    cornerRadius = 22.dp,
                    accentTint = activeAccent,
                    isDark = appSettings.isDarkMode
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                // INDIKATOR SIMBOL GARIS WARNA AREA 2 (Kotak Masukan Nominal)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BcaRoyalBlue,
                                    BcaElectricCyan,
                                    nominalIndicatorLineColor.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .testTag("nominal_color_line_indicator")
                )

                // Built-in Mode Switcher (Pengeluaran vs Pemasukan) with Frosted Glass Track
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(13.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = {
                                if (isIncomeMode) {
                                    selectedCategory = PresetCategories.expenseGrid3x3.first()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isIncomeMode) BcaRoyalBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mode_switch_expense")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (!isIncomeMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocalization.get("expense_mode", lang),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (!isIncomeMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                if (!isIncomeMode) {
                                    selectedCategory = PresetCategories.incomeQuickPills.first()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isIncomeMode) BcaCobaltBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mode_switch_income")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = if (isIncomeMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocalization.get("income_mode", lang),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isIncomeMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Simbol Tanda Tanya Panduan Input Nominal
                    FeatureHelpButton(
                        content = FeatureHelpDatabase.INPUT_NOMINAL,
                        iconSize = 13,
                        tint = BcaRoyalBlue,
                        testTag = "help_input_nominal_button"
                    )
                }

                // Inline Category Selector inside the recording box
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isIncomeMode) "Sumber Cuan:" else "Pos Pengeluaran:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            color = activeAccent.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "${selectedCategory.label} • ${selectedCategory.frequencyTag}",
                                style = MaterialTheme.typography.labelSmall,
                                color = activeAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableCategories.forEach { catItem ->
                            val isSelectedCat = selectedCategory.key == catItem.key
                            Surface(
                                onClick = { selectedCategory = catItem },
                                shape = RoundedCornerShape(11.dp),
                                color = if (isSelectedCat) activeAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                border = BorderStroke(
                                    width = if (isSelectedCat) 1.5.dp else 1.dp,
                                    color = if (isSelectedCat) activeAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.testTag("inline_category_chip_${catItem.key}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = iconForCategory(catItem.key),
                                        contentDescription = catItem.label,
                                        tint = if (isSelectedCat) activeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Column {
                                        Text(
                                            text = catItem.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isSelectedCat) activeAccent else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (catItem.subtitle.isNotBlank()) {
                                            Text(
                                                text = catItem.subtitle,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // PINTASAN KEHIDUPAN SEHARI-HARI (REAL-LIFE FLEXIBLE QUICK ITEMS: Bensin, Rokok, Makan, Kuota, Skincare, dll - Tanpa Jadwal Jam Kaku)
                    if (!isIncomeMode && !isEmergencyExpenseSelected && selectedCategory.key != "tagihan_bulanan") {
                        val quickItems = remember(selectedCategory.key, userProfile.gender) {
                            PresetCategories.realLifeQuickItems.filter { item ->
                                item.categoryKey == selectedCategory.key &&
                                    (item.genderFilter == "ALL" || item.genderFilter == userProfile.gender)
                            }
                        }
                        val activeLimitCfg = categoryLimits[selectedCategory.key] ?: CategoryLimitConfig(
                            categoryKey = selectedCategory.key,
                            limitAmount = selectedCategory.defaultLimitAmount,
                            period = selectedCategory.defaultLimitPeriod
                        )
                        val spentInPeriod = remember(transactions, mandatoryBills, selectedCategory.key, activeLimitCfg.period) {
                            ArusKasViewModel.calculateSpentForCategoryLimit(
                                categoryKey = selectedCategory.key,
                                period = activeLimitCfg.period,
                                transactions = transactions,
                                mandatoryBills = mandatoryBills
                            )
                        }
                        val remainingPool = (activeLimitCfg.limitAmount - spentInPeriod).coerceAtLeast(0L)
                        val adaptiveDailyAllowance = remember(activeLimitCfg, spentInPeriod) {
                            ArusKasViewModel.calculateFlexibleAdaptiveQuotaPerDay(
                                limitAmount = activeLimitCfg.limitAmount,
                                spentAmount = spentInPeriod,
                                period = activeLimitCfg.period
                            )
                        }

                        if (quickItems.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Pintasan Sehari-hari (Bebas Kapan Aja):",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        FeatureHelpButton(
                                            content = FeatureHelpDatabase.PINTASAN_SEHARI_HARI,
                                            iconSize = 10,
                                            tint = SkyBlue,
                                            testTag = "help_pintasan_sehari_hari"
                                        )
                                    }
                                    if (activeLimitCfg.limitAmount > 0L && activeLimitCfg.period != "BEBAS") {
                                        Text(
                                            text = "Sisa Kuota: ${ArusKasViewModel.formatCompactRupiah(remainingPool)} (~${ArusKasViewModel.formatCompactRupiah(adaptiveDailyAllowance)}/hr)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    quickItems.forEach { qItem ->
                                        val isPicked = noteText == qItem.label && nominalText == qItem.suggestedAmount.toString()
                                        Surface(
                                            onClick = {
                                                nominalText = qItem.suggestedAmount.toString()
                                                noteText = qItem.label
                                            },
                                            shape = RoundedCornerShape(9.dp),
                                            color = if (isPicked) activeAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (isPicked) activeAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.testTag("quick_real_item_${qItem.label.hashCode()}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = qItem.label,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = ArusKasViewModel.formatCompactRupiah(qItem.suggestedAmount),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                                    color = activeAccent,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Nominal input
                OutlinedTextField(
                    value = nominalText,
                    onValueChange = { input -> nominalText = input.filter { it.isDigit() } },
                    label = {
                        Text(
                            when {
                                isIncomeMode -> AppLocalization.get("nominal_input_label_income", lang)
                                isEmergencyExpenseSelected -> AppLocalization.get("nominal_input_label_emergency", lang)
                                else -> AppLocalization.get("nominal_input_label_expense", lang)
                            }
                        )
                    },
                    placeholder = { Text(AppLocalization.get("nominal_input_placeholder", lang)) },
                    prefix = {
                        Text(
                            text = "Rp ",
                            style = MaterialTheme.typography.titleLarge,
                            color = activeAccent,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = activeAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nominal_input_field")
                )

                // Wallet selector row OR Separated Emergency Fund notice
                if (isEmergencyExpenseSelected) {
                    Surface(
                        onClick = { showEmergencyDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = String.format(
                                    Locale("in", "ID"),
                                    AppLocalization.get("emergency_cut_notice", lang),
                                    formatMoneyDisplay(emergencyFund.emergencyBalance)
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(
                                    Locale("in", "ID"),
                                    AppLocalization.get("emergency_place_hint", lang),
                                    emergencyFund.storagePlaceNote
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (isIncomeMode) AppLocalization.get("select_wallet_income", lang) else AppLocalization.get("select_wallet_expense", lang),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (wallets.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            wallets.forEach { wallet ->
                                val isSelected = wallet.id == selectedWalletId
                                val wColor = parseColorSafely(wallet.colorHex)
                                Surface(
                                    onClick = { selectedWalletId = wallet.id },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) wColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) wColor else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = wColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = wallet.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = formatMoneyDisplay(wallet.balance),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Note & Submit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text(if (isIncomeMode) AppLocalization.get("note_input_income", lang) else AppLocalization.get("note_input_expense", lang)) },
                        placeholder = { Text(selectedCategory.defaultNoteHint, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("note_input_field")
                    )

                    Button(
                        onClick = {
                            val targetWallet = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()
                            val amt = nominalText.toLongOrNull() ?: 0L
                            if (targetWallet != null && amt > 0L) {
                                onRecordTransaction(
                                    targetWallet,
                                    selectedCategory.type,
                                    amt,
                                    selectedCategory.key,
                                    selectedCategory.label,
                                    selectedCategory.frequencyTag,
                                    noteText.ifBlank { selectedCategory.label }
                                )
                                nominalText = ""
                                noteText = ""
                            }
                        },
                        enabled = !isSubmitting && wallets.isNotEmpty() && (nominalText.toLongOrNull() ?: 0L) > 0L,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BcaRoyalBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("submit_transaction_button")
                    ) {
                        Text(
                            text = if (isIncomeMode) AppLocalization.get("btn_submit_income", lang) else AppLocalization.get("btn_submit_expense", lang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // PENGINGAT SEHABIS AKTIVITAS (BANNER INTERAKTIF)
        if (postActivityReminder != null) {
            PostActivityReviewBanner(
                visible = true,
                activityTitle = postActivityReminder.title,
                amountFormatted = postActivityReminder.amountFormatted,
                categoryLabel = postActivityReminder.categoryLabel,
                remainingAllowance = postActivityReminder.remainingAllowance,
                onCheckRecords = {
                    onDismissPostActivityReminder()
                },
                onDismiss = onDismissPostActivityReminder
            )
        }

        // KARTU PENGINGAT RUTINITAS HARIAN (BEBAS BENTROK JAM SIBUK)
        ActivityReminderDashboardCard(
            profile = userProfile,
            onOpenScheduleSettings = onOpenRoutineSettings,
            onTestNotificationTriggered = {
                // Notifikasi uji langsung terkirim
            }
        )

        // 4. AREA 3: 6 KOTAK KATEGORI PENGELUARAN (Judul Kecil + Bar Batas Limit & Bar Limit Terpakai + Simbol Panduan)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Pos Pengeluaran & Plafon Limit",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FeatureHelpButton(
                        content = FeatureHelpDatabase.KATEGORI_LIMIT,
                        iconSize = 12,
                        tint = EmeraldPrimary,
                        testTag = "help_category_limits_button"
                    )
                }
            }

            val rows = PresetCategories.expenseGrid3x3.chunked(3)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        val isSelected = selectedCategory.key == item.key
                        val rawLimitCfg = categoryLimits[item.key] ?: CategoryLimitConfig(
                            categoryKey = item.key,
                            limitAmount = item.defaultLimitAmount,
                            period = item.defaultLimitPeriod
                        )
                        val effectiveLimitAmt = if (item.key == "tagihan_bulanan" && mandatoryBills.isNotEmpty()) {
                            maxOf(rawLimitCfg.limitAmount, mandatoryBills.sumOf { it.totalAmount })
                        } else {
                            rawLimitCfg.limitAmount
                        }
                        val limitCfg = rawLimitCfg.copy(limitAmount = effectiveLimitAmt)
                        val spent = remember(transactions, mandatoryBills, item.key, limitCfg.period) {
                            ArusKasViewModel.calculateSpentForCategoryLimit(
                                categoryKey = item.key,
                                period = limitCfg.period,
                                transactions = transactions,
                                mandatoryBills = mandatoryBills
                            )
                        }
                        Expense3x3Tile(
                            modifier = Modifier.weight(1f),
                            item = item,
                            spentAmount = spent,
                            limitConfig = limitCfg,
                            isSelected = isSelected,
                            onClick = { selectedCategory = item },
                            onEditLimit = { editingLimitCategory = item }
                        )
                    }
                }
            }
        }

        // 4B. CEKLIST TAGIHAN WAJIB & SIKLUS JATUH TEMPO (Kosan Tgl 1, Kuota Habis & Beli Lagi, Listrik, Cicilan - Bisa Ceklist Belum / Setengah / Lunas)
        val currentSelectedWallet = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()
        MandatoryBillsChecklistSection(
            bills = mandatoryBills,
            selectedWallet = currentSelectedWallet,
            onChangeBillStatus = { bill, newStatus ->
                onUpdateMandatoryBillStatus(bill, newStatus, currentSelectedWallet)
            },
            onAddBillClick = {
                editingBillItem = null
                showBillEditorDialog = true
            },
            onEditBillClick = { bill ->
                editingBillItem = bill
                showBillEditorDialog = true
            },
            onDeleteBillClick = { bill ->
                onDeleteMandatoryBill(bill.id)
            }
        )

        // 5. COMPACT & MINIMALIST INCOME ROW (Tanpa Kata/Pill Tambahan di Bawah Uang Masuk + Simbol Panduan Melayang)
        val netCashflowMonth = monthIncome - monthExpense
        val incomeLineColor = when {
            monthIncome <= 0L -> GoldAccent
            netCashflowMonth >= 0L -> EmeraldPrimary
            else -> GoldAccent
        }

        Surface(
            onClick = { inspectingIncomeCategoryKey = "ALL" },
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            border = glassmorphicBorderStroke(accentColor = BcaRoyalBlue, width = 1.1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphicCardEffect(
                    cornerRadius = 16.dp,
                    accentTint = BcaRoyalBlue,
                    isDark = appSettings.isDarkMode
                )
                .testTag("bottom_income_monitor_box")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Indikator Simbol Garis Warna Pemasukan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BcaRoyalBlue,
                                    BcaElectricCyan,
                                    incomeLineColor.copy(alpha = 0.25f)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (todayIncome > 0L) {
                                "UANG MASUK • HARI INI: +${ArusKasViewModel.formatCompactRupiah(todayIncome)}"
                            } else {
                                "UANG MASUK (${monthIncomeTransactions.size}x Masuk)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        FeatureHelpButton(
                            content = FeatureHelpDatabase.MONITOR_UANG_MASUK,
                            iconSize = 11,
                            tint = EmeraldPrimary,
                            testTag = "help_income_monitor_button"
                        )
                    }
                    Text(
                        text = "Bulan ini: +${ArusKasViewModel.formatCompactRupiah(monthIncome)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (netCashflowMonth >= 0L) EmeraldPrimary else GoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 6. RECENT CASH FLOW LOG (Tanpa Judul Kata di Bawah Uang Masuk)
        if (transactions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(6).forEach { tx ->
                    TransactionRowCard(
                        tx = tx,
                        onDelete = { onDeleteTransaction(tx) }
                    )
                }
            }
        }
    }

    if (showBillEditorDialog) {
        MandatoryBillEditorDialog(
            initialBill = editingBillItem,
            onDismiss = {
                showBillEditorDialog = false
                editingBillItem = null
            },
            onSave = { billId, name, totalAmount, dueScheduleLabel, dueDay, renewalDay, billType ->
                onUpsertMandatoryBill(billId, name, totalAmount, dueScheduleLabel, dueDay, renewalDay, billType)
                showBillEditorDialog = false
                editingBillItem = null
            }
        )
    }

    if (inspectingIncomeCategoryKey != null) {
        val filterKey = inspectingIncomeCategoryKey!!
        IncomeHistoryDetailDialog(
            initialFilterKey = filterKey,
            monthIncomeTransactions = monthIncomeTransactions,
            todayIncome = todayIncome,
            monthIncome = monthIncome,
            monthExpense = monthExpense,
            onDismiss = { inspectingIncomeCategoryKey = null }
        )
    }

    if (editingLimitCategory != null) {
        val cat = editingLimitCategory!!
        val currentCfg = categoryLimits[cat.key] ?: CategoryLimitConfig(
            categoryKey = cat.key,
            limitAmount = cat.defaultLimitAmount,
            period = cat.defaultLimitPeriod
        )
        CategoryLimitDialog(
            category = cat,
            initialConfig = currentCfg,
            onDismiss = { editingLimitCategory = null },
            onSave = { limitAmount, period ->
                onUpdateCategoryLimit(cat.key, cat.label, limitAmount, period)
                editingLimitCategory = null
            }
        )
    }

    if (showEmergencyDialog) {
        EmergencyFundDialog(
            wallets = wallets,
            currentConfig = emergencyFund,
            currentSpent = emergencySpent,
            onDismiss = { showEmergencyDialog = false },
            onSave = { sourceWallet, addedAmount, newLimit, period, storageNote ->
                onUpdateEmergencyFund(sourceWallet, addedAmount, newLimit, period, storageNote)
                showEmergencyDialog = false
            }
        )
    }

    if (activeDepositGoal != null) {
        val goal = activeDepositGoal!!
        TabunganTargetDepositDialog(
            goal = goal,
            allGoals = goals,
            onSelectGoal = { picked ->
                selectedGoalId = picked.id
                activeDepositGoal = picked
            },
            onCreateNewTarget = {
                activeDepositGoal = null
                showCreateTargetDialog = true
            },
            wallets = wallets,
            onDismiss = { activeDepositGoal = null },
            onDelete = {
                onDeleteGoal(goal)
                activeDepositGoal = null
            },
            onConfirm = { wallet, amount, note ->
                onAllocateSavings(goal, wallet, amount, note)
                activeDepositGoal = null
            }
        )
    }

    if (showCreateTargetDialog) {
        TabunganTargetCreateDialog(
            onDismiss = { showCreateTargetDialog = false },
            onSave = { title, target, daily, note ->
                onAddGoal(title, target, daily, note, "laptop")
                showCreateTargetDialog = false
            }
        )
    }
}

@Composable
fun IncomeHistoryDetailDialog(
    initialFilterKey: String,
    monthIncomeTransactions: List<TransactionRecord>,
    todayIncome: Long,
    monthIncome: Long,
    monthExpense: Long,
    onDismiss: () -> Unit
) {
    var activeFilter by remember(initialFilterKey) { mutableStateOf(initialFilterKey) }
    val filteredList = remember(monthIncomeTransactions, activeFilter) {
        if (activeFilter == "ALL") {
            monthIncomeTransactions
        } else {
            monthIncomeTransactions.filter { it.categoryKey == activeFilter }
        }
    }
    val walletBreakdown = remember(monthIncomeTransactions) {
        monthIncomeTransactions
            .groupBy { it.walletName.ifBlank { "Dompet" } }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
    }
    val netCashflow = monthIncome - monthExpense
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("in", "ID")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Rincian Masuknya Uang",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hari Ini: +${ArusKasViewModel.formatRupiah(todayIncome)} • Bulan Ini: +${ArusKasViewModel.formatRupiah(monthIncome)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ringkasan Arus Kas Bersih & Rekomendasi Sisihkan
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (netCashflow >= 0L) EmeraldPrimary else GoldAccent).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, (if (netCashflow >= 0L) EmeraldPrimary else GoldAccent).copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (netCashflow >= 0L) {
                                "🟢 Surplus Bulan Ini: +${ArusKasViewModel.formatRupiah(netCashflow)}"
                            } else {
                                "🟡 Defisit Bulan Ini: -${ArusKasViewModel.formatRupiah(kotlin.math.abs(netCashflow))}"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (netCashflow >= 0L) EmeraldPrimary else GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saran sehat finansial: amankan minimal 20% (${ArusKasViewModel.formatRupiah((monthIncome * 20L) / 100L)}) dari total uang masuk ke Tabungan Target / Dana Darurat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rincian Masuk ke Dompet Mana Saja
                if (walletBreakdown.isNotEmpty()) {
                    Text(
                        text = "Masuk ke Dompet Mana Saja (Bulan Ini):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        walletBreakdown.forEach { (walletName, sumAmt) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = "$walletName: +${ArusKasViewModel.formatCompactRupiah(sumAmt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Filter per Sumber Uang Masuk
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = activeFilter == "ALL",
                        onClick = { activeFilter = "ALL" },
                        label = { Text("Semua (${monthIncomeTransactions.size})", style = MaterialTheme.typography.labelSmall) }
                    )
                    PresetCategories.incomeQuickPills.forEach { inc ->
                        val count = monthIncomeTransactions.count { it.categoryKey == inc.key }
                        FilterChip(
                            selected = activeFilter == inc.key,
                            onClick = { activeFilter = inc.key },
                            label = { Text("${inc.label} ($count)", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Daftar Riwayat Masuknya Uang
                if (filteredList.isEmpty()) {
                    Text(
                        text = "Belum ada catatan uang masuk pada kategori ini bulan ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    filteredList.take(10).forEach { tx ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.note.ifBlank { tx.categoryLabel },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${tx.categoryLabel} • Masuk ke ${tx.walletName} • ${dateFormat.format(Date(tx.transactionDateMillis))}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "+${ArusKasViewModel.formatCompactRupiah(tx.amount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun TabunganTargetDepositDialog(
    goal: SavingsGoal,
    allGoals: List<SavingsGoal> = emptyList(),
    onSelectGoal: (SavingsGoal) -> Unit = {},
    onCreateNewTarget: () -> Unit = {},
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onConfirm: (wallet: Wallet, amount: Long, note: String) -> Unit
) {
    var selectedWalletId by remember(wallets) { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var amountStr by remember(goal) {
        mutableStateOf(goal.dailyRecommendation.coerceAtLeast(25_000L).toString())
    }
    var noteStr by remember(goal) {
        mutableStateOf("Tabungan Target: ${goal.title} (Simpan Terpisah)")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tabungan: ${goal.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        onClick = onCreateNewTarget,
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.testTag("quick_add_target_button")
                    ) {
                        Text(
                            text = "+ Target Baru",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = "Terkumpul: ${ArusKasViewModel.formatRupiah(goal.savedAmount)} / ${ArusKasViewModel.formatRupiah(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (allGoals.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allGoals.forEach { g ->
                            val isSel = g.id == goal.id
                            FilterChip(
                                selected = isSel,
                                onClick = { onSelectGoal(g) },
                                label = { Text(g.title, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚠️ PENTING: Pisahkan Tempat Simpan Uang Tabungan!",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pindahkan/simpan uang tabungan target ini di rekening atau kantong khusus yang berbeda dari uang jajan harian. Setoran ini otomatis memotong Saldo Siap Pakai, namun tetap dihitung di Total Semua Duit di Kantongmu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Potong dari Saldo Dompet Mana:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
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
                    label = { Text("Nominal Setor Tabungan (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_target_amount_input")
                )

                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    label = { Text("Disimpan di Mana / Catatan") },
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
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color(0xFF042619)
                ),
                modifier = Modifier.testTag("confirm_deposit_target_button")
            ) {
                Text("Potong Saldo & Tabung", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onDelete) {
                    Text("Hapus Target", color = CoralExpense)
                }
                TextButton(onClick = onDismiss) { Text("Batal") }
            }
        }
    )
}

@Composable
fun TabunganTargetCreateDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, target: Long, daily: Long, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var dailyStr by remember { mutableStateOf("40000") }
    var note by remember { mutableStateOf("Disimpan terpisah di rekening tabungan") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Buat Tabungan Target Baru",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 Pastikan uang Tabungan Target disimpan terpisah dari saldo jajan harian agar targetmu cepat tercapai.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Target (misal: Beli Laptop / HP / Motor)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Nominal (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dailyStr,
                    onValueChange = { dailyStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Setoran per Hari (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Tempat Simpan Terpisah & Catatan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val t = targetStr.toLongOrNull() ?: 0L
                    val d = dailyStr.toLongOrNull() ?: 25_000L
                    if (title.isNotBlank() && t > 0L) {
                        onSave(title.trim(), t, d, note.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Color(0xFF231700)
                )
            ) {
                Text("Simpan Target", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun EmergencyFundDialog(
    wallets: List<Wallet>,
    currentConfig: EmergencyFundConfig,
    currentSpent: Long,
    onDismiss: () -> Unit,
    onSave: (
        sourceWallet: Wallet?,
        addedAmount: Long,
        newLimit: Long,
        period: String,
        storageNote: String
    ) -> Unit
) {
    var addAmountStr by remember { mutableStateOf("") }
    var limitStr by remember(currentConfig) { mutableStateOf(currentConfig.emergencyLimit.toString()) }
    var selectedPeriod by remember(currentConfig) { mutableStateOf(currentConfig.limitPeriod) }
    var storageNote by remember(currentConfig) { mutableStateOf(currentConfig.storagePlaceNote) }
    var selectedSourceWalletId by remember(wallets) {
        mutableStateOf(wallets.firstOrNull()?.id ?: "")
    }

    val periodOptions = listOf(
        "BULANAN" to "Per Bulan",
        "MINGGUAN" to "Per Minggu",
        "HARIAN" to "Per Hari",
        "BEBAS" to "Semaunya / Bebas"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Pos Dana & Limit Uang Darurat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Saldo Darurat Saat Ini: ${ArusKasViewModel.formatRupiah(currentConfig.emergencyBalance)} • Terpakai: ${ArusKasViewModel.formatRupiah(currentSpent)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Important Reminder to separate emergency storage
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚠️ PENTING: Pisahkan Penyimpanan Uang Darurat!",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simpan uang darurat di tempat/rekening yang berbeda dari uang jajan harian. Setiap penambahan Saldo Darurat di bawah ini otomatis memotong Total Saldo Aktifmu supaya tidak ikut terpakai.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 1. Add Emergency Balance (Separated & Deducted from Active Wallet)
                Text(
                    text = "1. Tambah / Sisihkan ke Saldo Darurat Terpisah",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                OutlinedTextField(
                    value = addAmountStr,
                    onValueChange = { input -> addAmountStr = input.filter { it.isDigit() } },
                    label = { Text("Nominal Tambah Saldo Darurat (Rp)") },
                    placeholder = { Text("Kosongkan jika hanya ubah limit") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_emergency_balance_input")
                )

                if (wallets.isNotEmpty()) {
                    Text(
                        text = "Potong dari Saldo Dompet Mana (Agar Total Saldo Langsung Terpotong):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        wallets.forEach { w ->
                            val isSel = w.id == selectedSourceWalletId
                            Surface(
                                onClick = { selectedSourceWalletId = w.id },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(w.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text(ArusKasViewModel.formatCompactRupiah(w.balance), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = storageNote,
                    onValueChange = { storageNote = it },
                    label = { Text("Disimpan Terpisah di Mana? (Pengingat)") },
                    placeholder = { Text("Misal: Rekening Bank Mas Khusus / Amplop Darurat") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // 2. Emergency Spending Limit
                Text(
                    text = "2. Atur Limit Pengeluaran Uang Darurat",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CoralExpense
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    periodOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedPeriod == key,
                            onClick = { selectedPeriod = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = limitStr,
                    onValueChange = { input -> limitStr = input.filter { it.isDigit() } },
                    label = { Text("Target Limit Pengeluaran Darurat (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_limit_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sourceW = wallets.find { it.id == selectedSourceWalletId } ?: wallets.firstOrNull()
                    val added = addAmountStr.toLongOrNull() ?: 0L
                    val newLim = limitStr.toLongOrNull() ?: currentConfig.emergencyLimit
                    onSave(sourceW, added, newLim, selectedPeriod, storageNote)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Color(0xFF231700)
                ),
                modifier = Modifier.testTag("save_emergency_fund_button")
            ) {
                Text("Simpan Pos Darurat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun Expense3x3Tile(
    modifier: Modifier = Modifier,
    item: QuickCategoryItem,
    spentAmount: Long,
    limitConfig: CategoryLimitConfig,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditLimit: () -> Unit
) {
    val limitAmt = limitConfig.limitAmount
    val ratio = if (limitAmt > 0L) (spentAmount.toFloat() / limitAmt.toFloat()) else 0f
    val pctUsed = (ratio * 100f).toInt().coerceAtLeast(0)
    val remainingAmt = (limitAmt - spentAmount).coerceAtLeast(0L)
    val statusColor = when {
        limitAmt <= 0L -> SkyBlue
        ratio >= 1f -> CoralExpense
        ratio >= 0.75f -> GoldAccent
        else -> EmeraldPrimary
    }
    val shortPeriod = when (limitConfig.period) {
        "HARIAN" -> "/hr"
        "MINGGUAN" -> "/mgg"
        "BULANAN" -> "/bln"
        else -> ""
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = glassmorphicBorderStroke(
            accentColor = statusColor,
            width = 1.1.dp,
            isSelected = isSelected
        ),
        modifier = modifier
            .height(110.dp)
            .glassmorphicCardEffect(
                cornerRadius = 16.dp,
                accentTint = statusColor
            )
            .testTag("category_tile_${item.key}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Header: Judul Kotak Kecil + Tombol Edit Limit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = iconForCategory(item.key),
                        contentDescription = item.label,
                        tint = if (isSelected) statusColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    onClick = onEditLimit,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(18.dp)
                        .testTag("edit_limit_${item.key}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Atur Limit ${item.label}",
                            tint = statusColor,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            // BAR 1: BAR BATAS LIMIT (Plafon 100%)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Batas Limit",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.4.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (limitAmt > 0L) {
                            "${ArusKasViewModel.formatCompactRupiah(limitAmt).replace("Rp ", "")}$shortPeriod"
                        } else {
                            "Bebas"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.8.sp),
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SkyBlue.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(SkyBlue.copy(alpha = 0.7f), SkyBlue)
                                )
                            )
                    )
                }
            }

            // BAR 2: BAR LIMIT YANG SUDAH TERPAKAI
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terpakai",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.4.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (limitAmt > 0L) {
                            "${ArusKasViewModel.formatCompactRupiah(spentAmount).replace("Rp ", "")} ($pctUsed%)"
                        } else {
                            ArusKasViewModel.formatCompactRupiah(spentAmount).replace("Rp ", "")
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.8.sp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                LinearProgressIndicator(
                    progress = {
                        if (limitAmt > 0L) ratio.coerceIn(0f, 1f) else if (spentAmount > 0L) 0.35f else 0f
                    },
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                )
                Text(
                    text = if (limitAmt > 0L) {
                        if (spentAmount <= limitAmt) {
                            "Sisa: ${ArusKasViewModel.formatCompactRupiah(remainingAmt)}"
                        } else {
                            "Lewat: +${ArusKasViewModel.formatCompactRupiah(spentAmount - limitAmt)}"
                        }
                    } else {
                        "Mode Fleksibel"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.4.sp),
                    color = if (limitAmt > 0L && spentAmount > limitAmt) CoralExpense else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CategoryLimitDialog(
    category: QuickCategoryItem,
    initialConfig: CategoryLimitConfig,
    onDismiss: () -> Unit,
    onSave: (limitAmount: Long, period: String) -> Unit
) {
    var limitStr by remember(initialConfig) {
        mutableStateOf(initialConfig.limitAmount.toString())
    }
    var selectedPeriod by remember(initialConfig) {
        mutableStateOf(initialConfig.period)
    }

    val periodOptions = listOf(
        "BULANAN" to "Per Bulan",
        "MINGGUAN" to "Per Minggu",
        "HARIAN" to "Per Hari",
        "BEBAS" to "Semaunya / Bebas"
    )
    val presetLimits = listOf(150_000L, 300_000L, 500_000L, 1_000_000L, 1_500_000L, 2_500_000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Target Limit: ${category.label}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pilih Periode Target Limit:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    periodOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedPeriod == key,
                            onClick = { selectedPeriod = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = limitStr,
                    onValueChange = { input -> limitStr = input.filter { it.isDigit() } },
                    label = { Text("Batas Maksimal Pengeluaran (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    visualTransformation = RupiahThousandsVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_limit_input")
                )

                Text(
                    text = "Pilihan Cepat Nominal Limit:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetLimits.forEach { preset ->
                        Surface(
                            onClick = { limitStr = preset.toString() },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (limitStr == preset.toString()) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = ArusKasViewModel.formatCompactRupiah(preset),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (limitStr == preset.toString()) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Tanda indikator akan otomatis berubah dari AMAN (Hijau), HAMPIR LIMIT (Kuning ≥75%), hingga LEWAT LIMIT! (Merah ≥100%).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = limitStr.toLongOrNull() ?: 0L
                    onSave(amt, selectedPeriod)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color(0xFF042619)
                ),
                modifier = Modifier.testTag("save_category_limit_button")
            ) {
                Text("Simpan Limit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun TabunganTargetRoundButton(
    activeGoal: SavingsGoal?,
    progress: Float,
    percent: Int,
    isSelected: Boolean,
    lang: AppLanguage = AppLanguage.INDONESIAN,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        border = glassmorphicBorderStroke(
            accentColor = EmeraldPrimary,
            width = 1.dp,
            isSelected = isSelected
        ),
        modifier = Modifier
            .glassmorphicCardEffect(cornerRadius = 14.dp, accentTint = EmeraldPrimary)
            .testTag("tabungan_target_minimalist_card")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Celengan Round Medallion with Progress Arc
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track and Progress Arc
                Canvas(modifier = Modifier.size(46.dp)) {
                    val strokeWidth = 3.dp.toPx()
                    drawCircle(
                        color = BcaRoyalBlue.copy(alpha = 0.16f),
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = BcaRoyalBlue,
                        startAngle = -90f,
                        sweepAngle = (progress.coerceIn(0f, 1f)) * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner Round Piggy Bank / Celengan Coin
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) BcaRoyalBlue else BcaRoyalBlue.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, BcaRoyalBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Coin slot decorative accent
                        Box(
                            modifier = Modifier
                                .width(7.dp)
                                .height(1.8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color.White else BcaElectricCyan)
                        )
                        Spacer(modifier = Modifier.height(1.5.dp))
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = AppLocalization.get("target_savings", lang),
                            tint = if (isSelected) Color.White else BcaRoyalBlue,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Text Label & Subtitle Info
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = AppLocalization.get("target_savings", lang),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BcaRoyalBlue.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = BcaRoyalBlue,
                            modifier = Modifier.padding(horizontal = 3.5.dp, vertical = 0.5.dp)
                        )
                    }
                }
                Text(
                    text = if (activeGoal != null) {
                        "${activeGoal.title.take(10)} • ${ArusKasViewModel.formatCompactRupiah(activeGoal.savedAmount)}"
                    } else {
                        AppLocalization.get("target_create_hint", lang)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.2.sp),
                    color = BcaRoyalBlue,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(11.dp),
        color = Color.Transparent,
        border = glassmorphicBorderStroke(accentColor = accentColor, width = 0.9.dp),
        modifier = modifier.glassmorphicCardEffect(cornerRadius = 11.dp, accentTint = accentColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$label:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.8.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = value.replace("Rp ", ""),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.6.sp),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = label,
                tint = accentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

private data class GenZDayStat(
    val dayShort: String,
    val fullDateLabel: String,
    val expenseAmount: Long,
    val incomeAmount: Long,
    val topCategoryLabel: String,
    val txCount: Int,
    val isToday: Boolean
)

@Composable
fun GraphicalExpenseSummaryCard(
    transactions: List<TransactionRecord>,
    mode: String,
    onModeChange: (String) -> Unit,
    onClose: () -> Unit,
    totalBalance: Long = 0L,
    totalTabunganSaved: Long = 0L,
    emergencyBalance: Long = 0L,
    categoryLimits: Map<String, CategoryLimitConfig> = PresetCategories.defaultCategoryLimits
) {
    val monthStart = remember { ArusKasViewModel.getStartOfMonthMillis() }
    val monthExpenses = remember(transactions, monthStart) {
        transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= monthStart }
    }
    val monthIncomes = remember(transactions, monthStart) {
        transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= monthStart }
    }
    val totalMonthExpense = remember(monthExpenses) { monthExpenses.sumOf { it.amount } }
    val totalMonthIncome = remember(monthIncomes) { monthIncomes.sumOf { it.amount } }

    // Real-life Gen Z unit conversions (Kopi @22rb, Seblak/Mie @18rb, Bensin @10rb)
    val coffeeCupsEq = (totalMonthExpense / 22_000L).coerceAtLeast(if (totalMonthExpense > 0L) 1L else 0L)
    val seblakBowlsEq = (totalMonthExpense / 18_000L).coerceAtLeast(if (totalMonthExpense > 0L) 1L else 0L)
    val bensinLitersEq = (totalMonthExpense / 10_000L).coerceAtLeast(if (totalMonthExpense > 0L) 1L else 0L)

    // Battery / Survival Meter Calculation
    val totalPool = (totalBalance + totalMonthExpense).coerceAtLeast(1L)
    val batteryPct = ((totalBalance.toFloat() / totalPool.toFloat()) * 100f).toInt().coerceIn(0, 100)
    val batteryColor = when {
        batteryPct >= 60 -> EmeraldPrimary
        batteryPct >= 30 -> GoldAccent
        else -> CoralExpense
    }
    val batteryVibeText = when {
        batteryPct >= 70 -> "🔋 $batteryPct% Full Senyum (Masih Seger)"
        batteryPct >= 40 -> "⚡ $batteryPct% Mode Waspada (Rem Jajan)"
        else -> "🪫 $batteryPct% Lowbat Parah (Mode Hemat Darurat!)"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.2.dp, batteryColor.copy(alpha = 0.55f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("genz_graphical_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. HEADER: RADAR DOMPET GEN Z + TOMBOL TUTUP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "⚡ RADAR VISUAL DOMPET LO",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = batteryColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = when (mode) {
                            "HARIAN" -> "Mood Bakar Duit 7 Hari Terakhir"
                            "ADU_CUAN" -> "Battle: Duit Masuk vs Duit Keluar"
                            else -> "Peta Dompet Bocor Bulan Ini"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    onClick = onClose,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Tutup ✕",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 2. BATERAI NYAWA DOMPET & KONVERSI KEHIDUPAN NYATA (Kopi, Seblak, Bensin)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                border = BorderStroke(0.8.dp, batteryColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = "Nyawa Dompet Harian Lo:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = batteryVibeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                            color = batteryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Segmented Battery Capsule Bar (10 kapsul baterai biar gampang kebayang)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val filledSegments = (batteryPct / 10).coerceIn(1, 10)
                        for (i in 1..10) {
                            val active = i <= filledSegments
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (active) batteryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                        }
                    }

                    // Konversi Kehidupan Sehari-hari ("Pengeluaran bulan ini setara apa sih?")
                    Text(
                        text = "Bulan ini lo udah ngeluarin ${ArusKasViewModel.formatCompactRupiah(totalMonthExpense)}, itu setara sama:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.4.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RealLifeEquivalentBadge(
                            modifier = Modifier.weight(1f),
                            emoji = "☕",
                            value = "$coffeeCupsEq Cup",
                            subtitle = "Es Kopi Susu",
                            accent = GoldAccent
                        )
                        RealLifeEquivalentBadge(
                            modifier = Modifier.weight(1f),
                            emoji = "🍜",
                            value = "$seblakBowlsEq Porsi",
                            subtitle = "Seblak / Mie",
                            accent = CoralExpense
                        )
                        RealLifeEquivalentBadge(
                            modifier = Modifier.weight(1f),
                            emoji = "⛽",
                            value = "$bensinLitersEq Liter",
                            subtitle = "Bensin Motor",
                            accent = SkyBlue
                        )
                    }
                }
            }

            // 3. PILIHAN MODE VISUAL GEN Z (3 Tab yang Gampang Dipahami)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(11.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    "HARIAN" to "🔥 7 Hari Vibe",
                    "BULANAN" to "🕳️ Peta Bocor",
                    "ADU_CUAN" to "⚡ Cuan vs Boncos"
                )
                tabs.forEach { (tabKey, tabLabel) ->
                    val isSelected = mode == tabKey
                    Surface(
                        onClick = { onModeChange(tabKey) },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isSelected) GoldAccent else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tabLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isSelected) Color(0xFF1F1400) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            // 4. ISI GRAFIK SESUAI TAB YANG DIPILIH
            when (mode) {
                "HARIAN" -> {
                    val dailyStats = remember(transactions) {
                        val dayShortFmt = SimpleDateFormat("EEE", Locale("in", "ID"))
                        val fullDateFmt = SimpleDateFormat("EEEE, dd MMM", Locale("in", "ID"))
                        (6 downTo 0).map { daysAgo ->
                            val c = Calendar.getInstance()
                            c.add(Calendar.DAY_OF_YEAR, -daysAgo)
                            c.set(Calendar.HOUR_OF_DAY, 0)
                            c.set(Calendar.MINUTE, 0)
                            c.set(Calendar.SECOND, 0)
                            c.set(Calendar.MILLISECOND, 0)
                            val start = c.timeInMillis
                            val end = start + 86_400_000L
                            val dayTxs = transactions.filter { it.transactionDateMillis in start until end }
                            val expTxs = dayTxs.filter { it.type == "EXPENSE" }
                            val incTxs = dayTxs.filter { it.type == "INCOME" }
                            val sumExp = expTxs.sumOf { it.amount }
                            val sumInc = incTxs.sumOf { it.amount }
                            val topCat = expTxs
                                .groupBy { it.categoryLabel.ifBlank { "Pengeluaran" } }
                                .maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                                ?.key ?: "Nggak ada jajan"
                            GenZDayStat(
                                dayShort = if (daysAgo == 0) "Skrg" else dayShortFmt.format(Date(start)),
                                fullDateLabel = if (daysAgo == 0) "Hari Ini (${fullDateFmt.format(Date(start))})" else fullDateFmt.format(Date(start)),
                                expenseAmount = sumExp,
                                incomeAmount = sumInc,
                                topCategoryLabel = topCat,
                                txCount = expTxs.size,
                                isToday = daysAgo == 0
                            )
                        }
                    }

                    // Pilih hari untuk di-inspect (default hari ini / index terakhir)
                    var selectedDayIndex by remember { mutableStateOf(6) }
                    val selectedDay = dailyStats.getOrElse(selectedDayIndex) { dailyStats.last() }

                    // Batas aman harian = Rp 100.000 (atau rata-rata limit)
                    val safeDailyLimit = 100_000L
                    val maxVal = maxOf(dailyStats.maxOfOrNull { it.expenseAmount } ?: 1L, safeDailyLimit, 100_000L)
                    val avgDaily = dailyStats.sumOf { it.expenseAmount } / 7L

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ketuk tiang hari buat liat detail bocornya 👇",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Rata-rata: ${ArusKasViewModel.formatCompactRupiah(avgDaily)}/hr",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Bar Chart Kapsul Modern dengan Emoji Mood di Atas Tiap Hari
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(136.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            dailyStats.forEachIndexed { idx, stat ->
                                val isSel = idx == selectedDayIndex
                                val ratio = (stat.expenseAmount.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
                                val moodEmoji = when {
                                    stat.expenseAmount == 0L -> "🧊"
                                    stat.expenseAmount <= 50_000L -> "😎"
                                    stat.expenseAmount <= safeDailyLimit -> "☕"
                                    else -> "🔥"
                                }
                                val barColor = when {
                                    stat.expenseAmount == 0L -> SkyBlue.copy(alpha = 0.45f)
                                    stat.expenseAmount <= 50_000L -> EmeraldPrimary
                                    stat.expenseAmount <= safeDailyLimit -> GoldAccent
                                    else -> CoralExpense
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSel) barColor.copy(alpha = 0.14f) else Color.Transparent
                                        )
                                        .clickable { selectedDayIndex = idx }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Text(
                                        text = moodEmoji,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                    )
                                    Text(
                                        text = if (stat.expenseAmount > 0L) {
                                            ArusKasViewModel.formatCompactRupiah(stat.expenseAmount).replace("Rp ", "")
                                        } else {
                                            "0"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = if (isSel) barColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(if (isSel) 22.dp else 18.dp)
                                            .height((64 * ratio).dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        barColor,
                                                        barColor.copy(alpha = 0.45f)
                                                    )
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stat.dayShort,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                        color = if (isSel || stat.isToday) barColor else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSel || stat.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Keterangan Emoji & Detail Hari yang Dipilih
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📌 ${selectedDay.fullDateLabel}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (selectedDay.expenseAmount > 0L) {
                                            "Keluar ${ArusKasViewModel.formatRupiah(selectedDay.expenseAmount)} (${selectedDay.txCount}x) • Terbanyak: ${selectedDay.topCategoryLabel}"
                                        } else {
                                            "🧊 Nggak keluar duit blas di hari ini! Dompet full senyum."
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.6.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (selectedDay.incomeAmount > 0L) {
                                    Surface(
                                        color = EmeraldPrimary.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "+${ArusKasViewModel.formatCompactRupiah(selectedDay.incomeAmount)}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.2.sp),
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Arti Mood: 🧊 Rp 0 (Puasa Jajan) • 😎 ≤50rb (Santuy) • ☕ ≤100rb (Wajar) • 🔥 >100rb (Boros!)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                "ADU_CUAN" -> {
                    // TAB 3: BATTLE DUIT MASUK VS DUIT KELUAR VS TABUNGAN
                    val totalFlow = (totalMonthIncome + totalMonthExpense + totalTabunganSaved).coerceAtLeast(1L)
                    val netDiff = totalMonthIncome - totalMonthExpense
                    val isSurplus = netDiff >= 0L

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = (if (isSurplus) EmeraldPrimary else CoralExpense).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, (if (isSurplus) EmeraldPrimary else CoralExpense).copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isSurplus) {
                                        "🏆 LO MENANG BULAN INI! (Surplus +${ArusKasViewModel.formatRupiah(netDiff)})"
                                    } else {
                                        "💀 AWAS KENA COMEBACK! (Besar Pasak dari Tiang -${ArusKasViewModel.formatRupiah(kotlin.math.abs(netDiff))})"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSurplus) EmeraldPrimary else CoralExpense,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isSurplus) {
                                        "Pemasukan lo lebih gede dari pengeluaran cuy! Pertahanin biar sisa uangnya bisa langsung diamankan ke Tabungan Target."
                                    } else {
                                        "Woy pengeluaran bulan ini udah ngebalap uang masuk lo! Stop checkout yang nggak penting sekarang juga."
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.6.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Tug-of-War Visual Bar
                        BattleComparisonRow(
                            label = "🟢 Cuan Masuk (Gaji, Ngojek, Usaha)",
                            amount = totalMonthIncome,
                            ratio = (totalMonthIncome.toFloat() / totalFlow.toFloat()).coerceIn(0.05f, 1f),
                            color = EmeraldPrimary
                        )
                        BattleComparisonRow(
                            label = "🔴 Duit Keluar (Jajan, Pokok, Tagihan)",
                            amount = totalMonthExpense,
                            ratio = (totalMonthExpense.toFloat() / totalFlow.toFloat()).coerceIn(0.05f, 1f),
                            color = CoralExpense
                        )
                        BattleComparisonRow(
                            label = "🛡️ Diamankan (Tabungan Target + Darurat)",
                            amount = totalTabunganSaved + emergencyBalance,
                            ratio = ((totalTabunganSaved + emergencyBalance).toFloat() / (totalFlow + emergencyBalance).coerceAtLeast(1L).toFloat()).coerceIn(0.05f, 1f),
                            color = GoldAccent
                        )
                    }
                }

                else -> {
                    // TAB 2 ("BULANAN"): PETA BOCOR DOMPET (Stacked DNA Bar + Top Tersangka + Real-Life Analogy)
                    val grouped = remember(monthExpenses) {
                        monthExpenses
                            .groupBy { it.categoryKey }
                            .mapValues { entry -> entry.value.sumOf { it.amount } }
                    }
                    val totalMonth = grouped.values.sum().coerceAtLeast(1L)
                    val catColors = listOf(
                        SkyBlue,
                        EmeraldPrimary,
                        Color(0xFFE879F9),
                        CoralExpense,
                        GoldAccent,
                        Color(0xFF38BDF8)
                    )

                    val sortedCategories = remember(grouped) {
                        PresetCategories.expenseGrid3x3.mapIndexed { idx, cat ->
                            Triple(cat, grouped[cat.key] ?: 0L, catColors[idx % catColors.size])
                        }.sortedByDescending { it.second }
                    }
                    val topCulprit = sortedCategories.firstOrNull()

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Top Tersangka Utama Callout
                        if (topCulprit != null && topCulprit.second > 0L) {
                            val topPct = ((topCulprit.second * 100L) / totalMonth).toInt()
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CoralExpense.copy(alpha = 0.12f),
                                border = BorderStroke(0.9.dp, CoralExpense.copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🚨", fontSize = 16.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "TERSANGKA #1 DOMPET BOCOR: ${topCulprit.first.label.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                            color = CoralExpense,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Nyerap $topPct% (${ArusKasViewModel.formatRupiah(topCulprit.second)}) dari seluruh pengeluaran bulan ini!",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.4.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Single Multi-Color DNA Bar (Proporsi semua kategori dalam 1 bar utuh)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            sortedCategories.forEach { (_, amt, color) ->
                                val weightVal = (amt.toFloat() / totalMonth.toFloat()).coerceAtLeast(if (amt > 0L) 0.04f else 0f)
                                if (weightVal > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .weight(weightVal)
                                            .fillMaxSize()
                                            .background(color)
                                    )
                                }
                            }
                        }

                        // Daftar Kategori dengan Real-Life Analogy & Status Limit
                        sortedCategories.forEach { (cat, amt, color) ->
                            val pct = ((amt.toFloat() / totalMonth.toFloat()) * 100f).toInt().coerceIn(0, 100)
                            val limitCfg = categoryLimits[cat.key]
                            val limitAmt = limitCfg?.limitAmount ?: cat.defaultLimitAmount
                            val limitRatio = if (limitAmt > 0L) (amt.toFloat() / limitAmt.toFloat()) else 0f
                            val realLifeHint = when (cat.key) {
                                "gaya_hidup" -> "≈ ${(amt / 22_000L)} cup kopi"
                                "kebutuhan_pokok" -> "≈ ${(amt / 10_000L)}L bensin/pokok"
                                "rawat_diri" -> "≈ ${(amt / 50_000L)} item skincare"
                                "tagihan_bulanan" -> "tagihan wajib"
                                else -> "≈ ${(amt / 20_000L)}x transaksi"
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Text(
                                                text = cat.label,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.2.sp),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "($pct% • $realLifeHint)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = ArusKasViewModel.formatCompactRupiah(amt),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.2.sp),
                                            color = color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { (amt.toFloat() / totalMonth.toFloat()).coerceIn(0f, 1f) },
                                        color = color,
                                        trackColor = color.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(50))
                                    )

                                    if (limitAmt > 0L) {
                                        Text(
                                            text = "Target Limit Kategori: ${(limitRatio * 100).toInt()}% dari ${ArusKasViewModel.formatCompactRupiah(limitAmt)}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.8.sp),
                                            color = if (limitRatio >= 1f) CoralExpense else MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun RealLifeEquivalentBadge(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    subtitle: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = accent.copy(alpha = 0.1f),
        border = BorderStroke(0.7.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$emoji $value",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                color = accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BattleComparisonRow(
    label: String,
    amount: Long,
    ratio: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = ArusKasViewModel.formatRupiah(amount),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { ratio.coerceIn(0f, 1f) },
            color = color,
            trackColor = color.copy(alpha = 0.16f),
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}

@Composable
fun TabunganTargetVisualRadarCard(
    activeGoal: SavingsGoal?,
    allGoals: List<SavingsGoal>,
    wallets: List<Wallet>,
    transactions: List<TransactionRecord>,
    mode: String,
    onModeChange: (String) -> Unit,
    onSelectGoal: (SavingsGoal) -> Unit,
    onAllocateSavings: (goal: SavingsGoal, wallet: Wallet, amount: Long, note: String) -> Unit,
    onOpenCreateTarget: () -> Unit,
    onDeleteGoal: (SavingsGoal) -> Unit,
    onClose: () -> Unit
) {
    val saved = activeGoal?.savedAmount ?: 0L
    val target = (activeGoal?.targetAmount ?: 1L).coerceAtLeast(1L)
    val remaining = (target - saved).coerceAtLeast(0L)
    val progress = if (activeGoal != null) (saved.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val goalPct = (progress * 100f).toInt().coerceIn(0, 100)
    val dailyRec = (activeGoal?.dailyRecommendation ?: 40_000L).coerceAtLeast(10_000L)

    // Real-life Gen Z conversions for the target
    val coffeeCupsRemaining = (remaining / 22_000L).coerceAtLeast(if (remaining > 0L) 1L else 0L)
    val daysLeftEstimate = if (remaining > 0L) ceil(remaining.toDouble() / dailyRec.toDouble()).toInt() else 0
    val totalAllTargetsSaved = remember(allGoals) { allGoals.sumOf { it.savedAmount } }

    val radarAccent = when {
        activeGoal == null -> GoldAccent
        goalPct >= 100 -> EmeraldPrimary
        goalPct >= 50 -> EmeraldPrimary
        else -> GoldAccent
    }
    val statusVibeText = when {
        activeGoal == null -> "🎯 Belum ada target, gas bikin dulu!"
        remaining <= 0L -> "🎉 100% LUNAS! Siap checkout impian lo!"
        goalPct >= 75 -> "🔥 $goalPct% Dikit lagi kebeli cuy!"
        goalPct >= 30 -> "⚡ $goalPct% On Track! Jangan kendor!"
        else -> "🚀 $goalPct% Baru mulai, konsisten ya bro!"
    }

    var selectedWalletId by remember(wallets) { mutableStateOf(wallets.firstOrNull()?.id ?: "") }
    var depositAmountStr by remember(activeGoal) {
        mutableStateOf(dailyRec.toString())
    }
    var depositNoteStr by remember(activeGoal) {
        mutableStateOf("Tabungan Target: ${activeGoal?.title ?: "Impian"} (Simpan di Rekening Terpisah)")
    }

    val savingsHistory = remember(transactions) {
        transactions.filter { it.type == "SAVINGS_ALLOCATION" }
    }
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("in", "ID")) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.2.dp, radarAccent.copy(alpha = 0.55f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("genz_target_visual_radar_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. HEADER: RADAR VISUAL TABUNGAN TARGET + TOMBOL + TARGET & TUTUP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎯 RADAR VISUAL TABUNGAN TARGET LO",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = radarAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (activeGoal != null) {
                            "Target Impian: ${activeGoal.title}"
                        } else {
                            "Mulai Rakit Tabungan Impian Lo"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = onOpenCreateTarget,
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("quick_add_target_button")
                    ) {
                        Text(
                            text = "+ Target",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        onClick = onClose,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Tutup ✕",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Switcher kalau punya lebih dari 1 target
            if (allGoals.size > 1 && activeGoal != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allGoals.forEach { g ->
                        val isSel = g.id == activeGoal.id
                        val gPct = ((g.savedAmount.toFloat() / g.targetAmount.coerceAtLeast(1L).toFloat()) * 100f).toInt().coerceIn(0, 100)
                        Surface(
                            onClick = { onSelectGoal(g) },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🎯 ${g.title} ($gPct%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (activeGoal == null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                    border = BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lo belum punya Tabungan Target aktif nih cuy!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "Bikin target (misal: Beli Laptop, Upgrade HP, atau Liburan) supaya uang tabungan lo terpisah dari saldo jajan harian.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onOpenCreateTarget,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF1F1400)
                            )
                        ) {
                            Text("+ Buat Target Impian Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // 2. BATERAI PROGRESS IMPIAN & KONVERSI KEHIDUPAN NYATA (Persis Seperti UI/UX Grafik!)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                    border = BorderStroke(0.8.dp, radarAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
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
                            Text(
                                text = "Terkumpul: ${ArusKasViewModel.formatCompactRupiah(saved)} / ${ArusKasViewModel.formatCompactRupiah(target)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = statusVibeText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = radarAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 10-Capsule Segmented Battery Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            val filledSegments = if (goalPct == 0) 0 else (goalPct / 10).coerceIn(1, 10)
                            for (i in 1..10) {
                                val active = i <= filledSegments
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(7.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (active) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                )
                            }
                        }

                        Text(
                            text = if (remaining > 0L) {
                                "Kurang ${ArusKasViewModel.formatRupiah(remaining)} lagi • ⚠️ Simpan di rekening/kantong terpisah dari uang jajan!"
                            } else {
                                "Target udah 100% terkumpul aman di kantong tabungan terpisah!"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.2.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 3 Real-Life Equivalent Badges (Kopi, Estimasi Hari, Total Diamankan)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RealLifeEquivalentBadge(
                                modifier = Modifier.weight(1f),
                                emoji = "☕",
                                value = "$coffeeCupsRemaining Cup",
                                subtitle = "Sisa Setara Kopi",
                                accent = GoldAccent
                            )
                            RealLifeEquivalentBadge(
                                modifier = Modifier.weight(1f),
                                emoji = "⏳",
                                value = if (daysLeftEstimate > 0) "±$daysLeftEstimate Hari" else "Lunas!",
                                subtitle = "Estimasi Lunas",
                                accent = SkyBlue
                            )
                            RealLifeEquivalentBadge(
                                modifier = Modifier.weight(1f),
                                emoji = "🛡️",
                                value = ArusKasViewModel.formatCompactRupiah(totalAllTargetsSaved).replace("Rp ", ""),
                                subtitle = "Aman Terpisah",
                                accent = EmeraldPrimary
                            )
                        }
                    }
                }

                // 3. PILIHAN MODE VISUAL TABUNGAN TARGET (3 Tab Interaktif Persis Seperti Grafik)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val targetTabs = listOf(
                        "SETOR" to "⚡ Gas Setor",
                        "SIMULASI" to "🚀 Simulasi Lunas",
                        "KELOLA" to "📜 Jejak & Kelola"
                    )
                    targetTabs.forEach { (tabKey, tabLabel) ->
                        val isSelected = mode == tabKey
                        Surface(
                            onClick = { onModeChange(tabKey) },
                            shape = RoundedCornerShape(9.dp),
                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = tabLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (isSelected) Color(0xFF042619) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                // 4. ISI KONTEN SESUAI TAB RADAR TABUNGAN TARGET
                when (mode) {
                    "SIMULASI" -> {
                        // TAB 2: SIMULASI KECEPATAN LUNAS ALA GEN Z (Bar Perbandingan Visual)
                        val paceSantuy = 20_000L
                        val paceNormal = dailyRec.coerceAtLeast(35_000L)
                        val paceSultan = (dailyRec * 2L).coerceAtLeast(75_000L)

                        val daysSantuy = if (remaining > 0L) ceil(remaining.toDouble() / paceSantuy.toDouble()).toInt() else 0
                        val daysNormal = if (remaining > 0L) ceil(remaining.toDouble() / paceNormal.toDouble()).toInt() else 0
                        val daysSultan = if (remaining > 0L) ceil(remaining.toDouble() / paceSultan.toDouble()).toInt() else 0

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                border = BorderStroke(0.9.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "💡 Kapan ${activeGoal.title} Lo Bakal Kebeli?",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Makin rajin lo nahan jajan nggak penting & mindahin ke rekening tabungan terpisah, makin cepet target ini lunas cuy!",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            BattleComparisonRow(
                                label = "🐢 Mode Santuy (${ArusKasViewModel.formatCompactRupiah(paceSantuy)}/hr ≈ Tahan 1 Kopi) → ±$daysSantuy Hari",
                                amount = paceSantuy,
                                ratio = 0.42f,
                                color = SkyBlue
                            )
                            BattleComparisonRow(
                                label = "⚡ Mode Ideal (${ArusKasViewModel.formatCompactRupiah(paceNormal)}/hr ≈ Rekomendasi) → ±$daysNormal Hari",
                                amount = paceNormal,
                                ratio = 0.72f,
                                color = GoldAccent
                            )
                            BattleComparisonRow(
                                label = "🔥 Mode Ngebut (${ArusKasViewModel.formatCompactRupiah(paceSultan)}/hr ≈ Gas Pol) → ±$daysSultan Hari",
                                amount = paceSultan,
                                ratio = 1f,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    "KELOLA" -> {
                        // TAB 3: JEJAK SETORAN & KELOLA TARGET
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Jejak Setoran Tabungan Terakhir:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.8.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                                TextButton(
                                    onClick = { onDeleteGoal(activeGoal) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Hapus Target Ini",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = CoralExpense,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (savingsHistory.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Belum ada riwayat setoran tabungan tercatat. Yuk mulai setor dari tab '⚡ Gas Setor'!",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            } else {
                                savingsHistory.take(4).forEach { tx ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = tx.note.ifBlank { tx.categoryLabel },
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Potong dari ${tx.walletName} • ${dateFormat.format(Date(tx.transactionDateMillis))}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "+${ArusKasViewModel.formatCompactRupiah(tx.amount)}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.2.sp),
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // TAB 1 ("SETOR"): GAS SETOR TABUNGAN LANGSUNG DI DALAM KARTU RADAR
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Pilihan Cepat Nominal Setoran ala Gen Z
                            val quickAmounts = listOf(
                                25_000L to "☕ Seharga 1 Kopi (25rb)",
                                dailyRec to "⚡ Jatah Harian (${ArusKasViewModel.formatCompactRupiah(dailyRec).replace("Rp ", "")})",
                                100_000L to "🔥 Gas Seratus (100rb)"
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickAmounts.forEach { (amtVal, labelStr) ->
                                    val isChosen = depositAmountStr == amtVal.toString()
                                    Surface(
                                        onClick = { depositAmountStr = amtVal.toString() },
                                        shape = RoundedCornerShape(50),
                                        color = if (isChosen) GoldAccent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            0.9.dp,
                                            if (isChosen) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                        )
                                    ) {
                                        Text(
                                            text = labelStr,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.4.sp),
                                            color = if (isChosen) GoldAccent else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Pilih Potong dari Dompet Mana
                            if (wallets.isNotEmpty()) {
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
                                            color = if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "${w.name}: ${ArusKasViewModel.formatCompactRupiah(w.balance)}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                                    color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Input Nominal Setor & Tombol Eksekusi Potong Saldo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = depositAmountStr,
                                    onValueChange = { depositAmountStr = it.filter { c -> c.isDigit() } },
                                    label = { Text("Nominal Tabung (Rp)") },
                                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                                    visualTransformation = RupiahThousandsVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("deposit_target_amount_input")
                                )

                                Button(
                                    onClick = {
                                        val w = wallets.find { it.id == selectedWalletId } ?: wallets.firstOrNull()
                                        val amt = depositAmountStr.toLongOrNull() ?: 0L
                                        if (w != null && amt > 0L) {
                                            onAllocateSavings(activeGoal, w, amt, depositNoteStr)
                                        }
                                    },
                                    enabled = wallets.isNotEmpty() && (depositAmountStr.toLongOrNull() ?: 0L) > 0L,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary,
                                        contentColor = Color(0xFF042619)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("confirm_deposit_target_button")
                                ) {
                                    Text(
                                        text = "Potong & Tabung",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
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

@Composable
fun TransactionRowCard(
    tx: TransactionRecord,
    onDelete: () -> Unit
) {
    val isIncome = tx.type == "INCOME"
    val isSavings = tx.type == "SAVINGS_ALLOCATION"
    val isTransfer = tx.type == "TRANSFER"
    val amountColor = when {
        isIncome -> EmeraldPrimary
        isSavings -> GoldAccent
        isTransfer -> SkyBlue
        else -> CoralExpense
    }
    val prefix = when {
        isIncome -> "+"
        isTransfer -> "⇄ "
        else -> "-"
    }
    val dateStr = remember(tx.transactionDateMillis) {
        SimpleDateFormat("dd MMM, HH:mm", Locale("in", "ID")).format(Date(tx.transactionDateMillis))
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = glassmorphicBorderStroke(accentColor = amountColor, width = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCardEffect(cornerRadius = 16.dp, accentTint = amountColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = amountColor.copy(alpha = 0.16f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = iconForCategory(tx.categoryKey),
                            contentDescription = tx.categoryLabel,
                            tint = amountColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = tx.note.ifBlank { tx.categoryLabel },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${tx.categoryLabel} • ${tx.walletName} • $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$prefix${ArusKasViewModel.formatRupiah(tx.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus transaksi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

data class BestieFinancialInsight(
    val saldoSymbolColor: Color,
    val saldoBadge: String,
    val saldoComment: String,
    val nominalSymbolColor: Color,
    val notificationBadge: String,
    val nominalBoxAnalysis: String,
    val categorySymbolColor: Color,
    val categoryComment: String
)

fun buildDynamicBestieInsight(
    totalBalance: Long,
    totalTabunganSaved: Long,
    emergencyBalance: Long,
    todayExpense: Long,
    monthExpense: Long,
    monthIncome: Long,
    selectedCategory: QuickCategoryItem,
    categoryLimits: Map<String, CategoryLimitConfig>,
    transactions: List<TransactionRecord>,
    goals: List<SavingsGoal>,
    sessionSeed: Int,
    guruState: GuruAdviceState
): BestieFinancialInsight {
    val activeGoal = goals.firstOrNull()
    val goalTitle = activeGoal?.title ?: "Target Impian"
    val goalRemaining = if (activeGoal != null) (activeGoal.targetAmount - activeGoal.savedAmount).coerceAtLeast(0L) else 0L

    // 1. Kotak Total Saldo evaluation
    val (saldoColor, saldoBadge, localSaldoComments) = when {
        totalBalance <= 150_000L -> Triple(
            CoralExpense,
            "🔴 SALDO TIPIS",
            listOf(
                "Waduh bestie, saldo siap pakai tinggal ${ArusKasViewModel.formatCompactRupiah(totalBalance)} nih, mode bertahan hidup aktif ya! 😂",
                "Dompet harian lagi diet ketat bro (${ArusKasViewModel.formatCompactRupiah(totalBalance)}), jangan coba-coba lirik promo dulu!",
                "Sisa ${ArusKasViewModel.formatCompactRupiah(totalBalance)} di kantong aktif, untung tabungan & darurat udah dipisah aman!"
            )
        )
        totalTabunganSaved == 0L && emergencyBalance == 0L -> Triple(
            GoldAccent,
            "🟡 BELUM DIPISAH",
            listOf(
                "Duit siap pakai ${ArusKasViewModel.formatCompactRupiah(totalBalance)} ngumpul semua nih bro, sisihin dikit ke Tabungan & Darurat yuk!",
                "Dompet tebel sih (${ArusKasViewModel.formatCompactRupiah(totalBalance)}), tapi bahaya kalau kecampur semua tanpa pos darurat bestie 😆",
                "Yuk amankan sebagian dari ${ArusKasViewModel.formatCompactRupiah(totalBalance)} ke kantong terpisah biar nggak ludes tak sengaja!"
            )
        )
        todayExpense > 150_000L -> Triple(
            GoldAccent,
            "🟡 REM DIKIT BRO",
            listOf(
                "Hari ini udah keluar ${ArusKasViewModel.formatCompactRupiah(todayExpense)} bestie, rem dikit biar saldo ${ArusKasViewModel.formatCompactRupiah(totalBalance)} awet!",
                "Jempolmu lagi lincah banget hari ini (${ArusKasViewModel.formatCompactRupiah(todayExpense)}), kasihan dompet kalau digas terus 😂",
                "Saldo masih ${ArusKasViewModel.formatCompactRupiah(totalBalance)}, tapi pengeluaran hari ini udah lumayan ngebut nih bro!"
            )
        )
        else -> Triple(
            EmeraldPrimary,
            "🟢 DOMPET SEGER",
            listOf(
                "Saldo siap pakai ${ArusKasViewModel.formatCompactRupiah(totalBalance)} aman terkendali bosku, tabungan & darurat juga udah rapi!",
                "Nah gini dong bestie, duit harian ${ArusKasViewModel.formatCompactRupiah(totalBalance)} jelas posnya, tidur pun nyenyak 😎",
                "Pertahankan bro! Saldo aktif ${ArusKasViewModel.formatCompactRupiah(totalBalance)} sehat, tinggal gas cicil $goalTitle pelan-pelan."
            )
        )
    }

    // 2. Category evaluation
    val isIncome = selectedCategory.type == "INCOME"
    val activeLimitCfg = categoryLimits[selectedCategory.key] ?: CategoryLimitConfig(
        categoryKey = selectedCategory.key,
        limitAmount = selectedCategory.defaultLimitAmount,
        period = selectedCategory.defaultLimitPeriod
    )
    val catSpent = ArusKasViewModel.calculateSpentForCategoryLimit(
        categoryKey = selectedCategory.key,
        period = activeLimitCfg.period,
        transactions = transactions
    )
    val catLimit = activeLimitCfg.limitAmount
    val catRatio = if (catLimit > 0L) (catSpent.toFloat() / catLimit.toFloat()) else 0f

    val (catColor, catComment) = if (isIncome) {
        EmeraldPrimary to when (sessionSeed % 3) {
            0 -> "🟢 Mantap bestie! Begitu uang ${selectedCategory.label} masuk, langsung amankan jatah $goalTitle sebelum numpang lewat doang 😆"
            1 -> "🟢 Rezeki ${selectedCategory.label} mengalir nih bro! Sisihkan minimal 10-20% ke kantong terpisah biar masa depan cerah 🔥"
            else -> "🟢 Cuan dari ${selectedCategory.label} siap dicatat! Ingat rumus orang kaya: nabung di awal, bukan nunggu sisa jajan ya bosku!"
        }
    } else {
        when {
            catLimit > 0L && catRatio >= 1f -> CoralExpense to "🔴 ALARM MERAH BESTIE! Pos ${selectedCategory.label} udah jebol ${(catRatio * 100).toInt()}% dari limit, stop dulu kalau nggak darurat banget 😭"
            catLimit > 0L && catRatio >= 0.75f -> GoldAccent to "🟡 Awas lampu kuning bro! Pos ${selectedCategory.label} udah kepake ${(catRatio * 100).toInt()}%, tahan godaan checkout biar nggak over limit!"
            catLimit > 0L && catRatio >= 0.4f -> SkyBlue to "🔵 Pos ${selectedCategory.label} baru jalan ${(catRatio * 100).toInt()}% sih bestie, masih aman asal jangan lapar mata pas jam rawan 🤣"
            else -> EmeraldPrimary to "🟢 Pos ${selectedCategory.label} masih hijau seger (${(catRatio * 100).toInt()}% dari limit). Tetap bijak ya bro demi kejar $goalTitle!"
        }
    }

    // 3. Minimalist Rectangular Analysis Box below Nominal Input (Rotates every time the app opens!)
    val localAnalysisVariations = listOf(
        Triple(
            if (todayExpense > 100_000L) GoldAccent else EmeraldPrimary,
            "🔔 PENGINGAT BESTIE #${(sessionSeed % 9) + 1}",
            "Bro, rasio kas aktifmu sekarang ${ArusKasViewModel.formatCompactRupiah(totalBalance)} dengan tabungan terpisah ${ArusKasViewModel.formatCompactRupiah(totalTabunganSaved)}. Hari ini udah keluar ${ArusKasViewModel.formatCompactRupiah(todayExpense)}—jangan biarin recehan kopi & jajan diem-diem nyabotase $goalTitle ya, dikit lagi lunas tuh! 😎"
        ),
        Triple(
            if (monthExpense > monthIncome && monthIncome > 0L) CoralExpense else SkyBlue,
            "📊 ANALISIS TEMEN DEKET",
            "Secara finansial nih bestie, pengeluaran bulan ini ${ArusKasViewModel.formatCompactRupiah(monthExpense)} dan dana daruratmu ${ArusKasViewModel.formatCompactRupiah(emergencyBalance)}. Kalau gatel pengen jajan yang nggak penting, inget rasanya nyesel pas cek mutasi di akhir bulan ya hahaha 🤣"
        ),
        Triple(
            GoldAccent,
            "💡 SARAN CERDAS HARI INI",
            "Cek fakta dompetmu bro: total semua duitmu ${ArusKasViewModel.formatCompactRupiah(totalBalance + totalTabunganSaved + emergencyBalance)}, tapi yang boleh diutak-atik cuma ${ArusKasViewModel.formatCompactRupiah(totalBalance)}. Sisihkan Rp 25rb–40rb hari ini ke $goalTitle (kurang ${ArusKasViewModel.formatCompactRupiah(goalRemaining)} lagi) biar makin cepet pamer hasil tabungan!"
        ),
        Triple(
            if (catRatio >= 0.75f) CoralExpense else EmeraldPrimary,
            "🔥 KRITIK & SEMANGAT",
            "Jujur-jujuran aja nih bestie, ngatur duit di BCA, Bank Mas, sama e-wallet itu paling rawan bocor halus kalau males nyatet! Untung lu rajin buka aplikasi ini—pertahanin disiplin limit kategorinya biar dompet tetep ganteng sampai gajian berikutnya 💪"
        )
    )

    val pickedLocal = localAnalysisVariations[kotlin.math.abs(sessionSeed) % localAnalysisVariations.size]
    val pickedSaldoComment = localSaldoComments[kotlin.math.abs(sessionSeed) % localSaldoComments.size]

    return if (guruState is GuruAdviceState.Success) {
        val aiColor = when (guruState.colorLevel.uppercase(Locale.ROOT)) {
            "RED" -> CoralExpense
            "YELLOW" -> GoldAccent
            "BLUE" -> SkyBlue
            else -> EmeraldPrimary
        }
        BestieFinancialInsight(
            saldoSymbolColor = aiColor,
            saldoBadge = guruState.statusBadge.take(18),
            saldoComment = guruState.saldoCritique.ifBlank { pickedSaldoComment },
            nominalSymbolColor = aiColor,
            notificationBadge = "🔔 ${guruState.statusBadge}",
            nominalBoxAnalysis = guruState.nominalBoxAnalysis.ifBlank { "${guruState.teguran} ${guruState.langkahKonkret}" },
            categorySymbolColor = catColor,
            categoryComment = if (guruState.categoryCritique.isNotBlank() && sessionSeed % 2 == 0) {
                "💡 ${guruState.categoryCritique}"
            } else {
                catComment
            }
        )
    } else {
        BestieFinancialInsight(
            saldoSymbolColor = saldoColor,
            saldoBadge = saldoBadge,
            saldoComment = pickedSaldoComment,
            nominalSymbolColor = pickedLocal.first,
            notificationBadge = pickedLocal.second,
            nominalBoxAnalysis = pickedLocal.third,
            categorySymbolColor = catColor,
            categoryComment = catComment
        )
    }
}

fun buildLiveNominalBestieReaction(
    typedAmount: Long,
    isIncomeMode: Boolean,
    isEmergencyExpenseSelected: Boolean,
    selectedCategoryLabel: String,
    totalBalance: Long,
    emergencyBalance: Long
): Triple<Color, String, String>? {
    if (typedAmount <= 0L) return null
    val formatted = ArusKasViewModel.formatRupiah(typedAmount)
    return when {
        isIncomeMode -> Triple(
            EmeraldPrimary,
            "🟢 CUAN MASUK!",
            "Asik masuk $formatted dari $selectedCategoryLabel nih bosku! Begitu masuk, langsung amankan 10–20% ke Tabungan Target atau Darurat biar nggak numpang lewat doang di dompet ya 😎"
        )
        isEmergencyExpenseSelected -> {
            if (typedAmount > emergencyBalance) {
                Triple(
                    CoralExpense,
                    "🔴 LEBIH DARI SALDO DARURAT!",
                    "Waduh bestie, pengeluaran darurat $formatted ini lebih gede dari Simpanan Daruratmu (${ArusKasViewModel.formatRupiah(emergencyBalance)})! Pastikan beneran mendesak ya."
                )
            } else {
                Triple(
                    GoldAccent,
                    "🟡 POS DARURAT TERPAKAI",
                    "Oke bro, $formatted bakal dipotong dari Simpanan Darurat Terpisah (sisa nanti ${ArusKasViewModel.formatRupiah(emergencyBalance - typedAmount)}). Semoga urusannya cepet beres ya!"
                )
            }
        }
        typedAmount > totalBalance -> Triple(
            CoralExpense,
            "🔴 SALDO NGGAK CUKUP BRO!",
            "Eh buset bestie, mau keluarin $formatted buat $selectedCategoryLabel padahal Saldo Siap Pakai cuma ${ArusKasViewModel.formatRupiah(totalBalance)}? Jangan ganggu jatah Tabungan/Darurat lho! 😭"
        )
        typedAmount >= 250_000L -> Triple(
            GoldAccent,
            "🟡 NOMINAL GEDE NIH!",
            "Seriusan mau keluarin $formatted sekaligus buat $selectedCategoryLabel bro? Coba tarik napas 3 detik, ini beneran kebutuhan atau cuma impulsif sesaat nih? 😂"
        )
        else -> Triple(
            EmeraldPrimary,
            "🟢 SIAP DICATAT BESTIE",
            "Pengeluaran $formatted untuk $selectedCategoryLabel masih terukur nih bro (sisa saldo siap pakai nanti ${ArusKasViewModel.formatRupiah((totalBalance - typedAmount).coerceAtLeast(0L))}). Gas catat biar arus kasmu transparan!"
        )
    }
}

