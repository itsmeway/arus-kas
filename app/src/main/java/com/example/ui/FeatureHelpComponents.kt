package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue

/**
 * Model Penjelasan Panduan Fitur Profesional dengan Petunjuk Tata Letak Visual
 */
data class FeatureHelpContent(
    val title: String,
    val categoryLabel: String,
    val icon: ImageVector = Icons.Default.Lightbulb,
    val layoutLocation: String, // Menunjuk ke posisi / letak fitur pada tampilan (cth: "Bagian Paling Atas Layar", "Tepat di Atas Kotak Input", dll.)
    val summary: String,
    val howToUseStepByStep: List<String> = emptyList(), // Langkah demi langkah cara pakai fitur
    val keyPoints: List<Pair<String, String>>,
    val proTip: String? = null,
    val accentColor: Color = SkyBlue
)

/**
 * Simbol kecil (Icon Button) tanda tanya (?) / panduan yang estetik dan intuitif.
 * Saat diklik, langsung memunculkan kartu awan melayang (Floating Cloud Tooltip / Popup)
 * tanpa perlu berpindah halaman, sehingga pengguna langsung paham fungsi dan cara pakai fitur.
 */
@Composable
fun FeatureHelpButton(
    content: FeatureHelpContent,
    modifier: Modifier = Modifier,
    iconSize: Int = 16,
    tint: Color = SkyBlue,
    testTag: String = "feature_help_button"
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            onClick = { isExpanded = !isExpanded },
            shape = CircleShape,
            color = tint.copy(alpha = 0.14f),
            border = BorderStroke(0.8.dp, tint.copy(alpha = 0.45f)),
            modifier = Modifier
                .size((iconSize + 8).dp)
                .testTag(testTag)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Panduan Fitur ${content.title}",
                    tint = tint,
                    modifier = Modifier.size(iconSize.dp)
                )
            }
        }

        if (isExpanded) {
            FloatingFeatureCloudPopup(
                content = content,
                onDismiss = { isExpanded = false }
            )
        }
    }
}

/**
 * Kartu awan melayang (Floating Cloud Dialog/Popup)
 * Tampil melayang di atas halaman dengan backdrop transparan halus, efek bayangan awan (cloud shadow),
 * serta penjelasan fitur yang profesional, padat, dan mudah dipahami.
 */
