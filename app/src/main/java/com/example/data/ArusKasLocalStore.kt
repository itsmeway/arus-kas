package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Penyimpanan Lokal Permanen (Offline-First Persistent Store) untuk ArusKas.
 * Memastikan:
 * 1. Kuesioner Profil Keuangan & Panduan Aplikasi HANYA muncul 1x seumur hidup saat pertama kali pakai
 *    (tidak akan pernah muncul berulang kali walaupun aplikasi ditutup-buka berkali-kali),
 *    namun tetap bisa dibuka manual kapan saja lewat menu Pengaturan / Tombol Alat di Dashboard.
 * 2. Daftar Bank & E-Wallet yang ditambah/dikurangi pengguna, Saldo, Riwayat Transaksi,
 *    Tagihan Wajib, Limit Kategori, Tabungan Impian, dan Dana Darurat tersimpan permanen di perangkat.
 */
class ArusKasLocalStore(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun hasCompletedInitialOnboardingOnce(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_SEEN_ONCE, false)
    }

    fun markInitialOnboardingSeenOnce() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN_ONCE, true).apply()
    }

    fun saveUserProfile(profile: UserFinancialProfile) {
        val habitsArray = JSONArray()
        profile.dailyHabits.forEach { habitsArray.put(it) }

        val obj = JSONObject().apply {
            put("isCompleted", profile.isCompleted)
            put("nickname", profile.nickname)
            put("gender", profile.gender)
            put("livingStatus", profile.livingStatus)
            put("incomeSourcePrimary", profile.incomeSourcePrimary)
            put("monthlyIncomeAmount", profile.monthlyIncomeAmount)
            put("paydayDate", profile.paydayDate)
            put("dependentsCount", profile.dependentsCount)
            put("transportMode", profile.transportMode)
            put("monthlyDebtObligation", profile.monthlyDebtObligation)
            put("primaryFinancialGoal", profile.primaryFinancialGoal)
            put("dailyHabits", habitsArray)
            put("controlMode", profile.controlMode)
            put("workShiftType", profile.workShiftType)
            put("workStartHour", profile.workStartHour)
            put("workStartMinute", profile.workStartMinute)
            put("lunchBreakHour", profile.lunchBreakHour)
            put("lunchBreakMinute", profile.lunchBreakMinute)
            put("workEndHour", profile.workEndHour)
            put("workEndMinute", profile.workEndMinute)
            put("eveningReviewHour", profile.eveningReviewHour)
            put("eveningReviewMinute", profile.eveningReviewMinute)
            put("activeDaysType", profile.activeDaysType)
            put("isPostActivityReminderEnabled", profile.isPostActivityReminderEnabled)
            put("isSmartScheduledReminderEnabled", profile.isSmartScheduledReminderEnabled)
        }
        val editor = prefs.edit().putString(KEY_USER_PROFILE, obj.toString())
        if (profile.isCompleted) {
            editor.putBoolean(KEY_ONBOARDING_SEEN_ONCE, true)
        }
        editor.apply()
    }

    fun loadUserProfile(): UserFinancialProfile {
        val seenOnce = hasCompletedInitialOnboardingOnce()
        val raw = prefs.getString(KEY_USER_PROFILE, null)
            ?: return UserFinancialProfile(isCompleted = seenOnce)
        return try {
            val obj = JSONObject(raw)
            val habitsJson = obj.optJSONArray("dailyHabits")
            val habits = mutableListOf<String>()
            if (habitsJson != null) {
                for (i in 0 until habitsJson.length()) {
                    val h = habitsJson.optString(i)
                    if (h.isNotBlank()) habits.add(h)
                }
            }
            val defaults = UserFinancialProfile()
            UserFinancialProfile(
                isCompleted = obj.optBoolean("isCompleted", false) || seenOnce,
                nickname = obj.optString("nickname", defaults.nickname),
                gender = obj.optString("gender", defaults.gender),
                livingStatus = obj.optString("livingStatus", defaults.livingStatus),
                incomeSourcePrimary = obj.optString("incomeSourcePrimary", defaults.incomeSourcePrimary),
                monthlyIncomeAmount = obj.optLong("monthlyIncomeAmount", defaults.monthlyIncomeAmount),
                paydayDate = obj.optInt("paydayDate", defaults.paydayDate),
                dependentsCount = obj.optInt("dependentsCount", defaults.dependentsCount),
                transportMode = obj.optString("transportMode", defaults.transportMode),
                monthlyDebtObligation = obj.optLong("monthlyDebtObligation", defaults.monthlyDebtObligation),
                primaryFinancialGoal = obj.optString("primaryFinancialGoal", defaults.primaryFinancialGoal),
                dailyHabits = if (habits.isNotEmpty()) habits else defaults.dailyHabits,
                controlMode = obj.optString("controlMode", defaults.controlMode),
                workShiftType = obj.optString("workShiftType", defaults.workShiftType),
                workStartHour = obj.optInt("workStartHour", defaults.workStartHour),
                workStartMinute = obj.optInt("workStartMinute", defaults.workStartMinute),
                lunchBreakHour = obj.optInt("lunchBreakHour", defaults.lunchBreakHour),
                lunchBreakMinute = obj.optInt("lunchBreakMinute", defaults.lunchBreakMinute),
                workEndHour = obj.optInt("workEndHour", defaults.workEndHour),
                workEndMinute = obj.optInt("workEndMinute", defaults.workEndMinute),
                eveningReviewHour = obj.optInt("eveningReviewHour", defaults.eveningReviewHour),
                eveningReviewMinute = obj.optInt("eveningReviewMinute", defaults.eveningReviewMinute),
                activeDaysType = obj.optString("activeDaysType", defaults.activeDaysType),
                isPostActivityReminderEnabled = obj.optBoolean("isPostActivityReminderEnabled", defaults.isPostActivityReminderEnabled),
                isSmartScheduledReminderEnabled = obj.optBoolean("isSmartScheduledReminderEnabled", defaults.isSmartScheduledReminderEnabled)
            )
        } catch (e: Exception) {
            UserFinancialProfile(isCompleted = seenOnce)
        }
    }

    fun saveAppSettings(settings: AppSettings) {
        val subObj = JSONObject().apply {
            put("isProActive", settings.subscription.isProActive)
            put("trialStartDateMillis", settings.subscription.trialStartDateMillis)
            put("proActivatedDateMillis", settings.subscription.proActivatedDateMillis)
            put("productId", settings.subscription.productId)
        }
        val obj = JSONObject().apply {
            put("language", settings.language.name)
            put("isDarkMode", settings.isDarkMode)
            put("useSystemTheme", settings.useSystemTheme)
            put("currencyFormat", settings.currencyFormat.name)
            put("isAppLockEnabled", settings.isAppLockEnabled)
            put("hideNominalOnDashboard", settings.hideNominalOnDashboard)
            put("dailyReminderNotification", settings.dailyReminderNotification)
            put("reminderHour", settings.reminderHour)
            put("reminderMinute", settings.reminderMinute)
            put("soundHapticFeedback", settings.soundHapticFeedback)
            put("autoBackupCloud", settings.autoBackupCloud)
            put("hasSeenUserGuide", settings.hasSeenUserGuide)
            put("subscription", subObj)
        }
        prefs.edit().putString(KEY_APP_SETTINGS, obj.toString()).apply()
    }

    fun loadAppSettings(): AppSettings {
        val seenOnce = hasCompletedInitialOnboardingOnce()
        val raw = prefs.getString(KEY_APP_SETTINGS, null)
            ?: return AppSettings(hasSeenUserGuide = seenOnce)
        return try {
            val obj = JSONObject(raw)
            val defaults = AppSettings()
            val langName = obj.optString("language", defaults.language.name)
            val lang = runCatching { AppLanguage.valueOf(langName) }.getOrDefault(defaults.language)
            val fmtName = obj.optString("currencyFormat", defaults.currencyFormat.name)
            val fmt = runCatching { CurrencyFormatOption.valueOf(fmtName) }.getOrDefault(defaults.currencyFormat)
            val subObj = obj.optJSONObject("subscription")
            val sub = if (subObj != null) {
                SubscriptionConfig(
                    isProActive = subObj.optBoolean("isProActive", false),
                    trialStartDateMillis = subObj.optLong("trialStartDateMillis", 0L),
                    proActivatedDateMillis = subObj.optLong("proActivatedDateMillis", 0L),
                    productId = subObj.optString("productId", "aruskas_pro_monthly_7k")
                )
            } else {
                defaults.subscription
            }
            AppSettings(
                language = lang,
                isDarkMode = obj.optBoolean("isDarkMode", defaults.isDarkMode),
                useSystemTheme = obj.optBoolean("useSystemTheme", defaults.useSystemTheme),
                currencyFormat = fmt,
                isAppLockEnabled = obj.optBoolean("isAppLockEnabled", defaults.isAppLockEnabled),
                hideNominalOnDashboard = obj.optBoolean("hideNominalOnDashboard", defaults.hideNominalOnDashboard),
                dailyReminderNotification = obj.optBoolean("dailyReminderNotification", defaults.dailyReminderNotification),
                reminderHour = obj.optInt("reminderHour", defaults.reminderHour),
                reminderMinute = obj.optInt("reminderMinute", defaults.reminderMinute),
                soundHapticFeedback = obj.optBoolean("soundHapticFeedback", defaults.soundHapticFeedback),
                autoBackupCloud = obj.optBoolean("autoBackupCloud", defaults.autoBackupCloud),
                hasSeenUserGuide = obj.optBoolean("hasSeenUserGuide", false) || seenOnce,
                subscription = sub
            )
        } catch (e: Exception) {
            AppSettings(hasSeenUserGuide = seenOnce)
        }
    }

    fun saveWallets(wallets: List<Wallet>) {
        val arr = JSONArray()
        wallets.forEach { w ->
            val obj = JSONObject().apply {
                put("id", w.id)
                put("userId", w.userId)
                put("name", w.name)
                put("walletType", w.walletType)
                put("balance", w.balance)
                put("colorHex", w.colorHex)
                put("iconKey", w.iconKey)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_WALLETS, arr.toString()).apply()
    }

    fun loadWallets(): List<Wallet> {
        val raw = prefs.getString(KEY_WALLETS, null) ?: return PresetCategories.defaultStarterWallets
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<Wallet>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                list.add(
                    Wallet(
                        id = obj.optString("id", "wallet_$i"),
                        userId = obj.optString("userId", "local_preview"),
                        name = obj.optString("name", "Dompet"),
                        walletType = obj.optString("walletType", "BANK"),
                        balance = obj.optLong("balance", 0L),
                        colorHex = obj.optString("colorHex", "#0066AE"),
                        iconKey = obj.optString("iconKey", "bank_bca")
                    )
                )
            }
            if (list.isNotEmpty()) list else PresetCategories.defaultStarterWallets
        } catch (e: Exception) {
            PresetCategories.defaultStarterWallets
        }
    }

    fun saveTransactions(transactions: List<TransactionRecord>) {
        val arr = JSONArray()
        transactions.forEach { tx ->
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("userId", tx.userId)
                put("type", tx.type)
                put("amount", tx.amount)
                put("categoryKey", tx.categoryKey)
                put("categoryLabel", tx.categoryLabel)
                put("frequencyTag", tx.frequencyTag)
                put("walletId", tx.walletId)
                put("walletName", tx.walletName)
                put("note", tx.note)
                put("transactionDateMillis", tx.transactionDateMillis)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_TRANSACTIONS, arr.toString()).apply()
    }

    fun loadTransactions(): List<TransactionRecord> {
        val raw = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<TransactionRecord>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                list.add(
                    TransactionRecord(
                        id = obj.optString("id", "tx_$i"),
                        userId = obj.optString("userId", "local_preview"),
                        type = obj.optString("type", "EXPENSE"),
                        amount = obj.optLong("amount", 0L),
                        categoryKey = obj.optString("categoryKey", "kebutuhan_pokok"),
                        categoryLabel = obj.optString("categoryLabel", "Pengeluaran"),
                        frequencyTag = obj.optString("frequencyTag", "HARIAN"),
                        walletId = obj.optString("walletId", ""),
                        walletName = obj.optString("walletName", ""),
                        note = obj.optString("note", ""),
                        transactionDateMillis = obj.optLong("transactionDateMillis", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveGoals(goals: List<SavingsGoal>) {
        val arr = JSONArray()
        goals.forEach { g ->
            val obj = JSONObject().apply {
                put("id", g.id)
                put("userId", g.userId)
                put("title", g.title)
                put("targetAmount", g.targetAmount)
                put("savedAmount", g.savedAmount)
                put("dailyRecommendation", g.dailyRecommendation)
                put("note", g.note)
                put("iconKey", g.iconKey)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_GOALS, arr.toString()).apply()
    }

    fun loadGoals(defaultGoals: List<SavingsGoal>): List<SavingsGoal> {
        val raw = prefs.getString(KEY_GOALS, null) ?: return defaultGoals
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<SavingsGoal>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                list.add(
                    SavingsGoal(
                        id = obj.optString("id", "goal_$i"),
                        userId = obj.optString("userId", "local_preview"),
                        title = obj.optString("title", "Target Impian"),
                        targetAmount = obj.optLong("targetAmount", 10_000_000L),
                        savedAmount = obj.optLong("savedAmount", 0L),
                        dailyRecommendation = obj.optLong("dailyRecommendation", 40_000L),
                        note = obj.optString("note", ""),
                        iconKey = obj.optString("iconKey", "laptop")
                    )
                )
            }
            list
        } catch (e: Exception) {
            defaultGoals
        }
    }

    fun saveMandatoryBills(bills: List<MandatoryBillItem>) {
        val arr = JSONArray()
        bills.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("name", b.name)
                put("totalAmount", b.totalAmount)
                put("paidAmount", b.paidAmount)
                put("dueScheduleLabel", b.dueScheduleLabel)
                put("dueDayOfMonth", b.dueDayOfMonth)
                if (b.renewalDayOfMonth != null) {
                    put("renewalDayOfMonth", b.renewalDayOfMonth)
                }
                put("status", b.status)
                put("billType", b.billType)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_MANDATORY_BILLS, arr.toString()).apply()
    }

    fun loadMandatoryBills(): List<MandatoryBillItem> {
        val raw = prefs.getString(KEY_MANDATORY_BILLS, null) ?: return PresetCategories.defaultMandatoryBills
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<MandatoryBillItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val hasRenewal = obj.has("renewalDayOfMonth") && !obj.isNull("renewalDayOfMonth")
                list.add(
                    MandatoryBillItem(
                        id = obj.optString("id", "bill_$i"),
                        name = obj.optString("name", "Tagihan"),
                        totalAmount = obj.optLong("totalAmount", 100_000L),
                        paidAmount = obj.optLong("paidAmount", 0L),
                        dueScheduleLabel = obj.optString("dueScheduleLabel", "Bayar setiap tanggal 1"),
                        dueDayOfMonth = obj.optInt("dueDayOfMonth", 1),
                        renewalDayOfMonth = if (hasRenewal) obj.optInt("renewalDayOfMonth") else null,
                        status = obj.optString("status", "UNPAID"),
                        billType = obj.optString("billType", "KOSAN")
                    )
                )
            }
            list
        } catch (e: Exception) {
            PresetCategories.defaultMandatoryBills
        }
    }

    fun saveCategoryLimits(limits: Map<String, CategoryLimitConfig>) {
        val arr = JSONArray()
        limits.forEach { (_, cfg) ->
            val obj = JSONObject().apply {
                put("categoryKey", cfg.categoryKey)
                put("limitAmount", cfg.limitAmount)
                put("period", cfg.period)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CATEGORY_LIMITS, arr.toString()).apply()
    }

    fun loadCategoryLimits(): Map<String, CategoryLimitConfig> {
        val raw = prefs.getString(KEY_CATEGORY_LIMITS, null) ?: return PresetCategories.defaultCategoryLimits
        return try {
            val arr = JSONArray(raw)
            val map = PresetCategories.defaultCategoryLimits.toMutableMap()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val key = obj.optString("categoryKey", "")
                if (key.isNotBlank()) {
                    map[key] = CategoryLimitConfig(
                        categoryKey = key,
                        limitAmount = obj.optLong("limitAmount", 500_000L),
                        period = obj.optString("period", "BULANAN")
                    )
                }
            }
            map
        } catch (e: Exception) {
            PresetCategories.defaultCategoryLimits
        }
    }

    fun saveEmergencyFund(config: EmergencyFundConfig) {
        val obj = JSONObject().apply {
            put("emergencyBalance", config.emergencyBalance)
            put("emergencyLimit", config.emergencyLimit)
            put("limitPeriod", config.limitPeriod)
            put("storagePlaceNote", config.storagePlaceNote)
        }
        prefs.edit().putString(KEY_EMERGENCY_FUND, obj.toString()).apply()
    }

    fun loadEmergencyFund(): EmergencyFundConfig {
        val raw = prefs.getString(KEY_EMERGENCY_FUND, null) ?: return EmergencyFundConfig()
        return try {
            val obj = JSONObject(raw)
            val defaults = EmergencyFundConfig()
            EmergencyFundConfig(
                emergencyBalance = obj.optLong("emergencyBalance", defaults.emergencyBalance),
                emergencyLimit = obj.optLong("emergencyLimit", defaults.emergencyLimit),
                limitPeriod = obj.optString("limitPeriod", defaults.limitPeriod),
                storagePlaceNote = obj.optString("storagePlaceNote", defaults.storagePlaceNote)
            )
        } catch (e: Exception) {
            EmergencyFundConfig()
        }
    }

    companion object {
        private const val PREFS_NAME = "aruskas_local_storage_v1"
        private const val KEY_ONBOARDING_SEEN_ONCE = "has_seen_onboarding_once"
        private const val KEY_USER_PROFILE = "user_financial_profile"
        private const val KEY_APP_SETTINGS = "app_settings"
        private const val KEY_WALLETS = "local_wallets"
        private const val KEY_TRANSACTIONS = "local_transactions"
        private const val KEY_GOALS = "local_goals"
        private const val KEY_MANDATORY_BILLS = "mandatory_bills"
        private const val KEY_CATEGORY_LIMITS = "category_limits"
        private const val KEY_EMERGENCY_FUND = "emergency_fund"
    }
}
