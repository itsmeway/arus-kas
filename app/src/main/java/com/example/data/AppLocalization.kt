package com.example.data

/**
 * Supported UI languages in ArusKas: Indonesian and English only.
 */
enum class AppLanguage(val code: String, val displayName: String, val regionLabel: String) {
    INDONESIAN("id", "Bahasa Indonesia", "Default / Bahasa Nasional"),
    ENGLISH("en", "English", "International / Global")
}

/**
 * Currency display format preference
 */
enum class CurrencyFormatOption(val code: String, val label: String) {
    FULL_RUPIAH("full", "Rp 150.000 (Lengkap / Full)"),
    COMPACT("compact", "Rp 150 Rb / Rp 1,5 Jt (Ringkas / Compact)")
}

/**
 * Subscription & Free Trial Configuration (Rp 7.000/month with 1-month Free Trial)
 */
data class SubscriptionConfig(
    val isProActive: Boolean = false,
    val trialStartDateMillis: Long = 0L,
    val proActivatedDateMillis: Long = 0L,
    val productId: String = "aruskas_pro_monthly_7k"
) {
    val effectiveTrialStartMillis: Long
        get() = if (trialStartDateMillis <= 0L) System.currentTimeMillis() else trialStartDateMillis

    val isTrialActive: Boolean
        get() {
            if (isProActive) return false
            val start = effectiveTrialStartMillis
            val elapsed = (System.currentTimeMillis() - start).coerceAtLeast(0L)
            val thirtyDays = 30L * 24 * 60 * 60 * 1000L
            return elapsed < thirtyDays
        }

    val remainingTrialDays: Int
        get() {
            if (isProActive) return 0
            val start = effectiveTrialStartMillis
            val elapsed = (System.currentTimeMillis() - start).coerceAtLeast(0L)
            val thirtyDays = 30L * 24 * 60 * 60 * 1000L
            val remaining = (thirtyDays - elapsed).coerceAtLeast(0L)
            return ((remaining + (24 * 60 * 60 * 1000L - 1)) / (24 * 60 * 60 * 1000L)).toInt().coerceIn(0, 30)
        }

    val statusTier: String
        get() = when {
            isProActive -> "PRO_ACTIVE"
            isTrialActive -> "FREE_TRIAL"
            else -> "EXPIRED"
        }
}

/**
 * Comprehensive Application Settings
 */
data class AppSettings(
    val language: AppLanguage = AppLanguage.INDONESIAN,
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = false,
    val currencyFormat: CurrencyFormatOption = CurrencyFormatOption.FULL_RUPIAH,
    val isAppLockEnabled: Boolean = false,
    val hideNominalOnDashboard: Boolean = false,
    val dailyReminderNotification: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val soundHapticFeedback: Boolean = true,
    val autoBackupCloud: Boolean = true,
    val hasSeenUserGuide: Boolean = false,
    val subscription: SubscriptionConfig = SubscriptionConfig()
)

/**
 * Full Dictionary providing 100% translation across Indonesian and English.
 */
object AppLocalization {

    fun get(key: String, lang: AppLanguage): String {
        val dict = when (lang) {
            AppLanguage.INDONESIAN -> ID_STRINGS
            AppLanguage.ENGLISH -> EN_STRINGS
        }
        return dict[key] ?: ID_STRINGS[key] ?: key
    }