@Composable
fun FloatingFeatureCloudPopup(
    content: FeatureHelpContent,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Scrim semi-transparan biru lembut agar pengguna tetap melihat halaman di belakangnya
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF041C38).copy(alpha = 0.32f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Kontainer Awan Melayang Glassmorphism (Cloud Card)
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .widthIn(max = 390.dp)
                    .glassmorphicCardEffect(
                        cornerRadius = 24.dp,
                        accentTint = content.accentColor
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Menahan klik agar tidak dismiss jika klik isi popup */ }
                    )
                    .testTag("floating_cloud_popup_${content.title.replace(" ", "_").lowercase()}"),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                border = glassmorphicBorderStroke(accentColor = content.accentColor, width = 1.3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Baris Awan: Kategori Badge + Judul + Tombol Tutup Silang
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = content.accentColor.copy(alpha = 0.16f),
                                border = BorderStroke(0.8.dp, content.accentColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = content.icon,
                                        contentDescription = null,
                                        tint = content.accentColor,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = content.categoryLabel.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = content.accentColor,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("close_floating_cloud_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Panduan",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Judul Utama Fitur
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Garis Gradien Indikator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(content.accentColor, content.accentColor.copy(alpha = 0.2f))
                                )
                            )
                    )

                    // 1. PETUNJUK TATA LETAK FITUR (Visual Location Pointer)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = content.accentColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, content.accentColor.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📍",
                                fontSize = 13.sp
                            )
                            Column {
                                Text(
                                    text = "POSISI TATA LETAK FITUR INI:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = content.accentColor
                                )
                                Text(
                                    text = content.layoutLocation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.2.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Ringkasan Pengertian Utama
                    Text(
                        text = content.summary,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 2. PETUNJUK CARA PAKAI LANGKAH DEMI LANGKAH (Step-by-Step Usage Guide)
                    if (content.howToUseStepByStep.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "👉",
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "CARA PAKAI LANGKAH DEMI LANGKAH:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = content.accentColor
                                )
                            }
                            content.howToUseStepByStep.forEachIndexed { index, stepDesc ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = content.accentColor.copy(alpha = 0.25f),
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = content.accentColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = stepDesc,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.8.sp, lineHeight = 14.5.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Poin-poin Kunci Cara Kerja Fitur
                    if (content.keyPoints.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            content.keyPoints.forEach { (label, desc) ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        color = content.accentColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 14.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Kotak Tips Pro / Best Practice Penggunaan
                    if (!content.proTip.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldAccent.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "💡",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = content.proTip,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.2.sp, lineHeight = 13.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Tombol 'Saya Paham' di bagian bawah
                    Surface(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        color = content.accentColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, content.accentColor.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_dismiss_floating_cloud")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Saya Paham ✓",
                                style = MaterialTheme.typography.labelMedium,
                                color = content.accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kamus Panduan & Pengertian Profesional untuk seluruh fitur ArusKas
 */
object FeatureHelpDatabase {

    val TOTAL_SALDO = FeatureHelpContent(
        title = "Total Saldo & Multi-Dompet",
        categoryLabel = "Keuangan Inti",
        layoutLocation = "Kotak besar paling atas layar (di bawah logo & menu bar)",
        summary = "Pusat informasi total seluruh uang kas dan tabungan Anda. Menggabungkan saldo rekening bank, dompet digital (e-wallet), dan uang tunai tanpa perlu membuka aplikasi perbankan satu per satu.",
        howToUseStepByStep = listOf(
            "Lihat nominal angka besar untuk mengetahui total uang kas siap pakai saat ini.",
            "Sentuh/klik kotak saldo ini untuk membuka lembar rincian dompet (BCA, Bank Mas, DANA, ShopeePay, GoPay, Tunai).",
            "Di dalam lembar rincian, Anda bisa menambah dompet baru, mengedit saldo, atau mencatat mutasi antar dompet.",
            "Klik tombol 'Grafik' di sisi kanan kotak saldo untuk membuka grafik perbandingan pengeluaran."
        ),
        keyPoints = listOf(
            "Saldo Utama" to "Menunjukkan uang kas siap pakai yang berada di dompet aktif.",
            "Rincian Multi-Dompet" to "Sentuh kotak saldo untuk melihat rincian isi saldo BCA, Bank Mas, DANA, ShopeePay, GoPay, atau menambah dompet baru.",
            "Simpanan Terpisah" to "Total mencakup simpanan Pos Darurat dan Tabungan Target agar Anda selalu melihat gambaran kekayaan riil Anda."
        ),
        proTip = "Klik pada angka saldo kapan saja untuk menambah, mengedit, atau mentransfer uang antar dompet.",
        accentColor = EmeraldPrimary
    )

    val POS_DARURAT = FeatureHelpContent(
        title = "Pos Pengeluaran Darurat",
        categoryLabel = "Proteksi Finansial",
        layoutLocation = "Garis baris kecil berwarna emas tepat di bawah angka Total Saldo",
        summary = "Rekening atau kantong simpanan terpisah khusus untuk kejadian tak terduga (ban bocor, sakit mendadak, servis mendesak) agar uang belanja harian Anda tidak terganggu.",
        howToUseStepByStep = listOf(
            "Lihat rasio nominal darurat: Angka sebelah kiri adalah pemakaian bulan ini, sebelah kanan adalah plafon batas limit darurat.",
            "Ketuk baris '+ Darurat' ini untuk menyetor simpanan darurat atau mengubah batas limit darurat bulanan Anda.",
            "Saat terjadi musibah mendadak, pada kotak input di bawah pilih pos 'Darurat' agar pemotongan uang diambil dari simpanan darurat terpisah ini."
        ),
        keyPoints = listOf(
            "Limit Darurat" to "Batas maksimal uang yang diizinkan untuk ditarik saat terjadi musibah dalam periode tertentu.",
            "Rekening Terpisah" to "Sangat disarankan disimpan di rekening bank terpisah yang tidak memiliki kartu debit untuk belanja.",
            "Disiplin Alokasi" to "Setiap penarikan darurat akan langsung memotong saldo darurat dan tercatat transparan."
        ),
        proTip = "Usahakan memiliki cadangan darurat minimal 1 sampai 3 bulan biaya hidup untuk rasa aman.",
        accentColor = GoldAccent
    )

    val GRAFIK_EXPENSE = FeatureHelpContent(
        title = "Grafik Arus Pengeluaran",
        categoryLabel = "Analisis Keuangan",
        layoutLocation = "Terbuka di bawah kotak Total Saldo saat tombol 'Grafik' di kanan atas diketuk",
        summary = "Visualisasi perbandingan laju uang keluar dan uang masuk secara harian, mingguan, maupun bulanan untuk melihat tren pengeluaran Anda.",
        howToUseStepByStep = listOf(
            "Ketuk tombol 'Grafik' di sebelah kanan kotak Total Saldo untuk membuka kartu grafik melayang.",
            "Pilih tab mode analisis: 'Harian' (7 hari terakhir), 'Bulanan' (riwayat per bulan), atau 'Adu Cuan' (rasio masuk vs keluar).",
            "Perhatikan diagram batang untuk mendeteksi lonjakan pengeluaran di hari tertentu.",
            "Ketuk tanda silang (✕) di sudut kanan atas kartu untuk menutup grafik kembali."
        ),
        keyPoints = listOf(
            "Mode Harian" to "Melihat hari-hari mana saja yang paling boros dalam seminggu terakhir.",
            "Mode Bulanan" to "Memantau kestabilan arus kas per bulan apakah mengalami surplus atau defisit.",
            "Adu Cuan" to "Membandingkan total pemasukan terhadap total pengeluaran secara langsung."
        ),
        proTip = "Periksa grafik seminggu sekali untuk mengetahui kapan harus menahan pengeluaran.",
        accentColor = GoldAccent
    )

    val TABUNGAN_TARGET = FeatureHelpContent(
        title = "Tabungan Target (Radar Impian)",
        categoryLabel = "Rencana Masa Depan",
        layoutLocation = "Tombol lingkaran celengan di sebelah kanan strip metrik 'Hari Ini & Bulan Ini'",
        summary = "Fitur untuk mengunci dan menyisihkan dana demi mewujudkan impian (laptop, liburan, motor, modal nikah, gadget) dengan target nominal yang jelas.",
        howToUseStepByStep = listOf(
            "Ketuk tombol lingkaran celengan untuk membuka panel 'Radar Impian'.",
            "Pilih target impian Anda (misal: Beli Laptop atau Liburan) atau ketuk '+ Buat Target' untuk menambah target baru.",
            "Pilih tab 'Setor Tabungan', pilih dompet sumber (cth: BCA), masukkan nominal yang ingin disisihkan, lalu ketuk 'Simpan Tabungan'.",
            "Saldo dompet utama akan berkurang dan dana otomatis terkunci aman di dalam target impian tersebut."
        ),
        keyPoints = listOf(
            "Simulasi & Setor" to "Anda bisa menyetor tabungan langsung dari dompet utama atau e-wallet kapan saja.",
            "Rekomendasi Harian" to "Aplikasi otomatis menghitung berapa nominal aman yang perlu Anda tabung setiap hari.",
            "Radar Visual" to "Melihat persentase pencapaian impian dalam grafik lingkaran yang memuaskan."
        ),
        proTip = "Setor nominal kecil secara konsisten (misal Rp 15.000/hari) lebih efektif daripada menunggu uang sisa.",
        accentColor = GoldAccent
    )

    val INPUT_NOMINAL = FeatureHelpContent(
        title = "Pencatatan Masuk & Keluar",
        categoryLabel = "Pencatatan Cepat",
        layoutLocation = "Kotak formulir input besar di bagian tengah layar (dengan tab Pengeluaran / Pemasukan)",
        summary = "Alat input instan untuk mencatat pengeluaran atau pemasukan hanya dalam hitungan detik dengan otomatisasi titik ribuan (Rp) standar Indonesia.",
        howToUseStepByStep = listOf(
            "Pilih tombol sakelar tab: 'Pengeluaran' (merah) atau 'Pemasukan' (hijau).",
            "Pilih salah satu chip kategori di bawahnya (misal: 'Bensin & Harian' atau 'Gaji Utama').",
            "Ketik nominal uang tanpa tanda titik (contoh: ketik 50000, sistem otomatis menampilkan 'Rp 50.000').",
            "Pilih dompet yang digunakan untuk membayar (BCA, DANA, Tunai, dll).",
            "Isi catatan detail transaksi jika diperlukan (opsional), lalu ketuk tombol '+ Catat Pengeluaran' di sisi kanan."
        ),
        keyPoints = listOf(
            "Format Standar ID" to "Ketik angka tanpa tanda titik, sistem otomatis menyusun format ribuan rapi (cth: 50000 -> Rp 50.000).",
            "Pilihan Dompet" to "Pilih dompet sumber pembayaran (BCA, DANA, dll) agar saldo dompet otomatis berkurang/bertambah.",
            "Catatan Fleksibel" to "Tuliskan keterangan detail pembelian atau pilih salah satu tombol pintasan cepat."
        ),
        proTip = "Bisa beralih cepat antara tab 'Pengeluaran' dan 'Pemasukan' menggunakan tombol sakelar di atas nominal.",
        accentColor = CoralExpense
    )

    val PINTASAN_SEHARI_HARI = FeatureHelpContent(
        title = "Pintasan Sehari-hari (Bebas Kapan Aja)",
        categoryLabel = "Pengeluaran Riil",
        layoutLocation = "Baris tombol chip oval mendatar di dalam kotak input nominal (di bawah pilihan pos pengeluaran)",
        summary = "Tombol instan untuk kebutuhan harian yang tidak memiliki jam atau tanggal beli yang pasti (seperti beli bensin, beli rokok, makan warteg, ngopi, atau skincare).",
        howToUseStepByStep = listOf(
            "Saat sedang belanja atau di SPBU, sentuh salah satu tombol pintasan seperti '⛽ Bensin' atau '🚬 Rokok'.",
            "Nominal standar dan nama catatan transaksi akan langsung terisi otomatis ke dalam formulir.",
            "Anda bisa langsung menyesuaikan nominal jika berbeda (misal: bensin Rp 25.000).",
            "Perhatikan teks 'Sisa Kuota: Rp X (~Rp Y/hr)' di atas baris tombol untuk memantau rata-rata jatah belanja harian yang masih aman."
        ),
        keyPoints = listOf(
            "1-Ketuk Selesai" to "Menekan tombol seperti '⛽ Bensin' langsung mengisi nominal rata-rata dan catatan pengeluaran secara otomatis.",
            "Kuota Fleksibel Adaptif" to "Aplikasi membagi sisa kuota kategori dengan sisa hari berjalan, memberi tahu jatah aman per hari tanpa mengekang waktu belanja Anda.",
            "Bebas & Nyata" to "Beli bensin atau rokok jam berapa pun tidak masalah, yang penting total pengeluaran tetap berada di bawah plafon limit."
        ),
        proTip = "Gunakan pintasan ini saat sedang buru-buru di kasir minimarket atau SPBU agar transaksi langsung tercatat.",
        accentColor = SkyBlue
    )

    val KATEGORI_LIMIT = FeatureHelpContent(
        title = "Plafon Batas Limit Kategori",
        categoryLabel = "Rem Pengeluaran",
        layoutLocation = "Deretan 6 kotak persegi (grid 3x2) di bawah kotak masukan nominal",
        summary = "Sistem penjagaan keuangan yang membagi pengeluaran Anda ke dalam 6 pos utama dengan batas maksimal (plafon) agar tidak terjadi overbudget.",
        howToUseStepByStep = listOf(
            "Perhatikan setiap kotak: Garis biru atas adalah 'Bar Batas Limit' (100% plafon), sedangkan garis bawah adalah 'Bar Terpakai'.",
            "Warna bar terpakai otomatis menunjukkan status: Hijau (Aman), Kuning (Awas > 75%), Merah (Overbudget).",
            "Ketuk kotak kategori untuk menjadikannya kategori aktif saat ingin mencatat transaksi.",
            "Ketuk ikon gerigi/setelan (Tune) di pojok kanan atas tiap kotak untuk mengubah batas nominal limit menjadi Bulanan, Mingguan, Harian, atau Bebas."
        ),
        keyPoints = listOf(
            "Bar Batas Limit" to "Menampilkan kapasitas plafon maksimal (100%) yang Anda tetapkan untuk pos tersebut.",
            "Bar Terpakai" to "Menunjukkan berapa banyak uang yang telah dihabiskan beserta persentase dan sisa kuota yang masih aman.",
            "Fleksibilitas Penuh" to "Klik ikon setelan (gerigi) pada tiap kotak untuk mengubah target limit menjadi Bulanan, Mingguan, Harian, atau Bebas."
        ),
        proTip = "Warna bar akan berubah hijau (Aman), kuning (Awas > 75%), hingga merah (Overbudget) sebagai pengingat visual.",
        accentColor = EmeraldPrimary
    )

    val TAGIHAN_WAJIB = FeatureHelpContent(
        title = "Ceklist Tagihan Wajib & Siklus Jatuh Tempo",
        categoryLabel = "Kewajiban Bulanan",
        layoutLocation = "Kotak kartu berbingkai biru di bawah 6 kotak kategori (berjudul 'CEKLIST TAGIHAN WAJIB')",
        summary = "Daftar pengingat pembayaran wajib yang harus diselesaikan setiap bulan (seperti bayar kosan, paket kuota internet, token listrik, WiFi, cicilan).",
        howToUseStepByStep = listOf(
            "Lihat nama tagihan dan tanggal jatuh tempo yang tertera (misal: 'Bayar setiap tanggal 1' atau 'Habis tgl 15 • Beli lagi tgl 16').",
            "Saat membayar tagihan, pilih salah satu tombol status di bawah tagihan: 'Belum Lunas', 'Baru Setengah (50%)', atau 'Sudah Lunas ✅'.",
            "Memilih status Lunas atau Setengah otomatis memotong saldo dompet aktif dan mencatat transaksi pengeluaran secara otomatis.",
            "Gunakan tombol '+ Tagihan' di pojok kanan atas kartu untuk menambah tagihan baru, atau ikon pensil di tiap baris untuk mengedit nominal/tanggal."
        ),
        keyPoints = listOf(
            "Jatuh Tempo & Siklus" to "Mendukung tanggal tetap (cth: 'Bayar kosan setiap tanggal 1') maupun siklus pemakaian (cth: 'Kuota habis tgl 15 • Beli lagi tgl 16').",
            "Ceklist 3 Status" to "Tandai langsung: 'Belum Lunas', 'Baru Setengah (50%)', atau 'Sudah Lunas ✅'. Status Lunas akan otomatis memotong saldo dompet.",
            "Bar Terbayar vs Batas" to "Memantau berapa total tagihan wajib yang sudah aman terbayar dibanding total kewajiban bulan ini."
        ),
        proTip = "Saat menerima gaji, utamakan menyelesaikan ceklist tagihan wajib terlebih dahulu agar sisa uang murni aman dibelanjakan.",
        accentColor = SkyBlue
    )

    val MONITOR_UANG_MASUK = FeatureHelpContent(
        title = "Monitor Aliran Uang Masuk",
        categoryLabel = "Evaluasi Pemasukan",
        layoutLocation = "Kotak persegi panjang hijau di bagian bawah (di atas daftar riwayat transaksi)",
        summary = "Panel pelacak seluruh pemasukan yang masuk ke kantong Anda dalam bulan berjalan (gaji bulanan, sampingan ojol, hasil jualan, transfer).",
        howToUseStepByStep = listOf(
            "Lihat angka uang masuk hari ini dan akumulasi total pemasukan bulan berjalan.",
            "Perhatikan indikator warna garis: Garis hijau menandakan arus kas surplus (pemasukan lebih besar dari pengeluaran).",
            "Sentuh kartu ini untuk membuka riwayat uang masuk lengkap per sumber pemasukan dan per dompet penerima.",
            "Gunakan informasi ini di akhir bulan untuk mengevaluasi apakah ada sisa uang yang bisa disetor ke Tabungan Impian."
        ),
        keyPoints = listOf(
            "Kalkulasi Surplus/Defisit" to "Membandingkan pemasukan bulan ini dengan total pengeluaran untuk memastikan keuangan tetap surplus.",
            "Riwayat Masuk" to "Klik kotak ini untuk melihat rincian kapan saja uang masuk dan rekening mana yang menerimanya."
        ),
        proTip = "Usahakan rasio uang masuk selalu lebih besar daripada pengeluaran bulanan agar sisa uang bisa dialokasikan ke tabungan.",
        accentColor = EmeraldPrimary
    )

    val PENGINGAT_AKTIVITAS = FeatureHelpContent(
        title = "Pengingat Sehabis Aktivitas (Anti-Bentrok)",
        categoryLabel = "Pengingat Pintar",
        layoutLocation = "Kartu berbingkai oranye-emas di atas 6 pos pengeluaran (berjudul 'PENGINGAT HABIS AKTIVITAS')",
        summary = "Sistem pengingat cerdas yang mengingatkan Anda untuk memeriksa catatan keuangan sehabis melakukan aktivitas belanja, sehabis makan siang, dan waktu santai malam, dengan proteksi otomatis agar tidak bentrok saat jam sibuk kerja.",
        howToUseStepByStep = listOf(
            "Isi jadwal rutinitas harian Anda (jam kerja, jam istirahat siang, jam pulang, dan hari kerja) melalui kuesioner awal atau tombol 'Atur Jam' di kartu ini.",
            "Saat Anda baru saja mencatat pengeluaran (bensin, makan, jajan), aplikasi akan memunculkan banner pengingat halus dan notifikasi Android untuk mengecek sisa limit kategori.",
            "Setiap sehabis jam istirahat makan siang (12:30), aplikasi mengirim pengingat lembut untuk memastikan makan siang Anda tercatat.",
            "Saat jam pulang kerja (17:30) dan santai malam (21:00), pengingat muncul untuk rekap harian sebelum tidur.",
            "Gunakan tombol 'Uji Notifikasi' pada kartu untuk menguji langsung notifikasi pengingat muncul di HP Anda."
        ),
        keyPoints = listOf(
            "Proteksi Jam Sibuk (Anti-Bentrok)" to "Notifikasi otomatis DIBUNGKAM saat Anda berada di jam sibuk kerja/kuliah agar tidak mengganggu fokus.",
            "Pengingat Sehabis Aktivitas" to "Mengingatkan langsung setelah transaksi disimpan untuk mengecek sisa kuota harian & pos belanja.",
            "Fleksibel & Bisa Diatur" to "Jam pengingat dapat disesuaikan dengan pola shift pagi, shift siang, kantor normal, atau jam bebas."
        ),
        proTip = "Jika Anda bekerja dengan sistem shift atau jam bebas, sesuaikan jam kerja di tombol 'Atur Jam' agar pengingat selalu datang di waktu senggang yang tepat.",
        accentColor = GoldAccent
    )
}

