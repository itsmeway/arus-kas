package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.ui.ArusKasViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern Gen Z PDF Table Report Generator for ArusKas.
 * Generates an ultra-sleek, clean, professional financial ledger PDF
 * featuring structured tables, card metrics, colored category chips,
 * and clear summaries understood effortlessly by modern users.
 */
object ModernPdfBackupGenerator {

    data class ExportResult(
        val isSuccess: Boolean,
        val fileName: String,
        val filePath: String,
        val errorMessage: String? = null
    )

    fun generateModernLedgerPdf(
        context: Context,
        wallets: List<Wallet>,
        transactions: List<TransactionRecord>,
        goals: List<SavingsGoal>,
        emergencyFund: EmergencyFundConfig,
        lang: AppLanguage = AppLanguage.INDONESIAN
    ): ExportResult {
        return try {
            val totalBalance = wallets.sumOf { it.balance }
            val totalTabungan = goals.sumOf { it.savedAmount }
            val netWorth = totalBalance + totalTabungan + emergencyFund.emergencyBalance

            val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }

            val now = Date()
            val fileStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now)
            val printDateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", if (lang == AppLanguage.INDONESIAN) Locale("in", "ID") else Locale.US).format(now)

            val fileName = "ArusKas_Cadangan_$fileStamp.pdf"

