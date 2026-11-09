package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppLanguage
import com.example.data.AppLocalization
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue

/**
 * Modern, interactive User Guide Modal (Panduan Awal Pengguna ArusKas)
 * Designed to explain the 4 lifesaver financial pillars with maximal clarity and minimal cognitive load.
 */
@Composable
fun UserGuideDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCompleteGuide: () -> Unit,
    onOpenStarterSetup: () -> Unit = {}
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
                .testTag("user_guide_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Badge & Skip/Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = AppLocalization.get("guide_badge", language),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = {
                                onCompleteGuide()
                                onDismiss()
                            },
                            modifier = Modifier.testTag("guide_skip_button")
                        ) {
                            Text(
                                text = AppLocalization.get("guide_btn_skip", language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title & Subtitle
                Text(
                    text = AppLocalization.get("guide_title", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = AppLocalization.get("guide_subtitle", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Dots Step Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        val isSelected = i == currentStep
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 7.dp,
                            animationSpec = tween(durationMillis = 250),
                            label = "dotWidth"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            animationSpec = tween(durationMillis = 250),
                            label = "dotColor"
                        )
                        Box(
                            modifier = Modifier
                                .height(7.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(50))
                                .background(dotColor)
                                .clickable { currentStep = i }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dynamic Animated Step Content
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                    },
                    label = "stepContentAnimation"
                ) { step ->
                    when (step) {
                        0 -> GuideStepCard(
                            stepNumber = 1,
                            badge = AppLocalization.get("guide_step1_badge", language),
                            title = AppLocalization.get("guide_step1_title", language),
                            desc = AppLocalization.get("guide_step1_desc", language),
                            tip = AppLocalization.get("guide_step1_tip", language),
                            accentColor = EmeraldPrimary,
                            icon = Icons.Default.AccountBalanceWallet,
                            previewContent = { SafeToSpendVisualPreview() }
                        )
                        1 -> GuideStepCard(
                            stepNumber = 2,
                            badge = AppLocalization.get("guide_step2_badge", language),
                            title = AppLocalization.get("guide_step2_title", language),
                            desc = AppLocalization.get("guide_step2_desc", language),
                            tip = AppLocalization.get("guide_step2_tip", language),
                            accentColor = GoldAccent,
                            icon = Icons.Default.ElectricBolt,
                            previewContent = { QuickLogVisualPreview() }
                        )
                        2 -> GuideStepCard(
                            stepNumber = 3,
                            badge = AppLocalization.get("guide_step3_badge", language),
                            title = AppLocalization.get("guide_step3_title", language),
                            desc = AppLocalization.get("guide_step3_desc", language),
                            tip = AppLocalization.get("guide_step3_tip", language),
                            accentColor = CoralExpense,
                            icon = Icons.Default.HealthAndSafety,
                            previewContent = { LimitEmergencyVisualPreview() }
                        )
                        else -> GuideStepCard(
                            stepNumber = 4,
                            badge = AppLocalization.get("guide_step4_badge", language),
                            title = AppLocalization.get("guide_step4_title", language),
                            desc = AppLocalization.get("guide_step4_desc", language),
                            tip = AppLocalization.get("guide_step4_tip", language),
                            accentColor = SkyBlue,
                            icon = Icons.Default.Savings,
                            previewContent = { GoalSavingsPdfVisualPreview() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Navigation Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("guide_prev_button")
                        ) {
                            Text(
                                text = AppLocalization.get("guide_btn_prev", language),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (currentStep < totalSteps - 1) {
                        Button(
                            onClick = { currentStep += 1 },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(if (currentStep > 0) 1.5f else 1f)
                                .height(48.dp)
                                .testTag("guide_next_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = AppLocalization.get("guide_btn_next", language),
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        // Final step: Big Got It button + Starter balance setup option
                        Button(
                            onClick = {
                                onCompleteGuide()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1.8f)
                                .height(48.dp)
                                .testTag("guide_finish_button")
                        ) {
                            Text(
                                text = AppLocalization.get("guide_btn_finish", language),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                // If on the final slide, show instant starter setup button
                if (currentStep == totalSteps - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            onCompleteGuide()
                            onDismiss()
                            onOpenStarterSetup()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guide_setup_now_button")
                    ) {
                        Text(
                            text = AppLocalization.get("guide_btn_setup_now", language),
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Card container for each walkthrough step with visual preview
 */
@Composable
private fun GuideStepCard(
    stepNumber: Int,
    badge: String,
    title: String,
    desc: String,
    tip: String,
    accentColor: Color,
    icon: ImageVector,
    previewContent: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Icon & Step Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.18f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Interactive/Mock visual demonstration
            previewContent()

            // Main descriptive explanation
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            // Highlighting tip box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.12f),
                border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = tip,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
    }
}

/**
 * Visual Mock Preview for Step 1: Safe-to-Spend vs Total Net Worth
 */
@Composable
private fun SafeToSpendVisualPreview() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF041E1B),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UANG BEBAS BELANJA (AMAN)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EmeraldPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "STATUS: AMAN",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "Rp 1.850.000",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total Seluruh Uang di Rekening: Rp 6.850.000 (Rp 5 Jt dikunci di Tabungan & Darurat)",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color(0xFFA5D6A7)
            )
        }
    }
}

/**
 * Visual Mock Preview for Step 2: 3-Second Quick Log with Color Line Indicator
 */
@Composable
private fun QuickLogVisualPreview() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF141917),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kategori: Makan & Ngopi",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nominal: Rp 28.000",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            // Simulating the dynamic active color line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(EmeraldPrimary, GoldAccent)
                        )
                    )
            )

            // Simulating Bestie advice toast
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF1C2B22)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "Bestie: 'Mantap ngopi 28rb masih masuk budget harian, gas!'",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color(0xFFC8E6C9)
                    )
                }
            }
        }
    }
}

/**
 * Visual Mock Preview for Step 3: Spending Limits & Emergency Shield
 */
@Composable
private fun LimitEmergencyVisualPreview() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF181515),
        border = BorderStroke(1.dp, CoralExpense.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Limit Jajan & Hiburan: Rp 350rb / Rp 500rb",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sisa: 30%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { 0.7f },
                color = GoldAccent,
                trackColor = Color(0xFF332A15),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡️ Brankas Darurat: Rp 1.500.000 (Terkunci)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = SkyBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CoralExpense.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Khusus Keadaan Mendesak",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                        color = CoralExpense,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Visual Mock Preview for Step 4: Goal Savings Piggy Bank & Modern PDF Report
 */
@Composable
private fun GoalSavingsPdfVisualPreview() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F1B24),
        border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Celengan: Beli Laptop Impian",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rp 4,5 Jt / 10 Jt (45%)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { 0.45f },
                color = SkyBlue,
                trackColor = Color(0xFF162D3D),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Grafik Adu Cuan & Laporan PDF Modern",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = Color(0xFF90CAF9)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EmeraldPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Export Siap Pakai",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