    private val ID_STRINGS = mapOf(
        // Header & Balance
        "app_title" to "ArusKas",
        "free_to_use_money" to "UANG BEBAS PAKAI (AMAN BUAT HARIAN)",
        "total_wallet_money" to "Total Seluruh Uangmu: %s (Termasuk Tabungan & Darurat)",
        "see_all_wallet_details" to "Lihat rincian semua uang di kantong",
        "btn_signin" to "Masuk",
        "btn_signout" to "Keluar",
        "btn_graphic" to "Grafik",
        "btn_settings" to "Setelan",
        "btn_close" to "Tutup",
        "btn_save" to "Simpan",
        "btn_cancel" to "Batal",
        "btn_delete" to "Hapus",

        // Emergency strip
        "emergency_limit_label" to "Limit Darurat: %s/%s • Simpanan: %s",
        "btn_add_emergency" to "+ Darurat",
        "status_safe" to "AMAN",
        "status_warning" to "AWAS",
        "status_over" to "OVER!",
        "status_free" to "BEBAS",

        // Metrics & Tabungan Target
        "label_today" to "Hari Ini",
        "label_this_month" to "Bulan Ini",
        "target_savings" to "Tabungan Target",
        "target_create_hint" to "+ Buat Impian Kamu",
        "target_detail" to "Rincian Target",
        "setor_tabungan" to "Setor Nabung",

        // Recording cockpit
        "expense_mode" to "Pengeluaran Duit",
        "income_mode" to "Uang Masuk",
        "source_cuan" to "Sumber Cuan:",
        "expense_post" to "Pos Pengeluaran:",
        "nominal_input_label_expense" to "Nominal Pengeluaran (Rp)",
        "nominal_input_label_income" to "Nominal Masuk (Rp)",
        "nominal_input_label_emergency" to "Nominal Pengeluaran Darurat (Rp)",
        "nominal_input_placeholder" to "Ketik nominal (misal: 25000)",
        "select_wallet_expense" to "Pakai Dompet:",
        "select_wallet_income" to "Simpan ke Dompet:",
        "note_input_expense" to "Beli / bayar apa nih?",
        "note_input_income" to "Catatan cuan (opsional)",
        "btn_submit_expense" to "Potong",
        "btn_submit_income" to "+ Masuk",
        "emergency_cut_notice" to "Dipotong dari Simpanan Darurat Terpisah: %s",
        "emergency_place_hint" to "Lokasi simpan: %s. Limit darurat terpantau di bawah Total Saldo.",

        // Categories (6 Categories)
        "cat_makan_label" to "Makan & Ngopi",
        "cat_makan_sub" to "Kebutuhan Pokok",
        "cat_belanja_label" to "Belanja Bulanan",
        "cat_belanja_sub" to "Dapur & Kosan",
        "cat_transport_label" to "Bensin & Ojol",
        "cat_transport_sub" to "Mobilitas",
        "cat_tagihan_label" to "Listrik & Kuota",
        "cat_tagihan_sub" to "Tagihan Rutin",
        "cat_jajan_label" to "Jajan & Hiburan",
        "cat_jajan_sub" to "Gaya Hidup",
        "cat_darurat_label" to "Sosial & Tak Terduga",
        "cat_darurat_sub" to "Kondangan/Keluarga",

        // Incomes
        "income_gaji" to "Gaji Bulanan",
        "income_freelance" to "Side Hustle/Proyek",
        "income_bisnis" to "Hasil Usaha/Jualan",
        "income_investasi" to "Dividen & Cuan",
        "income_hadiah" to "Bonus/Transferan",

        // Income monitor bottom box
        "income_monitor_title" to "MONITOR UANG MASUK (%sx Masuk)",
        "income_today_title" to "UANG MASUK • HARI INI: +%s",
        "income_month_total" to "Total Masuk Bulan Ini",
        "income_ratio_label" to "Arus Kas Bersih (Surplus)",
        "income_rule_note" to "Saran: Simpan minimal 20%% (%s) buat Tabungan Target & Darurat.",
        "btn_see_income_details" to "Lihat Sumber & Rincian",

        // Bestie / Guru Roast
        "bestie_analysis_title" to "KATA BESTIE KEUANGAN HARI INI",
        "btn_refresh_word" to "Acak Kata",

        // Settings Screen Strings
        "settings_title" to "Pengaturan Aplikasi",
        "settings_subtitle" to "Kelola bahasa, ekspor laporan PDF, privasi data & preferensi",
        "sec_language" to "Pilihan Bahasa",
        "sec_language_desc" to "Ganti seluruh teks aplikasi ke Bahasa Indonesia atau English",
        "sec_appearance" to "Tampilan & Format Uang",
        "sec_appearance_desc" to "Format nominal Rupiah & preferensi sensor saldo",
        "sec_security" to "Keamanan & Privasi Data",
        "sec_security_desc" to "Kunci aplikasi & perlindungan data keuangan",
        "sec_notifications" to "Pengingat & Notifikasi",
        "sec_notifications_desc" to "Pengingat catat kas harian & getaran respon",
        "sec_data_storage" to "Penyimpanan & Cadangkan Dokumen PDF",
        "sec_data_storage_desc" to "Cadangkan laporan kas ke PDF modern atau cloud realtime",
        "sec_about" to "Tentang ArusKas & Kreator • Iwa (Urang Bandung)",
        "sec_about_badge_bandung" to "Urang Bandung Asli 🏔️",
        "sec_about_creator_name" to "Iwa",
        "sec_about_creator_role" to "Lead Software Engineer & Creator",
        "sec_about_creator_origin" to "Bandung, Jawa Barat (Kota Kembang 🏔️)",
        "sec_about_creator_quote" to "\"Sampurasun! ArusKas ieu diwangun kalayan reueus & ikhlas ku urang Bandung. Mugi tiasa ngabantosan sakumna barudak ngora ngatur dompet sangkan hirup tenang, bebas boncos, jeung kahontal sagala impianna.\"",
        "sec_about_desc" to "ArusKas dirancang ku Iwa (urang Bandung) salaku Penyelamat Finansial modern. Filosofi utamana 'Zero-Boncos Discipline': misahkeun Uang Bebas Pakai harian, Pos Darurat Mandiri, jeung Celengan Impian. Sadaya data 100% offline & aman dina HP anjeun.",
        "sec_about_mission_title" to "Misi Utama ku Iwa (Zero-Boncos Discipline):",
        "sec_about_pillar1" to "1. Saldo Siap Pakai Murni: Gak bakal kepake buat belanja uang masa depan.",
        "sec_about_pillar2" to "2. Simpanan Darurat Terpisah: Benteng proteksi saat kondisi darurat datang.",
        "sec_about_pillar3" to "3. Celengan Impian Disiplin: Cita-cita jeung barang impian kahontal nyata.",
        "sec_about_pillar4" to "4. Kontrol Harian Fleksibel: Bensin, ngopi, roko tetep aman dikontrol tanpa ribet.",
        "sec_about_btn_story" to "Buka Profil & Cerita Kreator Iwa 🏔️",
        "sec_about_btn_salam" to "Kirim Salam ka Iwa (Urang Bandung) ☕",
        "sec_about_toast_salam" to "Hatur nuhun pisan kasadayana! Salam haneut ti Iwa ti Bandung! Mugia berkah tur lancar rezekina! ☕🏔️",
        "sec_about_dialog_title" to "Profil & Dedikasi Kreator Iwa",
        "sec_about_dialog_subtitle" to "Software Engineer ti Kota Kembang Bandung • Pengembang ArusKas",
        "sec_about_story_p1" to "Sampurasun baraya! Kenalkeun, nami abdi Iwa, pituin urang Bandung, Jawa Barat. Aplikasi ArusKas ieu diciptakeun tina kegelisahan ningali babaturan jeung barudak ngora anu sabenerna boga panghasilan, tapi unggal akhir bulan sok pusing mikiran artos anu seep teu karasa (boncos).",
        "sec_about_story_p2" to "Kusabab eta, Iwa ngarancang ArusKas kalayan prinsip realistis kahirupan sapopoe: misahkeun artos jajan harian, ngajaga pos darurat mandiri, jeung nyadiakeun ruang fleksibel keur kabutuhan poean saperti bensin, ngopi, atawa roko tanpa matak lieur.",
        "sec_about_tech_title" to "Standar Kualitas & Privasi:",
        "sec_about_tech_desc" to "• 100% Offline-First: Data transaksi henteu dikirim ka server, privasi mutlak dina HP anjeun.\n• Desain Modern: Konsep Fluid Liquid Blue Glassmorphism anu nenangkeun psikologis.\n• Native Android: Diwangun nganggo Kotlin & Jetpack Compose panggancangna.",

        // Setting Options
        "opt_lang_id" to "Bahasa Indonesia (Nasional)",
        "opt_lang_en" to "English (Global)",
        "opt_format_full" to "Format Rupiah Lengkap (Rp 150.000)",
        "opt_format_compact" to "Format Rupiah Ringkas (Rp 150 Rb / 1,5 Jt)",
        "opt_hide_balance" to "Sembunyikan Saldo di Beranda",
        "opt_hide_balance_desc" to "Sensor nominal saldo dengan '••••••' agar aman saat dilihat orang lain",
        "opt_app_lock" to "Kunci Keamanan Aplikasi",
        "opt_app_lock_desc" to "Gunakan PIN / Biometrik bawaan HP saat membuka aplikasi",
        "opt_daily_reminder" to "Pengingat Catat Harian",
        "opt_daily_reminder_desc" to "Ingatkan setiap malam jam 20:00 biar gak ada kas terlewat",
        "opt_haptic" to "Getaran Umpan Balik (Haptic)",
        "opt_haptic_desc" to "Getar halus saat tombol transaksi atau nominal dipencet",
        "opt_cloud_backup" to "Cadangkan Otomatis ke Cloud",
        "opt_cloud_backup_desc" to "Sinkronisasi realtime dengan Google Cloud Firestore",
        "opt_export_pdf" to "Cadangkan Laporan Rekap PDF (Tabel Modern)",
        "opt_export_pdf_desc" to "Cetak dokumen PDF resmi berdesain tabel Gen Z, ringkasan saldo & riwayat transaksi",
        "opt_clear_cache" to "Hapus Cache Aplikasi",
        "opt_clear_cache_desc" to "Segarkan ruang penyimpanan sementara perangkat",
        "opt_privacy_policy" to "Kebijakan Privasi & Ketentuan",
        "opt_privacy_policy_desc" to "Data 100% milik Anda dan dienkripsi aman",
        "cache_cleared_msg" to "Cache aplikasi berhasil disegarkan!",
        "pdf_generating_msg" to "Sedang menyusun dokumen PDF berdesain modern...",
        "pdf_saved_msg" to "File PDF '%s' berhasil disimpan di folder Dokumen perangkat!",
        "pdf_failed_msg" to "Gagal mencadangkan PDF: %s",

        // Starter setup
        "starter_title" to "Baru Pertama Kali Pakai ArusKas?",
        "starter_desc" to "Aktifkan dompet BCA, Bank Mas, DANA, ShopeePay, GoPay & target Laptop dalam 1 klik.",
        "starter_btn" to "Siapkan",

        // User Guide (Panduan Awal Pengguna)
        "btn_user_guide" to "Panduan",
        "btn_user_guide_full" to "Panduan Cara Kerja",
        "guide_badge" to "PANDUAN PEMULA • ARUSKAS",
        "guide_title" to "Cara Kerja ArusKas",
        "guide_subtitle" to "Biar gak bingung dan langsung jago kendalikan uangmu!",
        "guide_step_counter" to "Langkah %d dari %d",
        
        "guide_step1_badge" to "PILAR 1 • SALDO SIAP PAKAI",
        "guide_step1_title" to "Beda Saldo Bebas Belanja vs Total Uang",
        "guide_step1_desc" to "Saldo paling atas adalah Uang Siap Pakai. Ini uang yang benar-benar AMAN kamu belanjakan hari ini tanpa takut tekor. Duit tabungan & daruratmu sudah otomatis diproteksi dan dikunci di brankas terpisah.",
        "guide_step1_tip" to "💡 Kuncinya: Selama saldo siap pakai ini aman (hijau), kamu bebas jajan tanpa rasa bersalah!",
        
        "guide_step2_badge" to "PILAR 2 • CATAT KILAT 3 DETIK",
        "guide_step2_title" to "Catat Cepat dengan Garis Warna Pintar",
        "guide_step2_desc" to "Tinggal tap kategori (Makan, Jajan, atau tab Uang Masuk), ketik nominal, klik Simpan! Kotak nominal dilengkapi garis warna pintar (Hijau = Aman, Kuning = Waspada, Merah = Bahaya) serta sentilan jujur dari Bestie Keuangan.",
        "guide_step2_tip" to "💡 Kuncinya: Jangan tunda catat. Butuh cuma 3 detik sehabis bayar!",
        
        "guide_step3_badge" to "PILAR 3 • LIMIT KATEGORI & DANA DARURAT",
        "guide_step3_title" to "Pasang Rem Belanja & Amankan Darurat",
        "guide_step3_desc" to "Setiap pos pengeluaran punya batas limit (misal: Limit Jajan Rp 500rb/bulan). Kalau ada musibah atau kebutuhan mendadak, gunakan kategori 'Sosial & Tak Terduga' atau potong dari Brankas Darurat tanpa ganggu tabungan impian.",
        "guide_step3_tip" to "💡 Kuncinya: Tap kartu kategori kapan saja untuk menyesuaikan limit yang pas buat gaya hidupmu.",
        
        "guide_step4_badge" to "PILAR 4 • CELENGAN IMPIAN & PDF",
        "guide_step4_title" to "Kunci Target Impian & Pantau Grafik Cuan",
        "guide_step4_desc" to "Mau beli laptop baru atau traveling? Buat Celengan Target dan setor sisa uang harianmu. Buka tombol 'Grafik' untuk adu cuan pemasukan vs pengeluaran, atau unduh laporan tabel PDF modern di menu Pengaturan.",
        "guide_step4_tip" to "💡 Kuncinya: Duit yang masuk ke Celengan Target otomatis aman dan gak bakal kepotong belanja harian.",

        "guide_btn_skip" to "Lewati",
        "guide_btn_prev" to "Kembali",
        "guide_btn_next" to "Lanjut",
        "guide_btn_finish" to "Paham, Mulai Sekarang! 🚀",
        "guide_btn_setup_now" to "Atur Saldo Awal (BCA, DANA, dll)",
        "sec_guide_card_title" to "Panduan Cara Kerja ArusKas",
        "sec_guide_card_desc" to "Pelajari kembali 4 pilar penyelamat finansial anti-boncos",

        // Subscription & Free Trial (Rp 7.000/bln + Free Trial 30 Hari)
        "pro_badge" to "ARUSKAS PRO",
        "pro_trial_chip" to "PRO Trial • Sisa %d Hari",
        "pro_active_chip" to "PRO Aktif ⭐",
        "pro_expired_chip" to "Trial Berakhir • Aktifkan PRO",
        "paywall_title" to "ArusKas PRO • Penyelamat Finansial",
        "paywall_subtitle" to "Bebas atur seluruh uang, capai impian lebih cepat & hindari boncos tanpa batas.",
        "paywall_price_headline" to "Rp 7.000 / Bulan",
        "paywall_price_sub" to "Hanya ~Rp 230/hari • Lebih hemat dari sebutir permen!",
        "paywall_trial_highlight" to "GRATIS 30 HARI PERTAMA (Rp 0 HARI INI)",
        "paywall_feature1" to "Dompet & Rekening Tanpa Batas (BCA, DANA, GoPay, ShopeePay, Bank Mas)",
        "paywall_feature2" to "Ekspor Dokumen Laporan Tabel PDF Modern Tanpa Batas",
        "paywall_feature3" to "Bestie Keuangan AI Real-time & Analisis Cashflow Harian",
        "paywall_feature4" to "Celengan Target Impian Tanpa Batas dengan Proteksi Saldo Terkunci",
        "paywall_feature5" to "Pencadangan Realtime Cloud & Keamanan Enkripsi Google",
        "paywall_policy_transparency" to "Langganan otomatis diperpanjang Rp 7.000/bulan setelah uji coba gratis 30 hari selesai. Anda dapat membatalkan langganan kapan saja dengan mudah melalui Google Play Store tanpa biaya.",
        "btn_start_free_trial" to "Mulai Uji Coba Gratis 30 Hari (Rp 0)",
        "btn_subscribe_now" to "Langganan Sekarang (Rp 7.000/bln)",
        "btn_restore_purchase" to "Pulihkan Pembelian",
        "btn_manage_subscription" to "Kelola Langganan di Play Store",
        "msg_trial_activated" to "Selamat! Uji Coba Gratis 30 Hari ArusKas PRO Berhasil Aktif!",
        "msg_subscription_restored" to "Status langganan ArusKas PRO berhasil diverifikasi!",
        "sec_subscription_title" to "Status Langganan ArusKas PRO",
        "sec_subscription_desc" to "Rp 7.000/bulan • Uji Coba 30 Hari Gratis • Batalkan kapan saja"
    )