            // A4 dimensions: 595 x 842 points
            val pageWidth = 595
            val pageHeight = 842
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            // Paints
            val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
            val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)

            // 1. Dark Modern Header (Deep Emerald #0B251B)
            paintFill.color = Color.rgb(11, 37, 27)
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 115f, paintFill)

            // Header Title
            paintText.color = Color.WHITE
            paintText.textSize = 22f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("ArusKas", 32f, 44f, paintText)

            paintText.textSize = 10f
            paintText.color = Color.rgb(16, 185, 129) // Emerald #10B981
            val subtitle = if (lang == AppLanguage.INDONESIAN) {
                "LAPORAN CADANGAN KEUANGAN RESMI • GEN Z SMART LEDGER"
            } else {
                "OFFICIAL FINANCIAL BACKUP LEDGER • GEN Z SMART MONEY"
            }
            canvas.drawText(subtitle, 32f, 62f, paintText)

            // Header Meta Date
            paintText.textSize = 8.5f
            paintText.color = Color.rgb(209, 213, 219)
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val dateLabel = if (lang == AppLanguage.INDONESIAN) "Dicetak pada: $printDateStr" else "Exported on: $printDateStr"
            canvas.drawText(dateLabel, 32f, 80f, paintText)

            // Status Badge right aligned
            val badgeLabel = if (lang == AppLanguage.INDONESIAN) "SINKRONISASI AMAN" else "SECURE BACKUP"
            paintFill.color = Color.rgb(16, 185, 129)
            val badgeRect = RectF(pageWidth - 145f, 32f, pageWidth - 32f, 54f)
            canvas.drawRoundRect(badgeRect, 6f, 6f, paintFill)

            paintText.color = Color.rgb(4, 38, 25)
            paintText.textSize = 8f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(badgeLabel, pageWidth - 137f, 46f, paintText)

            // 2. Summary Modern Stat Cards (3 Columns)
            val cardY = 130f
            val cardH = 65f
            val cardGap = 12f
            val cardW = (pageWidth - 64f - (cardGap * 2)) / 3f

            // Card 1: Total Saldo Bebas
            drawMetricCard(
                canvas, 32f, cardY, cardW, cardH,
                title = if (lang == AppLanguage.INDONESIAN) "SALDO BEBAS PAKAI" else "SAFE TO SPEND",
                value = ArusKasViewModel.formatRupiah(totalBalance),
                caption = if (lang == AppLanguage.INDONESIAN) "${wallets.size} Dompet Terhubung" else "${wallets.size} Active Wallets",
                bgColor = Color.rgb(240, 253, 244),
                borderColor = Color.rgb(16, 185, 129),
                titleColor = Color.rgb(4, 120, 87),
                valueColor = Color.rgb(6, 78, 59)
            )

            // Card 2: Tabungan & Darurat
            drawMetricCard(
                canvas, 32f + cardW + cardGap, cardY, cardW, cardH,
                title = if (lang == AppLanguage.INDONESIAN) "TABUNGAN & DARURAT" else "SAVINGS & EMERGENCY",
                value = ArusKasViewModel.formatRupiah(totalTabungan + emergencyFund.emergencyBalance),
                caption = if (lang == AppLanguage.INDONESIAN) "${goals.size} Target Impian Aktif" else "${goals.size} Active Goals",
                bgColor = Color.rgb(254, 252, 232),
                borderColor = Color.rgb(234, 179, 8),
                titleColor = Color.rgb(161, 98, 7),
                valueColor = Color.rgb(113, 63, 18)
            )

            // Card 3: Total Kas Bersih
            drawMetricCard(
                canvas, 32f + (cardW + cardGap) * 2, cardY, cardW, cardH,
                title = if (lang == AppLanguage.INDONESIAN) "TOTAL SELURUH KEKAYAAN" else "TOTAL NET ASSETS",
                value = ArusKasViewModel.formatRupiah(netWorth),
                caption = if (lang == AppLanguage.INDONESIAN) "Arus Kas Bersih" else "All Liquid Stash",
                bgColor = Color.rgb(239, 246, 255),
                borderColor = Color.rgb(59, 130, 246),
                titleColor = Color.rgb(29, 78, 216),
                valueColor = Color.rgb(30, 58, 138)
            )

            // 3. Mini Inflow / Outflow Ratio Bar
            val ratioY = 212f
            paintFill.color = Color.rgb(243, 244, 246)
            canvas.drawRoundRect(RectF(32f, ratioY, pageWidth - 32f, ratioY + 28f), 6f, 6f, paintFill)

            paintText.textSize = 9.5f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paintText.color = Color.rgb(16, 185, 129)
            val inStr = if (lang == AppLanguage.INDONESIAN) "Pemasukan: +${ArusKasViewModel.formatRupiah(totalIncome)}" else "Inflows: +${ArusKasViewModel.formatRupiah(totalIncome)}"
            canvas.drawText(inStr, 44f, ratioY + 18f, paintText)

            paintText.color = Color.rgb(239, 68, 68)
            val outStr = if (lang == AppLanguage.INDONESIAN) "Pengeluaran: -${ArusKasViewModel.formatRupiah(totalExpense)}" else "Expenses: -${ArusKasViewModel.formatRupiah(totalExpense)}"
            canvas.drawText(outStr, 220f, ratioY + 18f, paintText)

            paintText.color = Color.rgb(107, 114, 128)
            val netStr = if (lang == AppLanguage.INDONESIAN) "Selisih: ${ArusKasViewModel.formatRupiah(totalIncome - totalExpense)}" else "Net: ${ArusKasViewModel.formatRupiah(totalIncome - totalExpense)}"
            canvas.drawText(netStr, 400f, ratioY + 18f, paintText)

            // 4. MODERN TABLE HEADER
            val tableTopY = 258f
            paintText.textSize = 12f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paintText.color = Color.rgb(17, 24, 39)
            val tableTitle = if (lang == AppLanguage.INDONESIAN) "RIWAYAT TRANSAKSI (TABEL MODERN)" else "TRANSACTION HISTORY (MODERN TABLE)"
            canvas.drawText(tableTitle, 32f, tableTopY, paintText)

            val colY = tableTopY + 12f
            paintFill.color = Color.rgb(31, 41, 55) // Dark Gray Modern Header
            canvas.drawRoundRect(RectF(32f, colY, pageWidth - 32f, colY + 24f), 5f, 5f, paintFill)

            paintText.color = Color.WHITE
            paintText.textSize = 8.5f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("NO", 42f, colY + 16f, paintText)
            canvas.drawText(if (lang == AppLanguage.INDONESIAN) "TANGGAL" else "DATE", 66f, colY + 16f, paintText)
            canvas.drawText(if (lang == AppLanguage.INDONESIAN) "KATEGORI & POS" else "CATEGORY", 145f, colY + 16f, paintText)
            canvas.drawText(if (lang == AppLanguage.INDONESIAN) "DOMPET" else "WALLET", 255f, colY + 16f, paintText)
            canvas.drawText(if (lang == AppLanguage.INDONESIAN) "CATATAN TRANSAKSI" else "NOTES", 330f, colY + 16f, paintText)
            canvas.drawText(if (lang == AppLanguage.INDONESIAN) "NOMINAL" else "AMOUNT", 485f, colY + 16f, paintText)

            // 5. TABLE ROWS (Clean Zebra Striping)
            var currentY = colY + 24f
            val rowH = 22f
            val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.US)
            val maxRows = 22 // Fits nicely on A4 page

            val displayList = transactions.take(maxRows)
            displayList.forEachIndexed { index, tx ->
                val isZebra = index % 2 == 1
                if (isZebra) {
                    paintFill.color = Color.rgb(249, 250, 251)
                    canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + rowH, paintFill)
                }

                // Row border bottom
                paintFill.color = Color.rgb(229, 231, 235)
                canvas.drawRect(32f, currentY + rowH - 1f, pageWidth - 32f, currentY + rowH, paintFill)

                // No
                paintText.textSize = 8f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paintText.color = Color.rgb(107, 114, 128)
                canvas.drawText("${index + 1}", 42f, currentY + 14f, paintText)

                // Date
                val dt = dateFormat.format(Date(tx.transactionDateMillis))
                paintText.color = Color.rgb(55, 65, 81)
                canvas.drawText(dt, 66f, currentY + 14f, paintText)

                // Category pill background
                val isIncome = tx.type == "INCOME"
                val pillColor = if (isIncome) Color.rgb(209, 250, 229) else Color.rgb(254, 226, 226)
                val pillTextColor = if (isIncome) Color.rgb(6, 95, 70) else Color.rgb(153, 27, 27)

                paintFill.color = pillColor
                val catW = 95f
                canvas.drawRoundRect(RectF(142f, currentY + 3.5f, 142f + catW, currentY + rowH - 3.5f), 4f, 4f, paintFill)

                paintText.color = pillTextColor
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintText.textSize = 7.5f
                val catText = tx.categoryLabel.take(16)
                canvas.drawText(catText, 147f, currentY + 14f, paintText)

                // Wallet
                paintText.color = Color.rgb(75, 85, 99)
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paintText.textSize = 8f
                canvas.drawText(tx.walletName.take(13), 255f, currentY + 14f, paintText)

                // Note
                val noteDisplay = tx.note.ifBlank { "-" }.take(26)
                canvas.drawText(noteDisplay, 330f, currentY + 14f, paintText)

                // Amount
                val amountStr = if (isIncome) "+${ArusKasViewModel.formatRupiah(tx.amount)}" else "-${ArusKasViewModel.formatRupiah(tx.amount)}"
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintText.color = if (isIncome) Color.rgb(16, 185, 129) else Color.rgb(239, 68, 68)
                canvas.drawText(amountStr, 485f, currentY + 14f, paintText)

                currentY += rowH
            }

            // If empty transactions
            if (displayList.isEmpty()) {
                paintText.textSize = 9f
                paintText.color = Color.rgb(156, 163, 175)
                val emptyMsg = if (lang == AppLanguage.INDONESIAN) "Belum ada transaksi yang tercatat." else "No transactions recorded yet."
                canvas.drawText(emptyMsg, 200f, currentY + 25f, paintText)
                currentY += 40f
            }

            // 6. Modern Footer (Signature, Watermark & Page Number)
            val footerY = pageHeight - 45f
            paintFill.color = Color.rgb(229, 231, 235)
            canvas.drawRect(32f, footerY - 10f, pageWidth - 32f, footerY - 9f, paintFill)

            paintText.textSize = 8f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paintText.color = Color.rgb(107, 114, 128)
            canvas.drawText("ArusKas Financial System • Gen Z Smart Money Assistant", 32f, footerY + 8f, paintText)

            paintText.color = Color.rgb(16, 185, 129)
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Halaman 1 / 1", pageWidth - 88f, footerY + 8f, paintText)

            pdfDoc.finishPage(page)

            // Save PDF to App-specific or External Documents directory
            val docsDir = context.getExternalFilesDir("Documents") ?: context.filesDir
            if (!docsDir.exists()) docsDir.mkdirs()

            val targetFile = File(docsDir, fileName)
            val fos = FileOutputStream(targetFile)
            pdfDoc.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDoc.close()

            ExportResult(
                isSuccess = true,
                fileName = fileName,
                filePath = targetFile.absolutePath
            )
        } catch (e: Exception) {
            ExportResult(
                isSuccess = false,
                fileName = "",
                filePath = "",
                errorMessage = e.localizedMessage ?: "Unknown PDF error"
            )
        }
    }

    private fun drawMetricCard(
        canvas: android.graphics.Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        title: String,
        value: String,
        caption: String,
        bgColor: Int,
        borderColor: Int,
        titleColor: Int,
        valueColor: Int
    ) {
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgColor }
        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

        val rect = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rect, 8f, 8f, paintFill)
        canvas.drawRoundRect(rect, 8f, 8f, paintStroke)

        // Title
        paintText.textSize = 7.5f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintText.color = titleColor
        canvas.drawText(title, x + 10f, y + 17f, paintText)

        // Value
        paintText.textSize = 12f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintText.color = valueColor
        canvas.drawText(value, x + 10f, y + 36f, paintText)

        // Caption
        paintText.textSize = 7.5f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintText.color = Color.rgb(107, 114, 128)
        canvas.drawText(caption, x + 10f, y + 52f, paintText)
    }

    fun openOrSharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to SEND intent (share) if no default PDF viewer is registered
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(Intent.createChooser(shareIntent, "Buka atau Bagikan PDF"))
            } catch (_: Exception) {
                // Handled gracefully
            }
        }
    }
}
