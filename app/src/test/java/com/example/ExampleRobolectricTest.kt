package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PresetCategories
import com.example.ui.ArusKasViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ArusKas", appName)
  }

  @Test
  fun `verify rupiah formatter and expense categories integrity`() {
    val formatted = ArusKasViewModel.formatRupiah(150000L)
    assertEquals("Rp 150.000", formatted)
    val compact = ArusKasViewModel.formatCompactRupiah(1500000L)
    assertEquals("Rp 1,5 Jt", compact)
    assertEquals(6, PresetCategories.expenseGrid3x3.size)
  }

  @Test
  fun `verify user guide keys and app settings default`() {
    val defaultSettings = com.example.data.AppSettings()
    org.junit.Assert.assertFalse(defaultSettings.hasSeenUserGuide)
    val titleId = com.example.data.AppLocalization.get("guide_title", com.example.data.AppLanguage.INDONESIAN)
    val titleEn = com.example.data.AppLocalization.get("guide_title", com.example.data.AppLanguage.ENGLISH)
    assertEquals("Cara Kerja ArusKas", titleId)
    assertEquals("How ArusKas Works", titleEn)
  }

  @Test
  fun `verify subscription 7k pricing and free trial logic`() {
    val sub = com.example.data.SubscriptionConfig()
    org.junit.Assert.assertEquals("aruskas_pro_monthly_7k", sub.productId)
    org.junit.Assert.assertTrue(sub.isTrialActive)
    org.junit.Assert.assertEquals(30, sub.remainingTrialDays)
    val priceId = com.example.data.AppLocalization.get("paywall_price_headline", com.example.data.AppLanguage.INDONESIAN)
    val priceEn = com.example.data.AppLocalization.get("paywall_price_headline", com.example.data.AppLanguage.ENGLISH)
    assertEquals("Rp 7.000 / Bulan", priceId)
    assertEquals("IDR 7,000 / Month", priceEn)
  }

  @Test
  fun `verify rupiah input formatting with thousands dots and Rp prefix`() {
    val formatted50k = com.example.ui.CurrencyInputFormatter.formatDigits("50000")
    assertEquals("50.000", formatted50k)

    val formatted1_5M = com.example.ui.CurrencyInputFormatter.formatDigits("1500000")
    assertEquals("1.500.000", formatted1_5M)

    val withPrefix = com.example.ui.CurrencyInputFormatter.formatWithPrefix("50000")
    assertEquals("Rp 50.000", withPrefix)

    val parsedLong = com.example.ui.CurrencyInputFormatter.parseToLong("Rp 1.500.000")
    assertEquals(1500000L, parsedLong)
  }

  @Test
  fun `verify mandatory bills checklist states and flexible real life expense logic`() {
    val defaultBills = PresetCategories.defaultMandatoryBills
    org.junit.Assert.assertTrue(defaultBills.any { it.name.contains("Kosan") && it.dueDayOfMonth == 1 })
    org.junit.Assert.assertTrue(defaultBills.any { it.name.contains("Kuota") && it.renewalDayOfMonth != null })

    val kosanBill = defaultBills.first { it.id == "bill_kosan" }.copy(totalAmount = 800_000L, paidAmount = 400_000L, status = "HALF_PAID")
    val kuotaBill = defaultBills.first { it.id == "bill_kuota" }.copy(totalAmount = 100_000L, paidAmount = 100_000L, status = "PAID")
    val spentBills = ArusKasViewModel.calculateSpentForCategoryLimit(
      categoryKey = "tagihan_bulanan",
      period = "BULANAN",
      transactions = emptyList(),
      mandatoryBills = listOf(kosanBill, kuotaBill)
    )
    assertEquals(500_000L, spentBills) // 400k (half kosan) + 100k (paid kuota)

    val quickItems = PresetCategories.realLifeQuickItems
    org.junit.Assert.assertTrue(quickItems.any { it.label.contains("Bensin") })
    org.junit.Assert.assertTrue(quickItems.any { it.label.contains("Rokok") })

    val adaptivePerDay = ArusKasViewModel.calculateFlexibleAdaptiveQuotaPerDay(
      limitAmount = 450_000L,
      spentAmount = 150_000L,
      period = "BULANAN"
    )
    org.junit.Assert.assertTrue(adaptivePerDay > 0L)
  }

  @Test
  fun `verify feature help cloud content database for all core tools`() {
    val totalSaldoHelp = com.example.ui.FeatureHelpDatabase.TOTAL_SALDO
    assertEquals("Total Saldo & Multi-Dompet", totalSaldoHelp.title)
    org.junit.Assert.assertTrue(totalSaldoHelp.summary.isNotBlank())
    org.junit.Assert.assertTrue(totalSaldoHelp.keyPoints.isNotEmpty())

    val billsHelp = com.example.ui.FeatureHelpDatabase.TAGIHAN_WAJIB
    assertEquals("Ceklist Tagihan Wajib & Siklus Jatuh Tempo", billsHelp.title)
    org.junit.Assert.assertTrue(billsHelp.keyPoints.any { it.first.contains("Jatuh Tempo") })

    val pintasanHelp = com.example.ui.FeatureHelpDatabase.PINTASAN_SEHARI_HARI
    org.junit.Assert.assertTrue(pintasanHelp.summary.contains("Bebas Kapan Aja") || pintasanHelp.title.contains("Pintasan"))

    val limitHelp = com.example.ui.FeatureHelpDatabase.KATEGORI_LIMIT
    org.junit.Assert.assertTrue(limitHelp.title.contains("Limit"))
  }

  @Test
  fun `verify daily routine anti-clash reminder schedule and busy hours detection`() {
    val profile = com.example.data.UserFinancialProfile(
      workShiftType = "KANTOR_NORMAL",
      workStartHour = 8,
      workStartMinute = 30,
      lunchBreakHour = 12,
      lunchBreakMinute = 30,
      workEndHour = 17,
      workEndMinute = 30,
      eveningReviewHour = 21,
      eveningReviewMinute = 0,
      activeDaysType = "SENIN_JUMAT",
      isPostActivityReminderEnabled = true,
      isSmartScheduledReminderEnabled = true
    )

    // Test calendar for Monday at 10:00 (Jam sibuk kerja pagi) -> WAJIB BUNGKAM (true)
    val mondayWorkMorning = java.util.Calendar.getInstance().apply {
      set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
      set(java.util.Calendar.HOUR_OF_DAY, 10)
      set(java.util.Calendar.MINUTE, 0)
    }
    val isBusy1 = com.example.data.ActivityReminderNotificationHelper.isWithinBusyWorkHours(profile, mondayWorkMorning)
    org.junit.Assert.assertTrue(isBusy1)

    // Test calendar for Monday at 12:45 (Sehabis makan siang) -> BEBAS BENTROK (false)
    val mondayLunch = java.util.Calendar.getInstance().apply {
      set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
      set(java.util.Calendar.HOUR_OF_DAY, 12)
      set(java.util.Calendar.MINUTE, 45)
    }
    val isBusy2 = com.example.data.ActivityReminderNotificationHelper.isWithinBusyWorkHours(profile, mondayLunch)
    org.junit.Assert.assertFalse(isBusy2)

    // Test calendar for Monday at 19:30 (Sehabis jam pulang kerja) -> BEBAS BENTROK (false)
    val mondayEvening = java.util.Calendar.getInstance().apply {
      set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
      set(java.util.Calendar.HOUR_OF_DAY, 19)
      set(java.util.Calendar.MINUTE, 30)
    }
    val isBusy3 = com.example.data.ActivityReminderNotificationHelper.isWithinBusyWorkHours(profile, mondayEvening)
    org.junit.Assert.assertFalse(isBusy3)

    // Test calendar for Sunday (Hari libur) -> BEBAS BENTROK (false)
    val sundayTime = java.util.Calendar.getInstance().apply {
      set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
      set(java.util.Calendar.HOUR_OF_DAY, 10)
      set(java.util.Calendar.MINUTE, 0)
    }
    val isBusy4 = com.example.data.ActivityReminderNotificationHelper.isWithinBusyWorkHours(profile, sundayTime)
    org.junit.Assert.assertFalse(isBusy4)

    // Verify FeatureHelp for PENGINGAT_AKTIVITAS
    val reminderHelp = com.example.ui.FeatureHelpDatabase.PENGINGAT_AKTIVITAS
    org.junit.Assert.assertTrue(reminderHelp.title.contains("Pengingat"))
    org.junit.Assert.assertTrue(reminderHelp.howToUseStepByStep.size >= 3)
    org.junit.Assert.assertTrue(reminderHelp.keyPoints.any { it.first.contains("Anti-Bentrok") })
  }

  @Test
  fun `verify strictly one-time onboarding and user guide persistence across multiple app restarts`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("aruskas_local_storage_v1", Context.MODE_PRIVATE).edit().clear().commit()

    val store1 = com.example.data.ArusKasLocalStore(context)
    val dummyRepo: com.example.data.ArusKasRepository? = null

    // Launch #1 (Fresh Install): Questionnaire & Guide have NOT been seen yet
    val vmLaunch1 = ArusKasViewModel(dummyRepo, null, store1)
    org.junit.Assert.assertFalse(vmLaunch1.userProfile.value.isCompleted)
    org.junit.Assert.assertFalse(vmLaunch1.appSettings.value.hasSeenUserGuide)

    // User customizes Banks & E-Wallets in Questionnaire (removes unused, adds BRI & Uang Tunai)
    val customWallets = listOf(
      com.example.data.Wallet(id = "wallet_bca", name = "Bank BCA", walletType = "BANK", balance = 4_000_000L),
      com.example.data.Wallet(id = "wallet_bri", name = "Bank BRI", walletType = "BANK", balance = 1_500_000L),
      com.example.data.Wallet(id = "wallet_dana", name = "DANA", walletType = "EWALLET", balance = 500_000L),
      com.example.data.Wallet(id = "wallet_tunai", name = "Uang Tunai (Cash)", walletType = "CASH", balance = 250_000L)
    )
    val configuredBills = listOf(
      com.example.data.MandatoryBillItem(
        id = "bill_kosan",
        name = "Bayar Kosan",
        totalAmount = 850_000L,
        paidAmount = 0L,
        dueScheduleLabel = "Bayar setiap tanggal 1",
        dueDayOfMonth = 1,
        status = "UNPAID",
        billType = "KOSAN"
      )
    )

    vmLaunch1.applyFinancialProfileSetup(
      profile = com.example.data.UserFinancialProfile(
        nickname = "Kang Iwa",
        monthlyIncomeAmount = 6_000_000L
      ),
      configuredWallets = customWallets,
      configuredBills = configuredBills,
      customCategoryLimits = PresetCategories.defaultCategoryLimits
    )

    org.junit.Assert.assertTrue(vmLaunch1.userProfile.value.isCompleted)
    org.junit.Assert.assertTrue(vmLaunch1.appSettings.value.hasSeenUserGuide)

    // Simulate Restart #2, #3, #4: User closes and reopens the app repeatedly
    repeat(3) {
      val restartedStore = com.example.data.ArusKasLocalStore(context)
      val restartedVm = ArusKasViewModel(dummyRepo, null, restartedStore)
      // Must NEVER ask onboarding questions or auto-show guide again!
      org.junit.Assert.assertTrue(restartedVm.userProfile.value.isCompleted)
      org.junit.Assert.assertTrue(restartedVm.appSettings.value.hasSeenUserGuide)
      assertEquals("Kang Iwa", restartedVm.userProfile.value.nickname)
      assertEquals(6_000_000L, restartedVm.userProfile.value.monthlyIncomeAmount)

      val loadedWallets = (restartedVm.walletsState.value as com.example.ui.UiState.Success).data
      assertEquals(4, loadedWallets.size)
      org.junit.Assert.assertTrue(loadedWallets.any { it.name == "Bank BRI" && it.balance == 1_500_000L })
      org.junit.Assert.assertTrue(loadedWallets.any { it.name == "Uang Tunai (Cash)" && it.balance == 250_000L })
      org.junit.Assert.assertFalse(loadedWallets.any { it.name == "ShopeePay" })
    }
  }

  @Test
  fun `verify skipping onboarding also marks one-time completion permanently`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("aruskas_local_storage_v1", Context.MODE_PRIVATE).edit().clear().commit()

    val store = com.example.data.ArusKasLocalStore(context)
    val dummyRepo: com.example.data.ArusKasRepository? = null
    val vm1 = ArusKasViewModel(dummyRepo, null, store)
    org.junit.Assert.assertFalse(vm1.userProfile.value.isCompleted)

    // User clicks "Lewati" (Skip)
    vm1.dismissOrSkipOnboarding()
    org.junit.Assert.assertTrue(vm1.userProfile.value.isCompleted)
    org.junit.Assert.assertTrue(vm1.appSettings.value.hasSeenUserGuide)

    // Reopen app: still completed, never bothers user again
    val vm2 = ArusKasViewModel(dummyRepo, null, com.example.data.ArusKasLocalStore(context))
    org.junit.Assert.assertTrue(vm2.userProfile.value.isCompleted)
    org.junit.Assert.assertTrue(vm2.appSettings.value.hasSeenUserGuide)
  }

  @Test
  fun `verify mandatory bill uncheck refunds wallet and cleans up transaction history`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("aruskas_local_storage_v1", Context.MODE_PRIVATE).edit().clear().commit()
    val vm = ArusKasViewModel(null, null, com.example.data.ArusKasLocalStore(context))

    val bcaBefore = (vm.walletsState.value as com.example.ui.UiState.Success).data.first { it.id == "wallet_bca" }
    val initialBalance = bcaBefore.balance
    val kosanBill = vm.mandatoryBills.value.first { it.id == "bill_kosan" } // 850_000 UNPAID

    // 1. Check Kosan as PAID -> deducts 850_000 and creates tx_bill_bill_kosan_*
    vm.updateMandatoryBillStatus(kosanBill, "PAID", bcaBefore)
    val bcaAfterPaid = (vm.walletsState.value as com.example.ui.UiState.Success).data.first { it.id == "wallet_bca" }
    assertEquals(initialBalance - 850_000L, bcaAfterPaid.balance)
    val txsAfterPaid = (vm.transactionsState.value as com.example.ui.UiState.Success).data
    org.junit.Assert.assertTrue(txsAfterPaid.any { it.id.startsWith("tx_bill_bill_kosan_") && it.amount == 850_000L })

    // 2. Uncheck Kosan back to UNPAID -> refunds 850_000 AND removes the bill transaction so expenses don't stay inflated!
    val paidKosanBill = vm.mandatoryBills.value.first { it.id == "bill_kosan" }
    vm.updateMandatoryBillStatus(paidKosanBill, "UNPAID", bcaAfterPaid)
    val bcaAfterRefund = (vm.walletsState.value as com.example.ui.UiState.Success).data.first { it.id == "wallet_bca" }
    assertEquals(initialBalance, bcaAfterRefund.balance)
    val txsAfterUnpaid = (vm.transactionsState.value as com.example.ui.UiState.Success).data
    org.junit.Assert.assertFalse(txsAfterUnpaid.any { it.id.startsWith("tx_bill_bill_kosan_") })
  }

  @Test
  fun `verify inter-wallet transfer and savings allocation deletion reversal`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("aruskas_local_storage_v1", Context.MODE_PRIVATE).edit().clear().commit()
    val vm = ArusKasViewModel(null, null, com.example.data.ArusKasLocalStore(context))

    val wallets0 = (vm.walletsState.value as com.example.ui.UiState.Success).data
    val bca0 = wallets0.first { it.id == "wallet_bca" }
    val dana0 = wallets0.first { it.id == "wallet_dana" }

    // 1. Transfer Rp 200.000 from BCA to DANA with Rp 1.000 admin fee
    vm.transferBetweenWallets(
      fromWallet = bca0,
      toWallet = dana0,
      amount = 200_000L,
      adminFee = 1_000L,
      note = "Top up DANA"
    )

    val wallets1 = (vm.walletsState.value as com.example.ui.UiState.Success).data
    val bca1 = wallets1.first { it.id == "wallet_bca" }
    val dana1 = wallets1.first { it.id == "wallet_dana" }
    assertEquals(bca0.balance - 201_000L, bca1.balance)
    assertEquals(dana0.balance + 200_000L, dana1.balance)

    // Only the Rp 1.000 admin fee is an EXPENSE; the Rp 200.000 transfer is type TRANSFER
    val txs1 = (vm.transactionsState.value as com.example.ui.UiState.Success).data
    val totalExpenses = txs1.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    assertEquals(1_000L, totalExpenses)

    // 2. Allocate Rp 100.000 savings to Goal, then delete that transaction -> both wallet & goal revert
    val goal0 = (vm.goalsState.value as com.example.ui.UiState.Success).data.first()
    val savedBefore = goal0.savedAmount
    vm.allocateSavingsToGoal(goal0, dana1, 100_000L, "Nabung rutin")

    val goalAfterSave = (vm.goalsState.value as com.example.ui.UiState.Success).data.first()
    assertEquals(savedBefore + 100_000L, goalAfterSave.savedAmount)

    val savingsTx = (vm.transactionsState.value as com.example.ui.UiState.Success).data.first { it.type == "SAVINGS_ALLOCATION" }
    val currentWallets = (vm.walletsState.value as com.example.ui.UiState.Success).data
    vm.deleteTransaction(savingsTx, currentWallets)

    val goalAfterDelete = (vm.goalsState.value as com.example.ui.UiState.Success).data.first()
    val danaAfterDelete = (vm.walletsState.value as com.example.ui.UiState.Success).data.first { it.id == "wallet_dana" }
    assertEquals(savedBefore, goalAfterDelete.savedAmount)
    assertEquals(dana1.balance, danaAfterDelete.balance)
  }
}
