package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Wallet(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val walletType: String = "BANK", // BANK, EWALLET, CASH
    val balance: Long = 0L,
    val colorHex: String = "#10B981",
    val iconKey: String = "bank_bca",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TransactionRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "EXPENSE", // EXPENSE, INCOME, SAVINGS_ALLOCATION
    val amount: Long = 0L,
    val categoryKey: String = "",
    val categoryLabel: String = "",
    val frequencyTag: String = "HARIAN", // HARIAN, MINGGUAN, BULANAN, MENDADAK, PEMASUKAN, TABUNGAN
    val walletId: String = "",
    val walletName: String = "",
    val note: String = "",
    val transactionDateMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavingsGoal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmount: Long = 0L,
    val savedAmount: Long = 0L,
    val dailyRecommendation: Long = 0L,
    val note: String = "",
    val iconKey: String = "laptop",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class QuickCategoryItem(
    val key: String,
    val label: String,
    val subtitle: String,
    val frequencyTag: String,
    val type: String, // EXPENSE or INCOME
    val defaultNoteHint: String,
    val defaultLimitAmount: Long = 1_000_000L,
    val defaultLimitPeriod: String = "BULANAN" // BULANAN, MINGGUAN, HARIAN, BEBAS
)

data class CategoryLimitConfig(
    val categoryKey: String,
    val limitAmount: Long,
    val period: String = "BULANAN" // BULANAN, MINGGUAN, HARIAN, BEBAS
)

data class EmergencyFundConfig(
    val emergencyBalance: Long = 1_000_000L,
    val emergencyLimit: Long = 500_000L,
    val limitPeriod: String = "BULANAN", // BULANAN, MINGGUAN, HARIAN, BEBAS
    val storagePlaceNote: String = "Rekening / Kantong Terpisah (Jangan dicampur uang harian)"
)

/**
 * Item Tagihan Wajib dengan Jatuh Tempo, Siklus Habis/Beli Lagi, dan Ceklist 3 Status:
 * - UNPAID: Belum Lunas (Rp 0)
 * - HALF_PAID: Baru Setengah / Dicicil Sebagian
 * - PAID: Sudah Lunas (100%)
 */
data class MandatoryBillItem(
    val id: String,
    val name: String,
    val totalAmount: Long,
    val paidAmount: Long = 0L,
    val dueScheduleLabel: String, // Cth: "Bayar setiap tanggal 1" atau "Kuota habis tgl 15 • Beli lagi tgl 16"
    val dueDayOfMonth: Int = 1,
    val renewalDayOfMonth: Int? = null, // Opsional untuk kuota/token yang ada tgl habis & tgl beli lagi
    val status: String = "UNPAID", // UNPAID, HALF_PAID, PAID
    val billType: String = "KOSAN" // KOSAN, KUOTA, LISTRIK, WIFI, CICILAN, LAINNYA
) {
    val remainingAmount: Long
        get() = (totalAmount - paidAmount).coerceAtLeast(0L)

    val progressFraction: Float
        get() = if (totalAmount > 0L) (paidAmount.toFloat() / totalAmount.toFloat()).coerceIn(0f, 1f) else 0f
}

/**
 * Pintasan item pengeluaran kehidupan sehari-hari yang tidak punya jam/tanggal pasti
 * (seperti beli bensin, beli rokok, makan warteg, kopi, skincare, dll).
 */
data class DailyRealLifeQuickItem(
    val label: String,
    val categoryKey: String,
    val suggestedAmount: Long = 0L,
    val genderFilter: String = "ALL" // ALL, LAKI_LAKI, PEREMPUAN
)

/**
 * Profil Keuangan Pengguna yang diisi sebelum masuk ke Dashboard
 * agar seluruh limit, tagihan wajib, dan pintasan harian langsung siap pakai.
 */
data class UserFinancialProfile(
    val isCompleted: Boolean = false,
    val nickname: String = "",
    val gender: String = "LAKI_LAKI", // LAKI_LAKI, PEREMPUAN
    val livingStatus: String = "NGEKOS", // NGEKOS, RUMAH_ORTU, KELUARGA
    val incomeSourcePrimary: String = "Gaji Bulanan", // Gaji Bulanan, Ngojek / Harian, Usaha / Dagang, Freelance
    val monthlyIncomeAmount: Long = 4_500_000L,
    val paydayDate: Int = 25, // Tanggal gajian / cair uang (1-31)
    val dependentsCount: Int = 0, // Tanggungan (keluarga/ortu/anak)
    val transportMode: String = "MOTOR", // MOTOR (Bensin), MOBIL, TRANSPORT_UMUM, JALAN_KAKI
    val monthlyDebtObligation: Long = 0L, // Tanggungan cicilan/hutang bulanan jika ada
    val primaryFinancialGoal: String = "BEBAS_BONCOS", // BEBAS_BONCOS, BISA_MENABUNG, LUNASI_CICILAN, DANA_DARURAT
    val dailyHabits: List<String> = listOf("Bensin", "Makan Harian", "Rokok / Kopi", "Kuota Internet"),
    val controlMode: String = "FLEKSIBEL", // FLEKSIBEL (Bebas jam/hari yang penting plafon aman), MINGGUAN, KETAT
    // Rutinitas Jadwal Harian & Pengingat Anti-Bentrok
    val workShiftType: String = "KANTOR_NORMAL", // KANTOR_NORMAL, SHIFT_PAGI, SHIFT_SORE, FLEKSIBEL, WIRAUSAHA
    val workStartHour: Int = 8,
    val workStartMinute: Int = 30,
    val lunchBreakHour: Int = 12,
    val lunchBreakMinute: Int = 30,
    val workEndHour: Int = 17,
    val workEndMinute: Int = 30,
    val eveningReviewHour: Int = 21,
    val eveningReviewMinute: Int = 0,
    val activeDaysType: String = "SENIN_JUMAT", // SENIN_JUMAT, SENIN_SABTU, SETIAP_HARI
    val isPostActivityReminderEnabled: Boolean = true, // Pengingat sehabis mencatat aktivitas transaksi
    val isSmartScheduledReminderEnabled: Boolean = true // Pengingat sehabis aktivitas harian anti-bentrok
)

data class RoutineScheduleOption(
    val key: String,
    val label: String,
    val timeRangeLabel: String,
    val startHour: Int,
    val startMinute: Int,
    val lunchHour: Int,
    val lunchMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val eveningHour: Int,
    val eveningMinute: Int
)

object PresetCategories {
    val defaultRoutineSchedules = listOf(
        RoutineScheduleOption("KANTOR_NORMAL", "🏢 Jam Kantor / Mahasiswa", "08:30 – 17:00", 8, 30, 12, 30, 17, 30, 21, 0),
        RoutineScheduleOption("SHIFT_PAGI", "🌅 Shift Pagi / Dinas Pagi", "06:30 – 14:30", 6, 30, 11, 30, 14, 30, 20, 30),
        RoutineScheduleOption("SHIFT_SORE", "🌇 Shift Siang / Mall / Resto", "14:00 – 22:00", 14, 0, 18, 0, 22, 0, 23, 0),
        RoutineScheduleOption("FLEKSIBEL", "💻 Freelance / Jam Bebas / WFH", "10:00 – 18:00", 10, 0, 13, 0, 18, 0, 21, 30),
        RoutineScheduleOption("WIRAUSAHA", "🏪 Wirausaha / Toko / Dagang", "09:00 – 21:00", 9, 0, 13, 0, 21, 0, 22, 0)
    )

    // Kategori Khusus Pengeluaran Darurat
    val emergencyExpenseCategory = QuickCategoryItem(
        key = "pakai_darurat",
        label = "Darurat",
        subtitle = "Potong Saldo Darurat",
        frequencyTag = "DARURAT",
        type = "EXPENSE",
        defaultNoteHint = "Keperluan darurat mendesak (ban bocor, obat, dll)...",
        defaultLimitAmount = 500_000L,
        defaultLimitPeriod = "BULANAN"
    )

    // 6 Pos Pengeluaran Utama Kehidupan Sehari-hari
    val expenseGrid3x3 = listOf(
        QuickCategoryItem(
            key = "tagihan_bulanan",
            label = "Tagihan Wajib",
            subtitle = "Kosan, Kuota, Listrik",
            frequencyTag = "CEKLIST",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Bayar kosan, paket kuota, token listrik, WiFi...",
            defaultLimitAmount = 1_350_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "kebutuhan_pokok",
            label = "Bensin & Harian",
            subtitle = "Makan, Bensin, Rokok, Air",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Isi bensin, beli rokok, makan siang, air galon...",
            defaultLimitAmount = 1_800_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "rawat_diri",
            label = "Rawat Diri",
            subtitle = "Sabun, Barber, Skincare",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Sabun, sampo, potong rambut, skincare, laundry...",
            defaultLimitAmount = 400_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "gaya_hidup",
            label = "Jajan & Ngopi",
            subtitle = "Kopi, Cemilan, Nongkrong",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kopi, gorengan, seblak, nongkrong, jajan...",
            defaultLimitAmount = 600_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "sosial_darurat",
            label = "Sosial & Ortu",
            subtitle = "Ortu, Kondangan, Patungan",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kasih ortu, kondangan, patungan temen...",
            defaultLimitAmount = 400_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "modal_produktif",
            label = "Servis & Kerja",
            subtitle = "Oli, Servis, Modal Kerja",
            frequencyTag = "PRODUKTIF",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Ganti oli motor, servis, alat kerja, modal usaha...",
            defaultLimitAmount = 500_000L,
            defaultLimitPeriod = "BULANAN"
        )
    )

    val defaultCategoryLimits: Map<String, CategoryLimitConfig> = expenseGrid3x3.associate { item ->
        item.key to CategoryLimitConfig(
            categoryKey = item.key,
            limitAmount = item.defaultLimitAmount,
            period = item.defaultLimitPeriod
        )
    }

    val defaultMandatoryBills = listOf(
        MandatoryBillItem(
            id = "bill_kosan",
            name = "Bayar Kosan / Kontrakan",
            totalAmount = 850_000L,
            paidAmount = 0L,
            dueScheduleLabel = "Bayar setiap tanggal 1",
            dueDayOfMonth = 1,
            renewalDayOfMonth = null,
            status = "UNPAID",
            billType = "KOSAN"
        ),
        MandatoryBillItem(
            id = "bill_kuota",
            name = "Paket Kuota Internet",
            totalAmount = 100_000L,
            paidAmount = 0L,
            dueScheduleLabel = "Habis tgl 15 • Beli lagi tgl 16",
            dueDayOfMonth = 15,
            renewalDayOfMonth = 16,
            status = "UNPAID",
            billType = "KUOTA"
        ),
        MandatoryBillItem(
            id = "bill_listrik",
            name = "Token Listrik & Air",
            totalAmount = 200_000L,
            paidAmount = 100_000L,
            dueScheduleLabel = "Isi rutin setiap tanggal 5 & 20",
            dueDayOfMonth = 20,
            renewalDayOfMonth = null,
            status = "HALF_PAID",
            billType = "LISTRIK"
        ),
        MandatoryBillItem(
            id = "bill_wifi",
            name = "WiFi / Langganan Bulanan",
            totalAmount = 200_000L,
            paidAmount = 200_000L,
            dueScheduleLabel = "Jatuh tempo setiap tanggal 10",
            dueDayOfMonth = 10,
            renewalDayOfMonth = null,
            status = "PAID",
            billType = "WIFI"
        )
    )

    val realLifeQuickItems = listOf(
        // Kebutuhan Pokok / Bensin & Harian (Tidak ada jam/tanggal pasti)
        DailyRealLifeQuickItem("⛽ Isi Bensin", "kebutuhan_pokok", 25_000L, "ALL"),
        DailyRealLifeQuickItem("🚬 Beli Rokok", "kebutuhan_pokok", 32_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("🍛 Makan Harian", "kebutuhan_pokok", 20_000L, "ALL"),
        DailyRealLifeQuickItem("🛒 Belanja Dapur/Warung", "kebutuhan_pokok", 35_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("💧 Galon / Gas", "kebutuhan_pokok", 22_000L, "ALL"),
        DailyRealLifeQuickItem("🛵 Ongkos Ojol/Parkir", "kebutuhan_pokok", 15_000L, "ALL"),

        // Gaya Hidup / Jajan & Ngopi
        DailyRealLifeQuickItem("☕ Beli Kopi / Es", "gaya_hidup", 18_000L, "ALL"),
        DailyRealLifeQuickItem("🍜 Jajan / Seblak / Bakso", "gaya_hidup", 20_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("🚬 Rokok / Vape Tongkrongan", "gaya_hidup", 30_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("🍢 Cemilan / Gorengan", "gaya_hidup", 10_000L, "ALL"),
        DailyRealLifeQuickItem("🎮 Nongkrong / Hiburan", "gaya_hidup", 50_000L, "ALL"),

        // Rawat Diri
        DailyRealLifeQuickItem("💈 Potong Rambut / Barber", "rawat_diri", 35_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("✨ Skincare / Makeup", "rawat_diri", 75_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("🧴 Sabun / Sampo / Odol", "rawat_diri", 25_000L, "ALL"),
        DailyRealLifeQuickItem("👕 Laundry Pakaian", "rawat_diri", 20_000L, "ALL"),

        // Sosial & Ortu
        DailyRealLifeQuickItem("🏡 Kasih Orang Tua", "sosial_darurat", 200_000L, "ALL"),
        DailyRealLifeQuickItem("✉️ Amplop Kondangan", "sosial_darurat", 100_000L, "ALL"),
        DailyRealLifeQuickItem("🤝 Traktir / Patungan", "sosial_darurat", 30_000L, "ALL"),

        // Servis & Kerja
        DailyRealLifeQuickItem("🛢️ Ganti Oli Motor", "modal_produktif", 55_000L, "ALL"),
        DailyRealLifeQuickItem("🔧 Servis Kendaraan", "modal_produktif", 100_000L, "ALL"),
        DailyRealLifeQuickItem("📦 Belanja Modal / Kerja", "modal_produktif", 150_000L, "ALL")
    )

    // Minimalist Income Pills
    val incomeQuickPills = listOf(
        QuickCategoryItem(
            key = "gaji_bulanan",
            label = "Gaji Utama",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Gaji bulanan / bonus kantor..."
        ),
        QuickCategoryItem(
            key = "ngojek_online",
            label = "Ngojek / Harian",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Pendapatan harian / ojol / tip..."
        ),
        QuickCategoryItem(
            key = "hasil_usaha",
            label = "Untung Usaha",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Penjualan usaha hari ini..."
        ),
        QuickCategoryItem(
            key = "rejeki_lain",
            label = "Transfer / Bonus",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Cashback, kiriman, bonus..."
        )
    )

    val defaultStarterWallets = listOf(
        Wallet(
            id = "wallet_bca",
            name = "Bank BCA",
            walletType = "BANK",
            balance = 3_200_000L,
            colorHex = "#0066AE",
            iconKey = "bank_bca"
        ),
        Wallet(
            id = "wallet_mas",
            name = "Bank Mas",
            walletType = "BANK",
            balance = 1_500_000L,
            colorHex = "#F59E0B",
            iconKey = "bank_mas"
        ),
        Wallet(
            id = "wallet_dana",
            name = "DANA",
            walletType = "EWALLET",
            balance = 350_000L,
            colorHex = "#118EEA",
            iconKey = "ewallet_dana"
        ),
        Wallet(
            id = "wallet_shopeepay",
            name = "ShopeePay",
            walletType = "EWALLET",
            balance = 250_000L,
            colorHex = "#EE4D2D",
            iconKey = "ewallet_shopee"
        ),
        Wallet(
            id = "wallet_gopay",
            name = "GoPay",
            walletType = "EWALLET",
            balance = 400_000L,
            colorHex = "#00AED6",
            iconKey = "ewallet_gopay"
        )
    )

    val availableBankAndWalletPresets = listOf(
        Wallet(
            id = "wallet_bca",
            name = "Bank BCA",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#0066AE",
            iconKey = "bank_bca"
        ),
        Wallet(
            id = "wallet_bri",
            name = "Bank BRI",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#00529C",
            iconKey = "bank_bri"
        ),
        Wallet(
            id = "wallet_mandiri",
            name = "Bank Mandiri",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#003D79",
            iconKey = "bank_mandiri"
        ),
        Wallet(
            id = "wallet_bni",
            name = "Bank BNI",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#F15A23",
            iconKey = "bank_bni"
        ),
        Wallet(
            id = "wallet_seabank",
            name = "SeaBank",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#FF6600",
            iconKey = "bank_seabank"
        ),
        Wallet(
            id = "wallet_jago",
            name = "Bank Jago",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#F59E0B",
            iconKey = "bank_jago"
        ),
        Wallet(
            id = "wallet_bsi",
            name = "Bank BSI",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#00A39D",
            iconKey = "bank_bsi"
        ),
        Wallet(
            id = "wallet_mas",
            name = "Bank Mas",
            walletType = "BANK",
            balance = 0L,
            colorHex = "#F59E0B",
            iconKey = "bank_mas"
        ),
        Wallet(
            id = "wallet_dana",
            name = "DANA",
            walletType = "EWALLET",
            balance = 0L,
            colorHex = "#118EEA",
            iconKey = "ewallet_dana"
        ),
        Wallet(
            id = "wallet_gopay",
            name = "GoPay",
            walletType = "EWALLET",
            balance = 0L,
            colorHex = "#00AED6",
            iconKey = "ewallet_gopay"
        ),
        Wallet(
            id = "wallet_shopeepay",
            name = "ShopeePay",
            walletType = "EWALLET",
            balance = 0L,
            colorHex = "#EE4D2D",
            iconKey = "ewallet_shopee"
        ),
        Wallet(
            id = "wallet_ovo",
            name = "OVO",
            walletType = "EWALLET",
            balance = 0L,
            colorHex = "#4C3494",
            iconKey = "ewallet_ovo"
        ),
        Wallet(
            id = "wallet_tunai",
            name = "Uang Tunai (Cash)",
            walletType = "CASH",
            balance = 0L,
            colorHex = "#10B981",
            iconKey = "cash_wallet"
        )
    )
}
