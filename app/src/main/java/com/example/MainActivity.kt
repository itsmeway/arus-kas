package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import com.example.data.AppLocalization
import com.example.data.AppSettings
import com.example.data.ActivityReminderNotificationHelper
import com.example.data.ArusKasLocalStore
import com.example.data.ArusKasRepository
import com.example.data.ModernPdfBackupGenerator
import com.example.data.SavingsGoal
import com.example.data.TransactionRecord
import com.example.data.Wallet
import com.example.ui.AppSettingsBottomSheet
import com.example.ui.ArusKasViewModel
import com.example.ui.BerandaExpenseDashboard
import com.example.ui.FinancialProfileOnboardingDialog
import com.example.ui.MyBcaLiquidGradientBackground
import com.example.ui.RoutineScheduleSettingsDialog
import com.example.ui.FirstTimeSetupDialog
import com.example.ui.GuruFinancialMentorScreen
import com.example.ui.ImpianSavingsScreen
import com.example.ui.UiState
import com.example.ui.WalletsBreakdownBottomSheet
import com.example.ui.UserGuideDialog
import com.example.ui.ArusKasProPaywallDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArusKasAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ArusKasAppNavigation() {
    MainArusKasScreen()
}

@Composable
fun MainArusKasScreen() {
    val viewModel: ArusKasViewModel = viewModel(
        key = "local_offline_dashboard",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val localStore = ArusKasLocalStore(app)
                ArusKasViewModel(ArusKasRepository(db), null, localStore)
            }
        }
    )

    val walletsUiState by viewModel.walletsState.collectAsStateWithLifecycle()
    val transactionsUiState by viewModel.transactionsState.collectAsStateWithLifecycle()
    val goalsUiState by viewModel.goalsState.collectAsStateWithLifecycle()
    val categoryLimits by viewModel.categoryLimits.collectAsStateWithLifecycle()
    val emergencyFund by viewModel.emergencyFund.collectAsStateWithLifecycle()
    val mandatoryBills by viewModel.mandatoryBills.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val guruState by viewModel.guruState.collectAsStateWithLifecycle()
    val sessionSeed by viewModel.sessionSeed.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val feedback by viewModel.actionFeedback.collectAsStateWithLifecycle()
    val postActivityReminder by viewModel.postActivityReminder.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    val wallets: List<Wallet> = (walletsUiState as? UiState.Success)?.data ?: emptyList()
    val transactions: List<TransactionRecord> = (transactionsUiState as? UiState.Success)?.data ?: emptyList()
    val goals: List<SavingsGoal> = (goalsUiState as? UiState.Success)?.data ?: emptyList()

    val context = LocalContext.current
    var showWalletsSheet by remember { mutableStateOf(false) }
    var showStarterDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showUserGuideDialog by remember { mutableStateOf(false) }
    var showFinancialProfileDialog by remember(userProfile.isCompleted) {
        mutableStateOf(!userProfile.isCompleted)
    }
    var showRoutineSettingsDialog by remember { mutableStateOf(false) }
    var showPaywallDialog by remember { mutableStateOf(false) }
    var hasTriggeredOpenAnalysis by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* permission updated */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!ActivityReminderNotificationHelper.hasNotificationPermission(context)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(postActivityReminder) {
        val event = postActivityReminder
        if (event != null) {
            ActivityReminderNotificationHelper.sendPostActivityNotification(
                context = context,
                activityTitle = event.title,
                amountFormatted = event.amountFormatted,
                categoryLabel = event.categoryLabel,
                note = event.title,
                remainingAllowance = event.remainingAllowance
            )
        }
    }

    // Initialize 30-day free trial on first app usage if not yet started
    LaunchedEffect(Unit) {
        if (appSettings.subscription.trialStartDateMillis <= 0L) {
            val initializedSub = appSettings.subscription.copy(
                trialStartDateMillis = System.currentTimeMillis()
            )
            viewModel.updateSettings(appSettings.copy(subscription = initializedSub))
        }
    }

    // Every time the app opens, generate a fresh, different Bestie financial analysis & reminder notification
    LaunchedEffect(wallets.isNotEmpty()) {
        if (!hasTriggeredOpenAnalysis && wallets.isNotEmpty()) {
            hasTriggeredOpenAnalysis = true
            viewModel.requestGuruFinancialEvaluation(wallets, transactions, goals)
        }
    }

    LaunchedEffect(feedback) {
        val msg = feedback
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    MyApplicationTheme(darkTheme = appSettings.isDarkMode) {
        MyBcaLiquidGradientBackground(isDarkMode = appSettings.isDarkMode) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
            BerandaExpenseDashboard(
                wallets = wallets,
                transactions = transactions,
                isSubmitting = isSubmitting,
                guruState = guruState,
                sessionSeed = sessionSeed,
                onRefreshBestieAnalysis = {
                    viewModel.requestGuruFinancialEvaluation(wallets, transactions, goals)
                },
                categoryLimits = categoryLimits,
                onUpdateCategoryLimit = { key, label, limitAmount, period ->
                    viewModel.updateCategoryLimit(key, label, limitAmount, period)
                },
                emergencyFund = emergencyFund,
                onUpdateEmergencyFund = { sourceWallet, addedAmount, newLimit, period, storageNote ->
                    viewModel.updateEmergencyFundAndAllocate(
                        sourceWallet = sourceWallet,
                        addedEmergencyAmount = addedAmount,
                        newEmergencyLimit = newLimit,
                        limitPeriod = period,
                        storagePlaceNote = storageNote
                    )
                },
                goals = goals,
                onAddGoal = { title, target, daily, note, iconKey ->
                    viewModel.addSavingsGoal(title, target, daily, note, iconKey)
                },
                onAllocateSavings = { goal, wallet, amount, note ->
                    viewModel.allocateSavingsToGoal(goal, wallet, amount, note)
                },
                onDeleteGoal = { goal ->
                    viewModel.deleteGoal(goal)
                },
                mandatoryBills = mandatoryBills,
                onUpdateMandatoryBillStatus = { bill, newStatus, wallet ->
                    viewModel.updateMandatoryBillStatus(bill, newStatus, wallet)
                },
                onUpsertMandatoryBill = { billId, name, totalAmount, dueScheduleLabel, dueDay, renewalDay, billType ->
                    viewModel.upsertMandatoryBill(
                        billId = billId,
                        name = name,
                        totalAmount = totalAmount,
                        dueScheduleLabel = dueScheduleLabel,
                        dueDayOfMonth = dueDay,
                        renewalDayOfMonth = renewalDay,
                        billType = billType
                    )
                },
                onDeleteMandatoryBill = { billId ->
                    viewModel.deleteMandatoryBill(billId)
                },
                userProfile = userProfile,
                onOpenFinancialProfileWizard = { showFinancialProfileDialog = true },
                onOpenWalletsSheet = { showWalletsSheet = true },
                onOpenStarterSetup = { showStarterDialog = true },
                onOpenUserGuide = { showUserGuideDialog = true },
                onOpenPaywall = { showPaywallDialog = true },
                appSettings = appSettings,
                onOpenSettings = { showSettingsSheet = true },
                postActivityReminder = postActivityReminder,
                onDismissPostActivityReminder = { viewModel.dismissPostActivityReminder() },
                onOpenRoutineSettings = { showRoutineSettingsDialog = true },
                onRecordTransaction = { wallet, type, amount, catKey, catLabel, freqTag, note ->
                    viewModel.recordTransaction(
                        wallet = wallet,
                        type = type,
                        amount = amount,
                        categoryKey = catKey,
                        categoryLabel = catLabel,
                        frequencyTag = freqTag,
                        note = note
                    )
                },
                onDeleteTransaction = { tx ->
                    viewModel.deleteTransaction(tx, wallets)
                }
            )
                }
            }
        }

    if (showFinancialProfileDialog) {
        FinancialProfileOnboardingDialog(
            initialProfile = userProfile,
            currentWallets = wallets,
            onDismiss = {
                viewModel.dismissOrSkipOnboarding()
                showFinancialProfileDialog = false
            },
            onOpenUserGuide = {
                showUserGuideDialog = true
            },
            onCompleteSetup = { profile, configuredWallets, configuredBills, customLimits ->
                viewModel.applyFinancialProfileSetup(
                    profile = profile,
                    configuredWallets = configuredWallets,
                    configuredBills = configuredBills,
                    customCategoryLimits = customLimits
                )
                showFinancialProfileDialog = false
            }
        )
    }

    if (showRoutineSettingsDialog) {
        RoutineScheduleSettingsDialog(
            initialProfile = userProfile,
            onDismiss = { showRoutineSettingsDialog = false },
            onSaveProfile = { updatedProfile ->
                viewModel.updateUserProfile(updatedProfile)
                showRoutineSettingsDialog = false
            }
        )
    }

    if (showWalletsSheet) {
        WalletsBreakdownBottomSheet(
            wallets = wallets,
            totalTabunganSaved = goals.sumOf { it.savedAmount },
            emergencyBalance = emergencyFund.emergencyBalance,
            onDismiss = { showWalletsSheet = false },
            onSaveWallet = { id, name, type, balance, colorHex ->
                viewModel.addOrUpdateWallet(
                    walletId = id,
                    name = name,
                    walletType = type,
                    balance = balance,
                    colorHex = colorHex,
                    iconKey = when (type) {
                        "BANK" -> "bank_bca"
                        "CASH" -> "cash_wallet"
                        else -> "ewallet_dana"
                    }
                )
            },
            onDeleteWallet = { w -> viewModel.deleteWallet(w) },
            onTransferWallet = { fromWallet, toWallet, amount, adminFee, note ->
                viewModel.transferBetweenWallets(
                    fromWallet = fromWallet,
                    toWallet = toWallet,
                    amount = amount,
                    adminFee = adminFee,
                    note = note
                )
            }
        )
    }

    if (showStarterDialog) {
        FirstTimeSetupDialog(
            onDismiss = { showStarterDialog = false },
            onConfirmSetup = { walletBalances, goalTitle, goalTarget, goalDaily, goalNote ->
                viewModel.seedStarterSetup(
                    walletBalances = walletBalances,
                    goalTitle = goalTitle,
                    goalTarget = goalTarget,
                    goalDaily = goalDaily,
                    goalNote = goalNote
                )
                showStarterDialog = false
            }
        )
    }

    if (showSettingsSheet) {
        val currentContext = LocalContext.current
        AppSettingsBottomSheet(
            settings = appSettings,
            onUpdateSettings = { newSettings ->
                viewModel.updateSettings(newSettings)
            },
            onExportPdf = {
                val result = ModernPdfBackupGenerator.generateModernLedgerPdf(
                    context = currentContext,
                    wallets = wallets,
                    transactions = transactions,
                    goals = goals,
                    emergencyFund = emergencyFund,
                    lang = appSettings.language
                )
                if (result.isSuccess) {
                    val msg = String.format(
                        AppLocalization.get("pdf_saved_msg", appSettings.language),
                        result.fileName
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                    val pdfFile = java.io.File(result.filePath)
                    if (pdfFile.exists()) {
                        ModernPdfBackupGenerator.openOrSharePdf(currentContext, pdfFile)
                    }
                } else {
                    val failMsg = String.format(
                        AppLocalization.get("pdf_failed_msg", appSettings.language),
                        result.errorMessage ?: ""
                    )
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(failMsg)
                    }
                }
            },
            onOpenUserGuide = {
                showSettingsSheet = false
                showUserGuideDialog = true
            },
            onOpenFinancialProfileWizard = {
                showSettingsSheet = false
                showFinancialProfileDialog = true
            },
            onOpenRoutineSettings = {
                showSettingsSheet = false
                showRoutineSettingsDialog = true
            },
            onOpenWalletsSheet = {
                showSettingsSheet = false
                showWalletsSheet = true
            },
            onOpenPaywall = {
                showSettingsSheet = false
                showPaywallDialog = true
            },
            onDismiss = { showSettingsSheet = false },
            onShowMessage = { msg ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        )
    }

    if (showUserGuideDialog) {
        UserGuideDialog(
            language = appSettings.language,
            onDismiss = {
                viewModel.updateSettings(appSettings.copy(hasSeenUserGuide = true))
                showUserGuideDialog = false
            },
            onCompleteGuide = {
                viewModel.updateSettings(appSettings.copy(hasSeenUserGuide = true))
            },
            onOpenStarterSetup = {
                showStarterDialog = true
            }
        )
    }

    val isTrialExpiredAndUnpaid = !appSettings.subscription.isProActive && !appSettings.subscription.isTrialActive
    if (showPaywallDialog || isTrialExpiredAndUnpaid) {
        ArusKasProPaywallDialog(
            subscription = appSettings.subscription,
            language = appSettings.language,
            onDismiss = {
                if (!isTrialExpiredAndUnpaid) {
                    showPaywallDialog = false
                }
            },
            onActivateFreeTrial = {
                val updatedSub = appSettings.subscription.copy(
                    trialStartDateMillis = System.currentTimeMillis()
                )
                viewModel.updateSettings(appSettings.copy(subscription = updatedSub))
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        AppLocalization.get("msg_trial_activated", appSettings.language)
                    )
                }
            },
            onSubscribePro = {
                val updatedSub = appSettings.subscription.copy(
                    isProActive = true,
                    proActivatedDateMillis = System.currentTimeMillis()
                )
                viewModel.updateSettings(appSettings.copy(subscription = updatedSub))
                showPaywallDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        "Selamat! Langganan ArusKas PRO (Rp 7.000/bln) aktif!"
                    )
                }
            },
            onRestorePurchase = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        AppLocalization.get("msg_subscription_restored", appSettings.language)
                    )
                }
            }
        )
    }
    }
}
