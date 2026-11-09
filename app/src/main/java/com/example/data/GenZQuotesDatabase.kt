package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.ArusKasViewModel
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue
import kotlin.math.max

data class GenZDailyAnalysis(
    val message: String,
    val lineColor: Color,
    val saldoLineColor: Color,
    val nominalLineColor: Color,
    val categoryLineColor: Color
)

/**
 * Database kata-kata pengingat, kritik pedas, saran, dan motivasi harian
 * dengan bahasa sehari-hari anak Gen Z ("lo/gue", "woy", "cuy", "bestie", "boncos")
 * yang mengacu langsung pada keseharian & data transaksi pengguna.
 */
object GenZQuotesDatabase {

    fun generatePersonaAnalysis(
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
        language: AppLanguage = AppLanguage.INDONESIAN,
        aiOverrideText: String? = null,
        aiColorLevel: String? = null
    ): GenZDailyAnalysis {
        val activeGoal = goals.firstOrNull()
        val goalName = activeGoal?.title ?: "Laptop Impian"
        val goalRemaining = if (activeGoal != null) {
            (activeGoal.targetAmount - activeGoal.savedAmount).coerceAtLeast(0L)
        } else 8_750_000L

        val monthStart = ArusKasViewModel.getStartOfMonthMillis()
        val jajanSpent = transactions
            .filter {
                it.type == "EXPENSE" &&
                    (it.categoryKey == "gaya_hidup" || it.categoryKey == "gaya_hidup_bebas" || it.categoryKey == "jajan_cemilan") &&
                    it.transactionDateMillis >= monthStart
            }
            .sumOf { it.amount }

        val pokokSpent = transactions
            .filter {
                it.type == "EXPENSE" &&
                    it.categoryKey == "kebutuhan_pokok" &&
                    it.transactionDateMillis >= monthStart
            }
            .sumOf { it.amount }

        val rawatDiriSpent = transactions
            .filter {
                it.type == "EXPENSE" &&
                    it.categoryKey == "rawat_diri" &&
                    it.transactionDateMillis >= monthStart
            }
            .sumOf { it.amount }

        // Estimasi keseharian (cup kopi @22rb, porsi seblak/jajan @18rb, liter bensin @10rb)
        val estCupsOfCoffee = max(3L, (if (jajanSpent > 0L) jajanSpent else max(todayExpense, 330_000L)) / 22_000L)
        val estSeblakPortions = max(4L, (if (jajanSpent > 0L) jajanSpent else 270_000L) / 18_000L)
        val saldoCompact = ArusKasViewModel.formatCompactRupiah(totalBalance)
        val todayCompact = ArusKasViewModel.formatCompactRupiah(todayExpense)
        val monthCompact = ArusKasViewModel.formatCompactRupiah(monthExpense)
        val tabunganCompact = ArusKasViewModel.formatCompactRupiah(totalTabunganSaved)
        val sisaGoalCompact = ArusKasViewModel.formatCompactRupiah(goalRemaining)

        // Evaluasi 3 Indikator Garis Warna:
        // 1. Garis Warna Kotak Total Saldo
        val saldoLineColor = when {
            totalBalance <= 150_000L -> CoralExpense
            totalBalance <= 500_000L || todayExpense > 150_000L -> GoldAccent
            else -> EmeraldPrimary
        }

        // 2. Garis Warna Kotak Masukan Nominal
        val nominalLineColor = when {
            todayExpense >= 250_000L -> CoralExpense
            todayExpense >= 100_000L -> GoldAccent
            selectedCategory.type == "INCOME" -> EmeraldPrimary
            else -> SkyBlue
        }

        // 3. Garis Warna Kotak Kategori Pengeluaran/Pemasukan
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
        val catRatio = if (activeLimitCfg.limitAmount > 0L) {
            catSpent.toFloat() / activeLimitCfg.limitAmount.toFloat()
        } else 0f
        val categoryLineColor = when {
            selectedCategory.type == "INCOME" -> EmeraldPrimary
            activeLimitCfg.limitAmount <= 0L -> SkyBlue
            catRatio >= 1f -> CoralExpense
            catRatio >= 0.75f -> GoldAccent
            else -> EmeraldPrimary
        }

        // Database puluhan kalimat bahasa gaul sehari-hari Gen Z (mengingatkan, mengkritik, memotivasi)
        val genZDatabase = buildList {
            // Kategori: Kritik Jajan / Kopi / Nongkrong
            add(
                "Woy, pengeluaran jajan lo kebanyakan bulan ini, setara udah minum kopi $estCupsOfCoffee cup! Sehat dikit lah dompet sama lambung lo, mending sisihin buat $goalName."
            )
            add(
                "Jujurly jempol lo licin banget kalau urusan jajan & healing, udah kebakar $monthCompact bulan ini cuy. Rem dikit napa, masa tua nanti nggak bisa dibayar pake struk kopi!"
            )
            add(
                "Bro, kalau duit jajan lo yang setara $estSeblakPortions mangkok seblak itu dikumpulin, $goalName lo udah makin deket lunas tau nggak? Kurang $sisaGoalCompact lagi tuh!"
            )
            add(
                "Woy bestie, tiap stres dikit larinya ke checkout keranjang oren sama es kopi susu. Dompet siap pakai lo sisa $saldoCompact nih, jangan sampe akhir bulan makan angin!"
            )
            add(
                "Lo kata duit di GoPay, ShopeePay, sama DANA itu daun kering bisa tumbuh sendiri? Udah keluar $todayCompact hari ini cuy, tahan dulu nongkrongnya!"
            )
            add(
                "Anjir, baru juga pertengahan jalan udah ngabisin $monthCompact aja lo. Kalau bukan kebutuhan mendesak, tutup dulu aplikasi ojol makanannya sekarang juga!"
            )

            // Kategori: Pengingat Saldo & Dompet Harian
            add(
                "Cek fakta nih cuy: duit siap pakai lo tinggal $saldoCompact. Nggak usah sok traktir temen dulu kalau besok bensin motor aja masih mikir!"
            )
            add(
                "Woy, saldo BCA sama e-wallet lo totalnya $saldoCompact di luar tabungan. Jangan gatel dipake top-up yang nggak jelas, simpen buat pegangan harian!"
            )
            add(
                "Gue perhatiin duit lo ngalir cepet banget kayak kuota ketemu TikTok. Hari ini aja udah kepotong $todayCompact, yakin malem ini nggak mau puasa jajan dulu?"
            )
            add(
                "Dompet lo lagi mode santuy di angka $saldoCompact sih, tapi awas penyakit lama lo kumat: liat saldo agak tebel dikit langsung berasa sultan!"
            )
            add(
                "Ingat ya bro, yang namanya uang dingin itu di kulkas, bukan di e-wallet lo. Saldo $saldoCompact ini buat hidup sehari-hari, jangan dihajar checkout tengah malem!"
            )

            // Kategori: Motivasi Kerja Keras (Gaji, Ngojek Online, Usaha) & Tabungan Target
            add(
                "Lo udah capek-capek cari duit dari gaji, ngojek kepanasan, sama muter usaha—masa hasilnya cuma numpang lewat buat bayarin lifestyle orang? Yuk amankan buat $goalName!"
            )
            add(
                "Semangat cuy! Tabungan $goalName lo udah ngumpul $tabunganCompact di kantong terpisah. Dikit lagi kebeli, jangan dikhianatin sama godaan diskon palsu ya!"
            )
            add(
                "Woy keren juga lo kalau konsisten gini! Tabungan $goalName tinggal kurang $sisaGoalCompact lagi. Abis narik ngojek atau gajian, langsung potong di depan biar nggak kepake!"
            )
            add(
                "Capek kerja seharian itu wajar bestie, tapi self-reward nggak harus bikin boncos tiap hari kali. Mending liat progres $goalName lo yang udah $tabunganCompact, bikin bangga!"
            )
            add(
                "Bayangin pas $goalName udah di tangan lo hasil keringet sendiri tanpa ngutang/paylater. Valid no debat paling puas sih, makanya hari ini hemat dulu ya bro!"
            )

            // Kategori: Kebutuhan Pokok, Kosan, Bensin, & Rawat Diri
            add(
                "Beli bensin, air minum, sama sabun sih wajib cuy (udah ${ArusKasViewModel.formatCompactRupiah(pokokSpent)} bulan ini), tapi pas ke minimarket nggak usah sambil comot cemilan sekeranjang juga kali!"
            )
            add(
                "Rawat diri & skincare udah habis ${ArusKasViewModel.formatCompactRupiah(rawatDiriSpent)} bulan ini bestie. Muka glowing itu penting, tapi dompet glowing jauh lebih menenangkan jiwa!"
            )
            add(
                "Woy jangan sampe tagihan wajib & kosan keteteran gara-gara keseringan nongkrong. Prioritasin yang bikin lo hidup tenang dulu, baru sisanya buat seneng-seneng!"
            )

            // Kategori: Dana Darurat & Kebiasaan Catat Uang
            add(
                "Simpanan darurat lo sekarang ${ArusKasViewModel.formatCompactRupiah(emergencyBalance)} di kantong terpisah. Jangan pernah diutak-atik buat beli kopi atau kuota ya, namanya juga darurat cuy!"
            )
            add(
                "Satu hal yang gue salut dari lo: lo berani jujur nyatet pengeluaran di sini. Kebanyakan orang pura-pura buta sampe saldo Rp 0, lo udah selangkah lebih waras bro!"
            )
            add(
                "Woy, inget pesan gue: pisahin rekening tabungan sama dompet jajan harian lo. Kalau masih satu kantong, dijamin ludes tak bersisa pas tanggal tua!"
            )
            add(
                "Hari ini pengeluaran lo $todayCompact. Kalau malem ini lo berhasil nggak ngeluarin duit sepeser pun lagi, fix lo juara bertahan lawan impulsif hari ini!"
            )
            add(
                "Jangan dikit-dikit bilang 'duit bisa dicari lagi' pas mau boros cuy. Iya bisa dicari, tapi inget encoknya pas ngojek sama pusingnya kerja!"
            )
        }

        val englishDatabase = listOf(
            "Bro, your snack and coffee expenses are wild this month, equivalent to $estCupsOfCoffee cups of iced lattes! Have some mercy on your wallet and stash it for $goalName instead.",
            "Honestly your thumbs have zero chill when it comes to checkout & treats—burnt $monthCompact already this month! Tap the brakes before payday turns into instant regret.",
            "Hey bestie, if you stacked all those snack orders equal to $estSeblakPortions takeout meals, your $goalName would be almost fully funded! You only need $sisaGoalCompact more!",
            "Reality check bestie: your safe-to-spend cash is down to $saldoCompact. Don't be out here playing Santa treating everyone when tomorrow's fuel money is tight!",
            "You logged $todayCompact in spending today. If you lock your cards and spend zero more tonight, you officially win the impulse-control trophy today!",
            "One thing to respect about you: you actually log every single dollar honestly. Most people play blind until their balance hits zero—you're way ahead of the game!"
        )

        val targetPool = when (language) {
            AppLanguage.ENGLISH -> englishDatabase
            AppLanguage.INDONESIAN -> genZDatabase
        }

        val pickedMessage = if (!aiOverrideText.isNullOrBlank()) {
            aiOverrideText
        } else {
            targetPool[kotlin.math.abs(sessionSeed) % targetPool.size]
        }

        val mainBoxLineColor = when (aiColorLevel?.uppercase()) {
            "RED" -> CoralExpense
            "YELLOW" -> GoldAccent
            "BLUE" -> SkyBlue
            "GREEN" -> EmeraldPrimary
            else -> when {
                catRatio >= 1f || totalBalance <= 150_000L -> CoralExpense
                catRatio >= 0.75f || todayExpense >= 100_000L -> GoldAccent
                else -> EmeraldPrimary
            }
        }

        return GenZDailyAnalysis(
            message = pickedMessage,
            lineColor = mainBoxLineColor,
            saldoLineColor = saldoLineColor,
            nominalLineColor = nominalLineColor,
            categoryLineColor = categoryLineColor
        )
    }

