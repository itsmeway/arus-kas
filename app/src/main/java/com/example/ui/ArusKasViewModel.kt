package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.data.ArusKasLocalStore
import com.example.data.ArusKasRepository
import com.example.data.CategoryLimitConfig
import com.example.data.EmergencyFundConfig
import com.example.data.MandatoryBillItem
import com.example.data.PresetCategories
import com.example.data.SavingsGoal
import com.example.data.TransactionRecord
import com.example.data.UserFinancialProfile
import com.example.data.Wallet
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface GuruAdviceState {
    data object Idle : GuruAdviceState
    data object Loading : GuruAdviceState
    data class Success(
        val statusBadge: String,
        val teguran: String,
        val motivasi: String,
        val langkahKonkret: String,
        val colorLevel: String = "GREEN", // GREEN, YELLOW, RED, BLUE
        val saldoCritique: String = "",
        val nominalBoxAnalysis: String = "",
        val categoryCritique: String = ""
    ) : GuruAdviceState
    data class Error(val message: String) : GuruAdviceState
}

class ArusKasViewModel(
    private val repository: ArusKasRepository? = null,
    private val currentUserId: String? = null,
    private val localStore: ArusKasLocalStore? = null
) : ViewModel() {

    private val _sessionSeed = MutableStateFlow(((System.currentTimeMillis() / 1000L) % 9973L).toInt())
    val sessionSeed: StateFlow<Int> = _sessionSeed.asStateFlow()

    private val guruSchema = Schema.obj(
        properties = mapOf(
            "statusBadge" to Schema.string(description = "Label notifikasi pengingat singkat 2-4 kata huruf kapital"),
            "colorLevel" to Schema.string(description = "Pilih satu: GREEN, YELLOW, RED, atau BLUE"),
            "saldoCritique" to Schema.string(description = "1 kalimat singkat kondisi saldo"),
            "nominalBoxAnalysis" to Schema.string(description = "Analisis singkat keuangan"),
            "categoryCritique" to Schema.string(description = "1 kalimat kritik kategori"),
            "teguran" to Schema.string(description = "Teguran jujur santai"),
            "motivasi" to Schema.string(description = "Kalimat penyemangat"),
            "langkahKonkret" to Schema.string(description = "1 saran angka praktis hari ini")
        )
    )

    private val guruModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = guruSchema
            }
        )
    }

    private val defaultInitialGoals = listOf(
        SavingsGoal(
            id = "goal_laptop_utama",
            userId = "local_preview",
            title = "Beli Laptop Impian",
            targetAmount = 10_000_000L,
            savedAmount = 1_250_000L,
            dailyRecommendation = 40_000L,
            note = "Sisihkan rutin dari penghasilan",
            iconKey = "laptop"
        )
    )

    private class SyncStateHolder<T>(
        initialValue: T,
        private val onUpdated: (T) -> Unit
    ) {
        val flow = MutableStateFlow(initialValue)
        var value: T
            get() = flow.value
            set(newVal) {
                flow.value = newVal
                onUpdated(newVal)
            }
    }

    private val _localWalletsUiState = MutableStateFlow<UiState<List<Wallet>>>(
        UiState.Success(localStore?.loadWallets() ?: PresetCategories.defaultStarterWallets)
    )
    private val _localTransactionsUiState = MutableStateFlow<UiState<List<TransactionRecord>>>(
        UiState.Success(localStore?.loadTransactions() ?: emptyList())
    )
    private val _localGoalsUiState = MutableStateFlow<UiState<List<SavingsGoal>>>(
        UiState.Success(localStore?.loadGoals(defaultInitialGoals) ?: defaultInitialGoals)
    )

    private val _localWallets = SyncStateHolder(
        (_localWalletsUiState.value as UiState.Success).data
    ) { _localWalletsUiState.value = UiState.Success(it) }

    private val _localTransactions = SyncStateHolder(
        (_localTransactionsUiState.value as UiState.Success).data
    ) { _localTransactionsUiState.value = UiState.Success(it) }

    private val _localGoals = SyncStateHolder(
        (_localGoalsUiState.value as UiState.Success).data
    ) { _localGoalsUiState.value = UiState.Success(it) }

    private val _mandatoryBills = MutableStateFlow(
        localStore?.loadMandatoryBills() ?: PresetCategories.defaultMandatoryBills
    )
    val mandatoryBills: StateFlow<List<MandatoryBillItem>> = _mandatoryBills.asStateFlow()

    private val _userProfile = MutableStateFlow(
        localStore?.loadUserProfile() ?: UserFinancialProfile()
    )
    val userProfile: StateFlow<UserFinancialProfile> = _userProfile.asStateFlow()

    data class PostActivityReminderEvent(
        val title: String,
        val amountFormatted: String,
        val categoryLabel: String,
        val remainingAllowance: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _postActivityReminder = MutableStateFlow<PostActivityReminderEvent?>(null)
    val postActivityReminder: StateFlow<PostActivityReminderEvent?> = _postActivityReminder.asStateFlow()

    fun dismissPostActivityReminder() {
        _postActivityReminder.value = null
    }

    fun updateUserProfile(profile: UserFinancialProfile) {
        _userProfile.value = profile
        localStore?.saveUserProfile(profile)
        _actionFeedback.value = "Jadwal harian & pengingat anti-bentrok berhasil disimpan!"
    }

    /**
     * Dipanggil ketika pengguna menyelesaikan ATAU melewati (Skip) Kuesioner Awal / Panduan
     * agar kuesioner dan panduan HANYA muncul 1x seumur hidup saat pertama kali buka aplikasi,
     * dan tidak pernah muncul otomatis lagi saat aplikasi ditutup-buka berulang kali.
     */
    fun dismissOrSkipOnboarding() {
        val updatedProfile = _userProfile.value.copy(isCompleted = true)
        val updatedSettings = _appSettings.value.copy(hasSeenUserGuide = true)
        _userProfile.value = updatedProfile
        _appSettings.value = updatedSettings
        localStore?.markInitialOnboardingSeenOnce()
        localStore?.saveUserProfile(updatedProfile)
        localStore?.saveAppSettings(updatedSettings)
    }

    val walletsState: StateFlow<UiState<List<Wallet>>> =
        if (currentUserId != null && repository != null) {
            repository.observeWallets(currentUserId)
                .map<List<Wallet>, UiState<List<Wallet>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat daftar dompet"))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = UiState.Loading
                )
        } else {
            _localWalletsUiState.asStateFlow()
        }

    val transactionsState: StateFlow<UiState<List<TransactionRecord>>> =
        if (currentUserId != null && repository != null) {
            repository.observeTransactions(currentUserId)
                .map<List<TransactionRecord>, UiState<List<TransactionRecord>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat riwayat transaksi"))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = UiState.Loading
                )
        } else {
            _localTransactionsUiState.asStateFlow()
        }

    val goalsState: StateFlow<UiState<List<SavingsGoal>>> =
        if (currentUserId != null && repository != null) {
            repository.observeGoals(currentUserId)
                .map<List<SavingsGoal>, UiState<List<SavingsGoal>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat target impian"))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = UiState.Loading
                )
        } else {
            _localGoalsUiState.asStateFlow()
        }

    private val _categoryLimits = MutableStateFlow(
        localStore?.loadCategoryLimits() ?: PresetCategories.defaultCategoryLimits
    )
    val categoryLimits: StateFlow<Map<String, CategoryLimitConfig>> = _categoryLimits.asStateFlow()

    private val _emergencyFund = MutableStateFlow(
        localStore?.loadEmergencyFund() ?: EmergencyFundConfig()
    )
    val emergencyFund: StateFlow<EmergencyFundConfig> = _emergencyFund.asStateFlow()

    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _guruState = MutableStateFlow<GuruAdviceState>(GuruAdviceState.Idle)
    val guruState: StateFlow<GuruAdviceState> = _guruState.asStateFlow()

    private val _appSettings = MutableStateFlow(
        localStore?.loadAppSettings() ?: AppSettings()
    )
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private fun persistAllLocalData() {
        val store = localStore ?: return
        store.saveUserProfile(_userProfile.value)
        store.saveAppSettings(_appSettings.value)
        store.saveWallets(_localWallets.value)
        store.saveTransactions(_localTransactions.value)
        store.saveGoals(_localGoals.value)
        store.saveMandatoryBills(_mandatoryBills.value)
        store.saveCategoryLimits(_categoryLimits.value)
        store.saveEmergencyFund(_emergencyFund.value)
    }

    fun updateLanguage(lang: AppLanguage) {
        val updated = _appSettings.value.copy(language = lang)
        _appSettings.value = updated
        localStore?.saveAppSettings(updated)
        _actionFeedback.value = when (lang) {
            AppLanguage.ENGLISH -> "Language changed to English!"
            AppLanguage.INDONESIAN -> "Bahasa berhasil diubah ke Bahasa Indonesia!"
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _appSettings.value = newSettings
        localStore?.saveAppSettings(newSettings)
        _actionFeedback.value = "Pengaturan berhasil disimpan!"
    }

    fun clearFeedback() {
        _actionFeedback.value = null
    }

    /**
     * Mengubah status pembayaran Tagihan Wajib (UNPAID -> HALF_PAID -> PAID)
     * dan otomatis menyesuaikan saldo dompet serta pencatatan transaksi secara presisi
     * (termasuk menghapus/menyesuaikan catatan transaksi jika status dikembalikan ke Belum Lunas).
     */
    fun updateMandatoryBillStatus(
        bill: MandatoryBillItem,
        newStatus: String,
        targetWallet: Wallet?,
        customPaidAmount: Long? = null
    ) {
        val targetPaid = when (newStatus) {
            "PAID" -> bill.totalAmount
            "HALF_PAID" -> customPaidAmount?.coerceIn(1L, bill.totalAmount) ?: (bill.totalAmount / 2L)
            else -> 0L
        }
        val deltaToPay = targetPaid - bill.paidAmount

        _mandatoryBills.value = _mandatoryBills.value.map { item ->
            if (item.id == bill.id) {
                item.copy(
                    paidAmount = targetPaid,
                    status = newStatus
                )
            } else item
        }

        // Sinkronisasi limit kategori tagihan_bulanan dengan total seluruh daftar tagihan wajib
        syncMandatoryBillsLimit()

        val billTxPrefix = "tx_bill_${bill.id}_"

        if (deltaToPay != 0L && targetWallet != null) {
            _localWallets.value = _localWallets.value.map { w ->
                if (w.id == targetWallet.id) w.copy(balance = w.balance - deltaToPay) else w
            }
            if (deltaToPay > 0L) {
                val statusLabel = if (newStatus == "PAID") "Lunas ✅" else "Cicil Setengah (50%)"
                val newTx = TransactionRecord(
                    id = "${billTxPrefix}${System.currentTimeMillis()}",
                    userId = currentUserId ?: "local_preview",
                    type = "EXPENSE",
                    amount = deltaToPay,
                    categoryKey = "tagihan_bulanan",
                    categoryLabel = "Tagihan Wajib",
                    frequencyTag = "WAJIB",
                    walletId = targetWallet.id,
                    walletName = targetWallet.name,
                    note = "${bill.name} ($statusLabel • ${bill.dueScheduleLabel})",
                    transactionDateMillis = System.currentTimeMillis()
                )
                _localTransactions.value = listOf(newTx) + _localTransactions.value
                _actionFeedback.value = "${bill.name}: $statusLabel (-${formatRupiah(deltaToPay)} dari ${targetWallet.name})"
            } else {
                val refundAmt = -deltaToPay
                // Bersihkan atau sesuaikan catatan transaksi tagihan agar pengeluaran harian/bulanan tidak membengkak palsu
                val withoutThisBillTxs = _localTransactions.value.filterNot { tx ->
                    tx.id.startsWith(billTxPrefix) ||
                        (tx.id.startsWith("tx_bill_") && tx.note.startsWith(bill.name))
                }
                if (targetPaid > 0L) {
                    val statusLabel = "Cicil Setengah (50%)"
                    val adjustedTx = TransactionRecord(
                        id = "${billTxPrefix}${System.currentTimeMillis()}",
                        userId = currentUserId ?: "local_preview",
                        type = "EXPENSE",
                        amount = targetPaid,
                        categoryKey = "tagihan_bulanan",
                        categoryLabel = "Tagihan Wajib",
                        frequencyTag = "WAJIB",
                        walletId = targetWallet.id,
                        walletName = targetWallet.name,
                        note = "${bill.name} ($statusLabel • ${bill.dueScheduleLabel})",
                        transactionDateMillis = System.currentTimeMillis()
                    )
                    _localTransactions.value = listOf(adjustedTx) + withoutThisBillTxs
                } else {
                    _localTransactions.value = withoutThisBillTxs
                }
                _actionFeedback.value = "Status ${bill.name} diubah ke ${if (newStatus == "UNPAID") "Belum Lunas" else "Setengah"} (+${formatRupiah(refundAmt)} kembali ke ${targetWallet.name})"
            }
        } else {
            if (targetPaid == 0L) {
                _localTransactions.value = _localTransactions.value.filterNot { tx ->
                    tx.id.startsWith(billTxPrefix) ||
                        (tx.id.startsWith("tx_bill_") && tx.note.startsWith(bill.name))
                }
            }
            val label = when (newStatus) {
                "PAID" -> "Sudah Lunas ✅"
                "HALF_PAID" -> "Baru Setengah (${formatRupiah(targetPaid)})"
                else -> "Belum Lunas"
            }
            _actionFeedback.value = "Tagihan '${bill.name}' ditandai: $label"
        }
        persistAllLocalData()
    }

    fun upsertMandatoryBill(
        billId: String?,
        name: String,
        totalAmount: Long,
        dueScheduleLabel: String,
        dueDayOfMonth: Int,
        renewalDayOfMonth: Int?,
        billType: String
    ) {
        val cleanTotal = totalAmount.coerceAtLeast(1_000L)
        val cleanName = name.trim().ifBlank { "Tagihan Bulanan" }
        val cleanSchedule = dueScheduleLabel.trim().ifBlank { "Bayar setiap tanggal $dueDayOfMonth" }

        if (billId.isNullOrBlank()) {
            val newBill = MandatoryBillItem(
                id = "bill_${System.currentTimeMillis()}",
                name = cleanName,
                totalAmount = cleanTotal,
                paidAmount = 0L,
                dueScheduleLabel = cleanSchedule,
                dueDayOfMonth = dueDayOfMonth.coerceIn(1, 31),
                renewalDayOfMonth = renewalDayOfMonth?.coerceIn(1, 31),
                status = "UNPAID",
                billType = billType
            )
            _mandatoryBills.value = _mandatoryBills.value + newBill
            _actionFeedback.value = "Tagihan '$cleanName' (${formatRupiah(cleanTotal)}) ditambahkan ke daftar!"
        } else {
            _mandatoryBills.value = _mandatoryBills.value.map { existing ->
                if (existing.id == billId) {
                    val adjustedPaid = when (existing.status) {
                        "PAID" -> cleanTotal
                        "HALF_PAID" -> cleanTotal / 2L
                        else -> 0L
                    }
                    existing.copy(
                        name = cleanName,
                        totalAmount = cleanTotal,
                        paidAmount = adjustedPaid,
                        dueScheduleLabel = cleanSchedule,
                        dueDayOfMonth = dueDayOfMonth.coerceIn(1, 31),
                        renewalDayOfMonth = renewalDayOfMonth?.coerceIn(1, 31),
                        billType = billType
                    )
                } else existing
            }
            _actionFeedback.value = "Tagihan '$cleanName' berhasil diperbarui!"
        }
        syncMandatoryBillsLimit()
        persistAllLocalData()
    }

    fun deleteMandatoryBill(billId: String) {
        val target = _mandatoryBills.value.find { it.id == billId }
        _mandatoryBills.value = _mandatoryBills.value.filterNot { it.id == billId }
        syncMandatoryBillsLimit()
        persistAllLocalData()
        if (target != null) {
            _actionFeedback.value = "Tagihan '${target.name}' dihapus dari daftar"
        }
    }

    private fun syncMandatoryBillsLimit() {
        val totalBills = _mandatoryBills.value.sumOf { it.totalAmount }
        if (totalBills > 0L) {
            val currentMap = _categoryLimits.value.toMutableMap()
            val existingCfg = currentMap["tagihan_bulanan"] ?: CategoryLimitConfig("tagihan_bulanan", totalBills, "BULANAN")
            currentMap["tagihan_bulanan"] = existingCfg.copy(limitAmount = totalBills)
            _categoryLimits.value = currentMap
        }
    }

    /**
     * Menerapkan seluruh jawaban kuesioner profil keuangan awal sebelum masuk dashboard,
     * termasuk daftar Bank & E-Wallet yang ditambah/dikurangi pengguna beserta saldonya,
     * daftar tagihan wajib, dan limit fleksibel.
     */
    fun applyFinancialProfileSetup(
        profile: UserFinancialProfile,
        configuredWallets: List<Wallet>,
        configuredBills: List<MandatoryBillItem>,
        customCategoryLimits: Map<String, CategoryLimitConfig>
    ) {
        val completedProfile = profile.copy(isCompleted = true)
        val updatedSettings = _appSettings.value.copy(hasSeenUserGuide = true)
        _userProfile.value = completedProfile
        _appSettings.value = updatedSettings
        localStore?.markInitialOnboardingSeenOnce()

        if (configuredWallets.isNotEmpty()) {
            _localWallets.value = configuredWallets
        }

        if (configuredBills.isNotEmpty()) {
            _mandatoryBills.value = configuredBills
        }

        val mergedLimits = _categoryLimits.value.toMutableMap()
        customCategoryLimits.forEach { (k, v) -> mergedLimits[k] = v }
        val totalBills = _mandatoryBills.value.sumOf { it.totalAmount }
        if (totalBills > 0L) {
            mergedLimits["tagihan_bulanan"] = CategoryLimitConfig(
                categoryKey = "tagihan_bulanan",
                limitAmount = totalBills,
                period = "BULANAN"
            )
        }
        _categoryLimits.value = mergedLimits
        persistAllLocalData()

        _actionFeedback.value = "Profil Keuangan siap! ${configuredWallets.size} Dompet/Bank, Tagihan Wajib & Limit Fleksibel sudah disesuaikan."
    }

    fun applyFinancialProfileSetup(
        profile: UserFinancialProfile,
        initialWalletBalances: Map<String, Long>,
        configuredBills: List<MandatoryBillItem>,
        customCategoryLimits: Map<String, CategoryLimitConfig>
    ) {
        val baseList = if (_localWallets.value.isNotEmpty()) _localWallets.value else PresetCategories.defaultStarterWallets
        val updatedWallets = baseList.map { w ->
            w.copy(balance = initialWalletBalances[w.id] ?: w.balance)
        }
        applyFinancialProfileSetup(
            profile = profile,
            configuredWallets = updatedWallets,
            configuredBills = configuredBills,
            customCategoryLimits = customCategoryLimits
        )
    }

    fun updateCategoryLimit(
        categoryKey: String,
        categoryLabel: String,
        limitAmount: Long,
        period: String
    ) {
        val cleanLimit = limitAmount.coerceAtLeast(0L)
        _categoryLimits.value = _categoryLimits.value.toMutableMap().apply {
            put(
                categoryKey,
                CategoryLimitConfig(
                    categoryKey = categoryKey,
                    limitAmount = cleanLimit,
                    period = period
                )
            )
        }
        localStore?.saveCategoryLimits(_categoryLimits.value)
        val periodLabel = periodDisplayLabel(period)
        _actionFeedback.value = if (cleanLimit > 0L) {
            "Limit $categoryLabel diatur ke ${formatRupiah(cleanLimit)} ($periodLabel)"
        } else {
            "Limit target untuk $categoryLabel diatur ke Mode Bebas"
        }
    }

    fun updateEmergencyFundAndAllocate(
        sourceWallet: Wallet?,
        addedEmergencyAmount: Long,
        newEmergencyLimit: Long,
        limitPeriod: String,
        storagePlaceNote: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val cleanAdd = addedEmergencyAmount.coerceAtLeast(0L)
                val cleanLimit = newEmergencyLimit.coerceAtLeast(0L)
                val currentCfg = _emergencyFund.value

                if (cleanAdd > 0L && sourceWallet != null) {
                    if (currentUserId != null && repository != null) {
                        val newWalletBalance = sourceWallet.balance - cleanAdd
                        repository.updateWalletBalance(sourceWallet, newWalletBalance)
                    } else {
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == sourceWallet.id) w.copy(balance = w.balance - cleanAdd) else w
                        }
                    }
                }

                _emergencyFund.value = currentCfg.copy(
                    emergencyBalance = currentCfg.emergencyBalance + cleanAdd,
                    emergencyLimit = cleanLimit,
                    limitPeriod = limitPeriod,
                    storagePlaceNote = storagePlaceNote.ifBlank {
                        "Rekening / Kantong Terpisah (Jangan dicampur uang harian)"
                    }
                )
                persistAllLocalData()

                _actionFeedback.value = if (cleanAdd > 0L && sourceWallet != null) {
                    "${formatRupiah(cleanAdd)} dipotong dari ${sourceWallet.name} ke Saldo Darurat Terpisah! (Limit: ${formatRupiah(cleanLimit)})"
                } else {
                    "Limit Pengeluaran Darurat diperbarui ke ${formatRupiah(cleanLimit)} (${periodDisplayLabel(limitPeriod)})"
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal memperbarui dana darurat: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun seedStarterSetup(
        walletBalances: Map<String, Long>,
        goalTitle: String,
        goalTarget: Long,
        goalDaily: Long,
        goalNote: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null && repository != null) {
                    repository.seedStarterSetup(
                        walletBalances = walletBalances,
                        initialGoalTitle = goalTitle,
                        initialGoalTarget = goalTarget,
                        initialGoalDaily = goalDaily,
                        initialGoalNote = goalNote
                    )
                } else {
                    val baseWallets = if (_localWallets.value.isNotEmpty()) _localWallets.value else PresetCategories.defaultStarterWallets
                    _localWallets.value = baseWallets.map { preset ->
                        preset.copy(balance = walletBalances[preset.id] ?: preset.balance)
                    }
                    if (goalTitle.isNotBlank() && goalTarget > 0L) {
                        _localGoals.value = listOf(
                            SavingsGoal(
                                id = "goal_laptop_utama",
                                userId = "local_preview",
                                title = goalTitle.trim(),
                                targetAmount = goalTarget,
                                savedAmount = 0L,
                                dailyRecommendation = goalDaily.coerceAtLeast(10_000L),
                                note = goalNote.trim(),
                                iconKey = "laptop"
                            )
                        )
                    }
                    persistAllLocalData()
                }
                _actionFeedback.value = "Dompet & Target Impian siap dipantau!"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyimpan pengaturan awal: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun recordTransaction(
        wallet: Wallet,
        type: String,
        amount: Long,
        categoryKey: String,
        categoryLabel: String,
        frequencyTag: String,
        note: String,
        onComplete: () -> Unit = {}
    ) {
        if (amount <= 0L) {
            _actionFeedback.value = "Masukkan nominal lebih dari Rp 0"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val isEmergencySpend = categoryKey == "pakai_darurat"
                if (isEmergencySpend) {
                    val currentEm = _emergencyFund.value
                    _emergencyFund.value = currentEm.copy(
                        emergencyBalance = (currentEm.emergencyBalance - amount).coerceAtLeast(0L)
                    )
                    val newTx = TransactionRecord(
                        id = "tx_${System.currentTimeMillis()}",
                        userId = currentUserId ?: "local_preview",
                        type = "EXPENSE",
                        amount = amount,
                        categoryKey = categoryKey,
                        categoryLabel = categoryLabel,
                        frequencyTag = "DARURAT",
                        walletId = "pos_darurat_terpisah",
                        walletName = "Saldo Darurat Terpisah",
                        note = note.trim(),
                        transactionDateMillis = System.currentTimeMillis()
                    )
                    _localTransactions.value = listOf(newTx) + _localTransactions.value
                    val formatted = formatRupiah(amount)
                    _actionFeedback.value = "-$formatted dipotong dari Saldo Darurat Terpisah"
                } else {
                    if (currentUserId != null && repository != null) {
                        repository.recordTransactionAndAdjustWallet(
                            wallet = wallet,
                            type = type,
                            amount = amount,
                            categoryKey = categoryKey,
                            categoryLabel = categoryLabel,
                            frequencyTag = frequencyTag,
                            note = note
                        )
                    } else {
                        val delta = if (type == "INCOME") amount else -amount
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == wallet.id) w.copy(balance = w.balance + delta) else w
                        }
                        val newTx = TransactionRecord(
                            id = "tx_${System.currentTimeMillis()}",
                            userId = "local_preview",
                            type = type,
                            amount = amount,
                            categoryKey = categoryKey,
                            categoryLabel = categoryLabel,
                            frequencyTag = frequencyTag,
                            walletId = wallet.id,
                            walletName = wallet.name,
                            note = note.trim(),
                            transactionDateMillis = System.currentTimeMillis()
                        )
                        _localTransactions.value = listOf(newTx) + _localTransactions.value
                    }
                    val formatted = formatRupiah(amount)
                    _actionFeedback.value = if (type == "INCOME") {
                        "+$formatted masuk ke ${wallet.name} ($categoryLabel)"
                    } else {
                        "-$formatted terpotong dari ${wallet.name} untuk $categoryLabel"
                    }
                }
                persistAllLocalData()

                if (_userProfile.value.isPostActivityReminderEnabled) {
                    val activeLimitCfg = _categoryLimits.value[categoryKey]
                    val limitAmt = activeLimitCfg?.limitAmount ?: 0L
                    val spentNow = calculateSpentForCategoryLimit(
                        categoryKey = categoryKey,
                        period = activeLimitCfg?.period ?: "BULANAN",
                        transactions = _localTransactions.value,
                        mandatoryBills = _mandatoryBills.value
                    )
                    val remainingPool = (limitAmt - spentNow).coerceAtLeast(0L)
                    val remLabel = if (limitAmt > 0L) formatRupiah(remainingPool) else "Bebas"
                    _postActivityReminder.value = PostActivityReminderEvent(
                        title = note.ifBlank { categoryLabel },
                        amountFormatted = formatRupiah(amount),
                        categoryLabel = categoryLabel,
                        remainingAllowance = remLabel
                    )
                }
                onComplete()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal mencatat transaksi: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    /**
     * Fitur Real-Life: Pindah Saldo / Top-Up E-Wallet / Tarik Tunai Antar Dompet.
     * Memindahkan saldo dari Bank (mis. BCA) ke E-Wallet (mis. DANA/GoPay/ShopeePay) atau Uang Tunai
     * tanpa membuat pengeluaran harian/bulanan membengkak palsu (kecuali jika ada biaya admin transfer).
     */
    fun transferBetweenWallets(
        fromWallet: Wallet,
        toWallet: Wallet,
        amount: Long,
        adminFee: Long = 0L,
        note: String = ""
    ) {
        if (amount <= 0L) {
            _actionFeedback.value = "Nominal pindah saldo harus lebih dari Rp 0"
            return
        }
        if (fromWallet.id == toWallet.id) {
            _actionFeedback.value = "Pilih dompet tujuan yang berbeda dari dompet asal"
            return
        }
        val cleanFee = adminFee.coerceAtLeast(0L)
        val totalDeduct = amount + cleanFee

        _localWallets.value = _localWallets.value.map { w ->
            when (w.id) {
                fromWallet.id -> w.copy(balance = w.balance - totalDeduct)
                toWallet.id -> w.copy(balance = w.balance + amount)
                else -> w
            }
        }

        val now = System.currentTimeMillis()
        val transferTx = TransactionRecord(
            id = "tx_transfer_${fromWallet.id}_to_${toWallet.id}_$now",
            userId = currentUserId ?: "local_preview",
            type = "TRANSFER",
            amount = amount,
            categoryKey = "mutasi_dompet",
            categoryLabel = "Pindah Saldo: ${fromWallet.name} → ${toWallet.name}",
            frequencyTag = "MUTASI",
            walletId = fromWallet.id,
            walletName = "${fromWallet.name} → ${toWallet.name}",
            note = note.trim().ifBlank { "Top-Up / Pindah saldo ke ${toWallet.name}" },
            transactionDateMillis = now
        )

        val newTxList = mutableListOf(transferTx)
        if (cleanFee > 0L) {
            val feeTx = TransactionRecord(
                id = "tx_fee_$now",
                userId = currentUserId ?: "local_preview",
                type = "EXPENSE",
                amount = cleanFee,
                categoryKey = "kebutuhan_pokok",
                categoryLabel = "Biaya Admin Transfer",
                frequencyTag = "HARIAN",
                walletId = fromWallet.id,
                walletName = fromWallet.name,
                note = "Biaya admin top-up/transfer ke ${toWallet.name}",
                transactionDateMillis = now + 1L
            )
            newTxList.add(0, feeTx)
        }

        _localTransactions.value = newTxList + _localTransactions.value
        persistAllLocalData()

        val feeSuffix = if (cleanFee > 0L) " (+Biaya Admin ${formatRupiah(cleanFee)})" else " (Bebas Biaya Admin)"
        _actionFeedback.value = "Berhasil pindah ${formatRupiah(amount)} dari ${fromWallet.name} ke ${toWallet.name}$feeSuffix"
    }

    fun addOrUpdateWallet(
        walletId: String?,
        name: String,
        walletType: String,
        balance: Long,
        colorHex: String,
        iconKey: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null && repository != null) {
                    repository.upsertWallet(
                        walletId = walletId,
                        name = name,
                        walletType = walletType,
                        balance = balance,
                        colorHex = colorHex,
                        iconKey = iconKey
                    )
                } else {
                    if (walletId.isNullOrBlank()) {
                        val newW = Wallet(
                            id = "wallet_${System.currentTimeMillis()}",
                            userId = "local_preview",
                            name = name.trim(),
                            walletType = walletType,
                            balance = balance,
                            colorHex = colorHex,
                            iconKey = iconKey
                        )
                        _localWallets.value = _localWallets.value + newW
                    } else {
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == walletId) {
                                w.copy(
                                    name = name.trim(),
                                    walletType = walletType,
                                    balance = balance,
                                    colorHex = colorHex,
                                    iconKey = iconKey
                                )
                            } else w
                        }
                    }
                    persistAllLocalData()
                }
                _actionFeedback.value = "Dompet $name berhasil disimpan (${formatRupiah(balance)})"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyimpan dompet: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun adjustWalletBalanceDirectly(wallet: Wallet, newBalance: Long) {
        viewModelScope.launch {
            try {
                if (currentUserId != null && repository != null) {
                    repository.updateWalletBalance(wallet, newBalance)
                } else {
                    _localWallets.value = _localWallets.value.map { w ->
                        if (w.id == wallet.id) w.copy(balance = newBalance) else w
                    }
                    persistAllLocalData()
                }
                _actionFeedback.value = "Saldo ${wallet.name} diperbarui menjadi ${formatRupiah(newBalance)}"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal mengubah saldo: ${e.localizedMessage}"
            }
        }
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            try {
                if (_localWallets.value.size <= 1) {
                    _actionFeedback.value = "Minimal harus ada 1 dompet aktif"
                    return@launch
                }
                if (currentUserId != null && repository != null) {
                    repository.deleteWallet(wallet.id)
                } else {
                    _localWallets.value = _localWallets.value.filterNot { it.id == wallet.id }
                    persistAllLocalData()
                }
                _actionFeedback.value = "Dompet ${wallet.name} dihapus"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus dompet: ${e.localizedMessage}"
            }
        }
    }

    fun deleteTransaction(tx: TransactionRecord, wallets: List<Wallet>) {
        if (currentUserId == null || repository == null) {
            if (tx.categoryKey == "pakai_darurat") {
                _localTransactions.value = _localTransactions.value.filterNot { it.id == tx.id }
                val currentEm = _emergencyFund.value
                _emergencyFund.value = currentEm.copy(
                    emergencyBalance = currentEm.emergencyBalance + tx.amount
                )
                persistAllLocalData()
                _actionFeedback.value = "Catatan darurat dihapus & Saldo Darurat Terpisah dikembalikan"
                return
            }

            // 1. Jika transaksi adalah Mutasi / Pindah Saldo antar dompet (TRANSFER)
            if (tx.type == "TRANSFER" && tx.id.startsWith("tx_transfer_")) {
                _localTransactions.value = _localTransactions.value.filterNot { it.id == tx.id }
                val body = tx.id.removePrefix("tx_transfer_")
                val fromId = body.substringBefore("_to_", missingDelimiterValue = tx.walletId)
                val rest = body.substringAfter("_to_", missingDelimiterValue = "")
                val toId = rest.substringBeforeLast("_", missingDelimiterValue = "")
                _localWallets.value = _localWallets.value.map { w ->
                    when (w.id) {
                        fromId -> w.copy(balance = w.balance + tx.amount)
                        toId -> w.copy(balance = w.balance - tx.amount)
                        else -> w
                    }
                }
                persistAllLocalData()
                _actionFeedback.value = "Mutasi saldo dibatalkan & saldo kedua dompet dikembalikan"
                return
            }

            val matchingWallet = wallets.find { it.id == tx.walletId } ?: _localWallets.value.find { it.id == tx.walletId }
            _localTransactions.value = _localTransactions.value.filterNot { it.id == tx.id }
            if (matchingWallet != null) {
                val revertDelta = if (tx.type == "INCOME") -tx.amount else tx.amount
                _localWallets.value = _localWallets.value.map { w ->
                    if (w.id == matchingWallet.id) w.copy(balance = w.balance + revertDelta) else w
                }
            }

            // 2. Jika transaksi adalah alokasi tabungan impian (SAVINGS_ALLOCATION), kurangi juga savedAmount di Goal!
            if (tx.type == "SAVINGS_ALLOCATION") {
                val goalTitleFromLabel = tx.categoryLabel.removePrefix("Tabungan:").trim()
                _localGoals.value = _localGoals.value.map { g ->
                    if (g.title.equals(goalTitleFromLabel, ignoreCase = true) || tx.note.contains(g.title, ignoreCase = true)) {
                        g.copy(savedAmount = (g.savedAmount - tx.amount).coerceAtLeast(0L))
                    } else g
                }
            }

            // 3. Jika transaksi berasal dari ceklist Tagihan Wajib (tx_bill_), kembalikan status tagihan wajib!
            if (tx.id.startsWith("tx_bill_")) {
                _mandatoryBills.value = _mandatoryBills.value.map { b ->
                    val matchesBillId = tx.id.startsWith("tx_bill_${b.id}_")
                    val matchesBillName = tx.note.startsWith(b.name)
                    if (matchesBillId || matchesBillName) {
                        val revertedPaid = (b.paidAmount - tx.amount).coerceAtLeast(0L)
                        val revertedStatus = when {
                            revertedPaid <= 0L -> "UNPAID"
                            revertedPaid < b.totalAmount -> "HALF_PAID"
                            else -> "PAID"
                        }
                        b.copy(paidAmount = revertedPaid, status = revertedStatus)
                    } else b
                }
                syncMandatoryBillsLimit()
            }
            persistAllLocalData()
            _actionFeedback.value = "Catatan dihapus & saldo ${tx.walletName} dikembalikan"
            return
        }

        viewModelScope.launch {
            try {
                val matchingWallet = wallets.find { it.id == tx.walletId }
                repository.deleteTransactionAndRevertWallet(tx, matchingWallet)
                _actionFeedback.value = "Catatan dihapus & saldo ${tx.walletName} dikembalikan"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus catatan: ${e.localizedMessage}"
            }
        }
    }

    fun addSavingsGoal(
        title: String,
        targetAmount: Long,
        dailyRecommendation: Long,
        note: String,
        iconKey: String
    ) {
        if (currentUserId == null || repository == null) {
            val newGoal = SavingsGoal(
                id = "goal_${System.currentTimeMillis()}",
                userId = "local_preview",
                title = title.trim(),
                targetAmount = targetAmount,
                savedAmount = 0L,
                dailyRecommendation = dailyRecommendation,
                note = note.trim(),
                iconKey = iconKey
            )
            _localGoals.value = _localGoals.value + newGoal
            persistAllLocalData()
            _actionFeedback.value = "Impian '$title' berhasil ditambahkan!"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repository.createSavingsGoal(
                    title = title,
                    targetAmount = targetAmount,
                    savedAmount = 0L,
                    dailyRecommendation = dailyRecommendation,
                    note = note,
                    iconKey = iconKey
                )
                _actionFeedback.value = "Impian '$title' berhasil ditambahkan!"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menambah impian: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun allocateSavingsToGoal(
        goal: SavingsGoal,
        wallet: Wallet,
        amount: Long,
        note: String
    ) {
        if (amount <= 0L) {
            _actionFeedback.value = "Nominal tabungan harus lebih dari Rp 0"
            return
        }
        if (currentUserId == null || repository == null) {
            _localWallets.value = _localWallets.value.map { w ->
                if (w.id == wallet.id) w.copy(balance = w.balance - amount) else w
            }
            _localGoals.value = _localGoals.value.map { g ->
                if (g.id == goal.id) g.copy(savedAmount = g.savedAmount + amount) else g
            }
            val newTx = TransactionRecord(
                id = "tx_${System.currentTimeMillis()}",
                userId = "local_preview",
                type = "SAVINGS_ALLOCATION",
                amount = amount,
                categoryKey = "tabungan_impian",
                categoryLabel = "Tabungan: ${goal.title}",
                frequencyTag = "TABUNGAN",
                walletId = wallet.id,
                walletName = wallet.name,
                note = note.ifBlank { "Setor tabungan ke ${goal.title}" },
                transactionDateMillis = System.currentTimeMillis()
            )
            _localTransactions.value = listOf(newTx) + _localTransactions.value
            persistAllLocalData()
            _actionFeedback.value = "Mantap! ${formatRupiah(amount)} dari ${wallet.name} masuk ke tabungan ${goal.title}"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repository.allocateToGoalFromWallet(
                    goal = goal,
                    wallet = wallet,
                    amount = amount,
                    note = note
                )
                _actionFeedback.value = "Mantap! ${formatRupiah(amount)} dari ${wallet.name} masuk ke tabungan ${goal.title}"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyetor tabungan: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        if (currentUserId == null || repository == null) {
            _localGoals.value = _localGoals.value.filterNot { it.id == goal.id }
            persistAllLocalData()
            _actionFeedback.value = "Target '${goal.title}' dihapus"
            return
        }
        viewModelScope.launch {
            try {
                repository.deleteSavingsGoal(goal.id)
                _actionFeedback.value = "Target '${goal.title}' dihapus"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus target: ${e.localizedMessage}"
            }
        }
    }

    fun rotateSessionSeed() {
        _sessionSeed.value = (_sessionSeed.value + 137 + ((System.currentTimeMillis() % 97L).toInt())) % 10000
    }

    fun requestGuruFinancialEvaluation(
        wallets: List<Wallet>,
        transactions: List<TransactionRecord>,
        goals: List<SavingsGoal>
    ) {
        rotateSessionSeed()
        val currentSeed = _sessionSeed.value
        viewModelScope.launch {
            _guruState.value = GuruAdviceState.Loading
            try {
                val totalBalance = wallets.sumOf { it.balance }
                val totalGoalSaved = goals.sumOf { it.savedAmount }
                val emergencyBalance = _emergencyFund.value.emergencyBalance
                val walletBreakdown = wallets.joinToString(", ") { "${it.name}: ${formatRupiah(it.balance)}" }

                val todayStart = getStartOfDayMillis()
                val monthStart = getStartOfMonthMillis()

                val todayExpenses = transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= todayStart }
                val todayExpenseSum = todayExpenses.sumOf { it.amount }
                val monthlyExpenseSum = transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= monthStart }.sumOf { it.amount }
                val monthlyIncomeSum = transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= monthStart }.sumOf { it.amount }

                val prompt = """
                    Analisis singkat kondisi keuangan:
                    - Saldo: ${formatRupiah(totalBalance)} ($walletBreakdown)
                    - Tabungan Target: ${formatRupiah(totalGoalSaved)}
                    - Simpanan Darurat: ${formatRupiah(emergencyBalance)}
                    - Pengeluaran Hari Ini: ${formatRupiah(todayExpenseSum)}
                    - Pengeluaran Bulan Ini: ${formatRupiah(monthlyExpenseSum)} vs Pemasukan: ${formatRupiah(monthlyIncomeSum)}
                    Hasilkan JSON sesuai skema.
                """.trimIndent()

                val response = withTimeout(30_000L) {
                    guruModel.generateContent(prompt)
                }
                val jsonString = response.text ?: throw IllegalStateException("Respons kosong.")
                val json = JSONObject(jsonString)

                val teguranText = json.optString("teguran", "Kontrol arus kas harianmu agar tetap aman.")
                val motivasiText = json.optString("motivasi", "Disiplin kecil setiap hari mempercepat targetmu.")
                val langkahText = json.optString("langkahKonkret", "Pastikan tagihan wajib lunas tepat waktu.")

                _guruState.value = GuruAdviceState.Success(
                    statusBadge = json.optString("statusBadge", "STATUS KE-$currentSeed"),
                    teguran = teguranText,
                    motivasi = motivasiText,
                    langkahKonkret = langkahText,
                    colorLevel = json.optString("colorLevel", "GREEN"),
                    saldoCritique = json.optString("saldoCritique", teguranText),
                    nominalBoxAnalysis = json.optString("nominalBoxAnalysis", "$teguranText $langkahText"),
                    categoryCritique = json.optString("categoryCritique", motivasiText)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Evaluation fallback used: ${e.message}")
                _guruState.value = GuruAdviceState.Error("Mode Evaluasi Lokal Aktif")
            }
        }
    }

    companion object {
        private const val TAG = "ArusKasViewModel"

        fun formatRupiah(amount: Long): String {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getNumberInstance(localeID)
            return if (amount < 0) {
                "-Rp ${numberFormat.format(-amount)}"
            } else {
                "Rp ${numberFormat.format(amount)}"
            }
        }

        fun formatCompactRupiah(amount: Long): String {
            val abs = kotlin.math.abs(amount)
            val prefix = if (amount < 0) "-Rp " else "Rp "
            return when {
                abs >= 1_000_000_000L -> String.format(Locale("in", "ID"), "%s%.1f M", prefix, abs / 1_000_000_000.0)
                abs >= 1_000_000L -> String.format(Locale("in", "ID"), "%s%.1f Jt", prefix, abs / 1_000_000.0)
                abs >= 1_000L -> String.format(Locale("in", "ID"), "%s%d Rb", prefix, abs / 1_000L)
                else -> "$prefix$abs"
            }
        }

        fun getStartOfDayMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getStartOfWeekMillis(): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -6)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getStartOfMonthMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getRemainingDaysInCurrentPeriod(period: String): Int {
            val cal = Calendar.getInstance()
            return when (period) {
                "HARIAN" -> 1
                "MINGGUAN" -> {
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1..7
                    (8 - dayOfWeek).coerceIn(1, 7)
                }
                else -> {
                    val today = cal.get(Calendar.DAY_OF_MONTH)
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    (maxDay - today + 1).coerceAtLeast(1)
                }
            }
        }

        /**
         * Menghitung jatah fleksibel harian otomatis dari sisa plafon limit kategori.
         * Karena beli bensin/rokok/makan tidak terikat jam/tanggal kaku, sisa kuota otomatis
         * dibagi sisa hari agar pengguna tahu rata-rata aman tanpa merasa dikekang.
         */
        fun calculateFlexibleAdaptiveQuotaPerDay(
            limitAmount: Long,
            spentAmount: Long,
            period: String
        ): Long {
            if (limitAmount <= 0L) return 0L
            val remainingQuota = (limitAmount - spentAmount).coerceAtLeast(0L)
            val remainingDays = getRemainingDaysInCurrentPeriod(period)
            return remainingQuota / remainingDays.toLong()
        }

        fun periodDisplayLabel(period: String): String {
            return when (period) {
                "HARIAN" -> "Per Hari"
                "MINGGUAN" -> "Per Minggu"
                "BEBAS" -> "Bebas Fleksibel"
                else -> "Per Bulan"
            }
        }

        fun calculateSpentForCategoryLimit(
            categoryKey: String,
            period: String,
            transactions: List<TransactionRecord>,
            mandatoryBills: List<MandatoryBillItem> = emptyList()
        ): Long {
            if (categoryKey == "tagihan_bulanan" && mandatoryBills.isNotEmpty()) {
                val paidFromChecklist = mandatoryBills.sumOf { it.paidAmount }
                val startMillis = getStartOfMonthMillis()
                val manualExtraTx = transactions
                    .filter {
                        it.type == "EXPENSE" &&
                            it.categoryKey == "tagihan_bulanan" &&
                            it.transactionDateMillis >= startMillis &&
                            !it.id.startsWith("tx_bill_")
                    }
                    .sumOf { it.amount }
                return paidFromChecklist + manualExtraTx
            }
            val startMillis = when (period) {
                "HARIAN" -> getStartOfDayMillis()
                "MINGGUAN" -> getStartOfWeekMillis()
                "BEBAS" -> 0L
                else -> getStartOfMonthMillis()
            }
            return transactions
                .filter { it.type == "EXPENSE" && it.categoryKey == categoryKey && it.transactionDateMillis >= startMillis }
                .sumOf { it.amount }
        }
    }
}