    private val EN_STRINGS = mapOf(
        // Header & Balance
        "app_title" to "ArusKas",
        "free_to_use_money" to "SAFE-TO-SPEND (READY FOR DAILY EXPENSES)",
        "total_wallet_money" to "Total Net Worth: %s (Including Savings & Emergency)",
        "see_all_wallet_details" to "View breakdown of all funds in wallet",
        "btn_signin" to "Sign In",
        "btn_signout" to "Sign Out",
        "btn_graphic" to "Charts",
        "btn_settings" to "Settings",
        "btn_close" to "Close",
        "btn_save" to "Save",
        "btn_cancel" to "Cancel",
        "btn_delete" to "Delete",

        // Emergency strip
        "emergency_limit_label" to "Emergency Limit: %s/%s • Stash: %s",
        "btn_add_emergency" to "+ Emergency",
        "status_safe" to "SAFE",
        "status_warning" to "WARN",
        "status_over" to "OVER!",
        "status_free" to "FREE",

        // Metrics & Tabungan Target
        "label_today" to "Today",
        "label_this_month" to "This Month",
        "target_savings" to "Target Savings",
        "target_create_hint" to "+ Create Your Dream Goal",
        "target_detail" to "Target Details",
        "setor_tabungan" to "Deposit Fund",

        // Recording cockpit
        "expense_mode" to "Record Expense",
        "income_mode" to "Cash Inflow",
        "source_cuan" to "Income Stream:",
        "expense_post" to "Expense Category:",
        "nominal_input_label_expense" to "Expense Amount (IDR)",
        "nominal_input_label_income" to "Inflow Amount (IDR)",
        "nominal_input_label_emergency" to "Emergency Amount (IDR)",
        "nominal_input_placeholder" to "Enter amount (e.g., 25000)",
        "select_wallet_expense" to "Charge From Wallet:",
        "select_wallet_income" to "Deposit To Wallet:",
        "note_input_expense" to "What was this purchase for?",
        "note_input_income" to "Income note (optional)",
        "btn_submit_expense" to "Deduct",
        "btn_submit_income" to "+ Deposit",
        "emergency_cut_notice" to "Deducted from Isolated Emergency Stash: %s",
        "emergency_place_hint" to "Stored at: %s. Emergency limit monitored below Total Balance.",

        // Categories (6 Categories)
        "cat_makan_label" to "Food & Coffee",
        "cat_makan_sub" to "Daily Meals",
        "cat_belanja_label" to "Monthly Grocery",
        "cat_belanja_sub" to "Kitchen & Home",
        "cat_transport_label" to "Fuel & Commute",
        "cat_transport_sub" to "Rides & Transit",
        "cat_tagihan_label" to "Power & Data",
        "cat_tagihan_sub" to "Bills & Utilities",
        "cat_jajan_label" to "Fun & Treats",
        "cat_jajan_sub" to "Lifestyle",
        "cat_darurat_label" to "Social & Family",
        "cat_darurat_sub" to "Unplanned Aid",

        // Incomes
        "income_gaji" to "Monthly Salary",
        "income_freelance" to "Freelance / Hustle",
        "income_bisnis" to "Business Profits",
        "income_investasi" to "Dividends & Returns",
        "income_hadiah" to "Bonus / Gifts",

        // Income monitor bottom box
        "income_monitor_title" to "INCOME INFLOW RADAR (%sx Inflows)",
        "income_today_title" to "INCOME INFLOW • TODAY: +%s",
        "income_month_total" to "Total Inflows This Month",
        "income_ratio_label" to "Net Monthly Cashflow (Surplus)",
        "income_rule_note" to "Tip: Set aside at least 20%% (%s) into Target Savings & Emergency Stash.",
        "btn_see_income_details" to "View Streams & Details",

        // Bestie / Guru Roast
        "bestie_analysis_title" to "FINANCIAL BESTIE QUOTE OF THE DAY",
        "btn_refresh_word" to "Shuffle Quote",

        // Settings Screen Strings
        "settings_title" to "App Settings",
        "settings_subtitle" to "Manage language, export modern PDF table report, security & preferences",
        "sec_language" to "Language Options",
        "sec_language_desc" to "Switch app language between Bahasa Indonesia and English",
        "sec_appearance" to "Display & Currency Format",
        "sec_appearance_desc" to "Rupiah display formatting and privacy balance masking",
        "sec_security" to "Security & Privacy",
        "sec_security_desc" to "App lock and personal financial privacy protection",
        "sec_notifications" to "Reminders & Notifications",
        "sec_notifications_desc" to "Daily 20:00 bookkeeping reminder and haptic touch feedback",
        "sec_data_storage" to "Backup & Modern PDF Table Report",
        "sec_data_storage_desc" to "Export full ledger to sleek Gen Z PDF report or realtime cloud sync",
        "sec_about" to "About ArusKas & Creator • Iwa (Bandung Pride)",
        "sec_about_badge_bandung" to "Urang Bandung Asli 🏔️",
        "sec_about_creator_name" to "Iwa",
        "sec_about_creator_role" to "Lead Software Engineer & Creator",
        "sec_about_creator_origin" to "Bandung, West Java (Paris van Java 🏔️)",
        "sec_about_creator_quote" to "\"Sampurasun! ArusKas is engineered with sincere pride by Iwa from Bandung. Built to empower young people to manage their daily money, stay calm, avoid overspending, and reach every dream.\"",
        "sec_about_desc" to "ArusKas was created by Iwa (from Bandung) as a modern Personal Financial Lifesaver. Its core philosophy is 'Zero-Boncos Discipline': strictly separating Safe-to-Spend cash, Isolated Emergency Stash, and Goal Savings. 100% offline & secure on your phone.",
        "sec_about_mission_title" to "Core Lifesaver Architecture by Iwa:",
        "sec_about_pillar1" to "1. Pure Safe-to-Spend: Shields your daily routine from eating into future savings.",
        "sec_about_pillar2" to "2. Isolated Emergency Shield: Standalone fortress for unforeseen emergencies.",
        "sec_about_pillar3" to "3. Goal Milestone Piggy Bank: Systematic discipline to realize your biggest ambitions.",
        "sec_about_pillar4" to "4. Real-world Daily Freedom: Flexible tracking for coffee, fuel, and essentials without guilt.",
        "sec_about_btn_story" to "View Iwa's Story & Journey 🏔️",
        "sec_about_btn_salam" to "Send Warm Regards to Iwa ☕",
        "sec_about_toast_salam" to "Thank you so much! Warm greetings from Iwa in Bandung! Wishing you flourishing wealth and peace! ☕🏔️",
        "sec_about_dialog_title" to "Creator Profile & Story • Iwa",
        "sec_about_dialog_subtitle" to "Software Engineer from Bandung City • Creator of ArusKas",
        "sec_about_story_p1" to "Sampurasun! I am Iwa, a software developer born and raised in Bandung, West Java. ArusKas was born from seeing friends and young professionals struggle with late-month financial fatigue and unexpected overspending.",
        "sec_about_story_p2" to "To solve this, Iwa engineered ArusKas with a realistic approach to daily life: isolating daily pocket money, safeguarding a dedicated emergency cushion, and accommodating spontaneous daily expenses like fuel, coffee, and daily essentials with zero friction.",
        "sec_about_tech_title" to "Engineering & Privacy Standards:",
        "sec_about_tech_desc" to "• 100% Offline-First: No transaction data is sent to external servers; absolute device privacy.\n• Modern Fluid Design: myBCA-grade Liquid Blue Glassmorphism for psychological calm.\n• Native Android: Built with pure Kotlin & high-performance Jetpack Compose.",

        // Setting Options
        "opt_lang_id" to "Bahasa Indonesia (National)",
        "opt_lang_en" to "English (Global)",
        "opt_format_full" to "Full Currency Format (Rp 150.000)",
        "opt_format_compact" to "Compact Currency Format (Rp 150 Rb / 1,5 Jt)",
        "opt_hide_balance" to "Hide Balance on Dashboard",
        "opt_hide_balance_desc" to "Mask balance with '••••••' to prevent onlookers from seeing your money",
        "opt_app_lock" to "App Security Lock",
        "opt_app_lock_desc" to "Use device PIN or Fingerprint biometrics upon opening",
        "opt_daily_reminder" to "Daily Log Reminder",
        "opt_daily_reminder_desc" to "Get an alert every evening at 20:00 to log daily expenses",
        "opt_haptic" to "Haptic Feedback",
        "opt_haptic_desc" to "Gentle tactile vibration when tapping buttons or recording numbers",
        "opt_cloud_backup" to "Realtime Cloud Sync",
        "opt_cloud_backup_desc" to "Instant synchronization with Google Cloud Firestore",
        "opt_export_pdf" to "Backup to PDF Document (Modern Table)",
        "opt_export_pdf_desc" to "Generate a modern Gen Z PDF report with structured tables, summary metrics & logs",
        "opt_clear_cache" to "Clear App Cache",
        "opt_clear_cache_desc" to "Free up temporary cached device storage",
        "opt_privacy_policy" to "Privacy Policy & Terms",
        "opt_privacy_policy_desc" to "100% user-owned data, encrypted and protected",
        "cache_cleared_msg" to "App cache successfully refreshed!",
        "pdf_generating_msg" to "Generating modern PDF ledger document...",
        "pdf_saved_msg" to "PDF file '%s' saved to device Documents folder!",
        "pdf_failed_msg" to "Failed to backup PDF: %s",

        // Starter setup
        "starter_title" to "First Time Using ArusKas?",
        "starter_desc" to "Setup BCA, Bank Mas, DANA, ShopeePay, GoPay & Laptop goal in 1 click.",
        "starter_btn" to "Setup Now",

        // User Guide (Initial User Guide)
        "btn_user_guide" to "Guide",
        "btn_user_guide_full" to "App Quick Guide",
        "guide_badge" to "BEGINNER GUIDE • ARUSKAS",
        "guide_title" to "How ArusKas Works",
        "guide_subtitle" to "Master your cashflow in minutes with zero guesswork!",
        "guide_step_counter" to "Step %d of %d",
        
        "guide_step1_badge" to "PILLAR 1 • SAFE-TO-SPEND",
        "guide_step1_title" to "Safe-to-Spend vs Total Net Worth",
        "guide_step1_desc" to "The top balance shows your Safe-to-Spend cash. This is the exact amount you can spend today without worrying. Your dream savings and emergency reserves are automatically protected and locked in isolated vaults.",
        "guide_step1_tip" to "💡 Key Rule: As long as Safe-to-Spend is green, spend with zero guilt!",
        
        "guide_step2_badge" to "PILLAR 2 • 3-SECOND LIGHTNING LOG",
        "guide_step2_title" to "Smart Color Lines & Instant Feedback",
        "guide_step2_desc" to "Tap a category (Meals, Treats, or switch to Inflow), enter the amount, and tap Save! The amount field features an active color line (Green = Safe, Yellow = Caution, Red = Danger) plus blunt commentary from your Financial Bestie.",
        "guide_step2_tip" to "💡 Key Rule: Never postpone logging. It takes barely 3 seconds right after checkout!",
        
        "guide_step3_badge" to "PILLAR 3 • LIMIT CAPS & EMERGENCY SHIELD",
        "guide_step3_title" to "Set Spending Caps & Guard Emergencies",
        "guide_step3_desc" to "Each expense category has a custom cap (e.g. Treats cap at Rp 500k/mo). In case of genuine emergencies, tap the Emergency category to draw from your isolated stash without sacrificing your savings goals.",
        "guide_step3_tip" to "💡 Key Rule: Tap any category card anytime to customize its budget limit.",
        
        "guide_step4_badge" to "PILLAR 4 • GOAL PIGGY BANK & PDF",
        "guide_step4_title" to "Lock In Dreams & Inspect Visual Charts",
        "guide_step4_desc" to "Saving for a laptop or dream getaway? Set up Target Savings and deposit spare cash. Tap 'Charts' to see your inflow vs outflow ratio, or export a sleek Gen Z table PDF report in Settings.",
        "guide_step4_tip" to "💡 Key Rule: Any money deposited into Target Savings is segregated and cannot be spent accidentally.",

        "guide_btn_skip" to "Skip",
        "guide_btn_prev" to "Back",
        "guide_btn_next" to "Next",
        "guide_btn_finish" to "Got It, Let's Begin! 🚀",
        "guide_btn_setup_now" to "Set Up Starter Balances (BCA, DANA, etc)",
        "sec_guide_card_title" to "How ArusKas Works (Guide)",
        "sec_guide_card_desc" to "Review the 4 financial lifesaver pillars and key rules",

        // Subscription & Free Trial (IDR 7,000/mo + 30-Day Free Trial)
        "pro_badge" to "ARUSKAS PRO",
        "pro_trial_chip" to "PRO Trial • %d Days Left",
        "pro_active_chip" to "PRO Active ⭐",
        "pro_expired_chip" to "Trial Expired • Upgrade to PRO",
        "paywall_title" to "ArusKas PRO • Financial Lifesaver",
        "paywall_subtitle" to "Master your cashflow, crush savings goals faster & eliminate money stress.",
        "paywall_price_headline" to "IDR 7,000 / Month",
        "paywall_price_sub" to "Only ~IDR 230/day • Cheaper than a piece of candy!",
        "paywall_trial_highlight" to "FIRST 30 DAYS 100% FREE (IDR 0 TODAY)",
        "paywall_feature1" to "Unlimited Wallets & Bank Accounts (BCA, DANA, GoPay, ShopeePay, Bank Mas)",
        "paywall_feature2" to "Unlimited Modern Gen Z Table PDF Ledger Exports",
        "paywall_feature3" to "Realtime AI Financial Bestie & Daily Cashflow Roasts",
        "paywall_feature4" to "Unlimited Goal Piggy Banks with Automatic Fund Locking",
        "paywall_feature5" to "Realtime Google Cloud Sync & Encrypted Security",
        "paywall_policy_transparency" to "Auto-renews at IDR 7,000/month after 30-day free trial. Cancel anytime in Google Play Store subscriptions at zero fee.",
        "btn_start_free_trial" to "Start 30-Day Free Trial (IDR 0)",
        "btn_subscribe_now" to "Subscribe Now (IDR 7,000/mo)",
        "btn_restore_purchase" to "Restore Purchases",
        "btn_manage_subscription" to "Manage Subscription in Play Store",
        "msg_trial_activated" to "Congrats! 30-day free trial of ArusKas PRO is active!",
        "msg_subscription_restored" to "ArusKas PRO subscription status verified!",
        "sec_subscription_title" to "ArusKas PRO Subscription Status",
        "sec_subscription_desc" to "IDR 7,000/month • 30-Day Free Trial • Cancel anytime"
    )
}