    fun liveTypingGenZRoast(
        typedAmount: Long,
        isIncomeMode: Boolean,
        isEmergencyExpenseSelected: Boolean,
        selectedCategoryLabel: String,
        totalBalance: Long,
        emergencyBalance: Long,
        language: AppLanguage = AppLanguage.INDONESIAN
    ): Pair<Color, String>? {
        if (typedAmount <= 0L) return null
        val rp = ArusKasViewModel.formatRupiah(typedAmount)
        return when (language) {
            AppLanguage.ENGLISH -> when {
                isIncomeMode -> EmeraldPrimary to "Awesome! Fresh cash inflow of $rp from $selectedCategoryLabel! Lock in your target savings slice before spending tempts you."
                isEmergencyExpenseSelected && typedAmount > emergencyBalance -> CoralExpense to "Wait, emergency of $rp?! Your emergency stash only has ${ArusKasViewModel.formatRupiah(emergencyBalance)}—don't overdraw!"
                isEmergencyExpenseSelected -> GoldAccent to "Deducted $rp from your isolated emergency stash. Hope whatever came up gets sorted quickly!"
                typedAmount > totalBalance -> CoralExpense to "Whoa hold on bestie! Spending $rp on $selectedCategoryLabel when your safe balance is only ${ArusKasViewModel.formatRupiah(totalBalance)}?! Reality check time!"
                typedAmount >= 200_000L -> GoldAccent to "That's a chunky $rp on $selectedCategoryLabel at once! Worth roughly ${max(2L, typedAmount / 22_000L)} coffees—is it a real need or an impulse craving?"
                else -> SkyBlue to "Logging $rp for $selectedCategoryLabel? Your remaining safe-to-spend balance will be ${ArusKasViewModel.formatRupiah((totalBalance - typedAmount).coerceAtLeast(0L))}. No regrets!"
            }
            AppLanguage.INDONESIAN -> when {
                isIncomeMode -> EmeraldPrimary to "Wih mantap cuy masuk $rp dari $selectedCategoryLabel! Langsung amankan jatah tabungan target dulu sebelum gatel dipake jajan ya."
                isEmergencyExpenseSelected && typedAmount > emergencyBalance -> CoralExpense to "Woy seriusan darurat $rp? Simpanan darurat lo aja cuma ${ArusKasViewModel.formatRupiah(emergencyBalance)} cuy, jangan sampe boncos!"
                isEmergencyExpenseSelected -> GoldAccent to "Oke $rp kepotong dari pos darurat terpisah lo. Semoga urusan mendadaknya cepet kelar ya bro!"
                typedAmount > totalBalance -> CoralExpense to "Buset dah bestie, mau ngeluarin $rp buat $selectedCategoryLabel padahal saldo siap pakai lo cuma ${ArusKasViewModel.formatRupiah(totalBalance)}?! Sadar diri woy!"
                typedAmount >= 200_000L -> GoldAccent to "Woy angka gede nih $rp sekaligus buat $selectedCategoryLabel! Setara ${max(2L, typedAmount / 22_000L)} cup kopi nih, yakin beneran butuh atau cuma kalap sesaat?"
                else -> SkyBlue to "Mau nyatet $rp buat $selectedCategoryLabel nih cuy? Sisa saldo siap pakai lo nanti tinggal ${ArusKasViewModel.formatRupiah((totalBalance - typedAmount).coerceAtLeast(0L))}, pastiin nggak nyesel ya!"
            }
        }
    }
}
