package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppLanguage
import com.example.data.AppLocalization
import com.example.data.SubscriptionConfig
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

/**
 * Modern Google Play Subscription Paywall Dialog for ArusKas PRO.
 * Strictly adheres to Google Play Subscription Transparency Policies:
 * - Clear pricing: Rp 7.000 / month
 * - Transparent 30-day Free Trial offer (Rp 0 today)
 * - Clear explanation of renewal and cancellation mechanisms
 */
@Composable
fun ArusKasProPaywallDialog(
    subscription: SubscriptionConfig,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onActivateFreeTrial: () -> Unit,
    onSubscribePro: () -> Unit,
    onRestorePurchase: () -> Unit
) {
    val context = LocalContext.current
    val isPro = subscription.isProActive
    val isTrial = subscription.isTrialActive
    val isExpired = !isPro && !isTrial
    val remainingDays = subscription.remainingTrialDays

    Dialog(
        onDismissRequest = {
            if (!isExpired) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isExpired,
            dismissOnClickOutside = !isExpired,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(1.2.dp, if (isExpired) Color(0xFFFF5252) else GoldAccent.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 18.dp)
                .testTag("aruskas_pro_paywall_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row: Badge & Close Button (Hidden if trial expired to lock access)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (isExpired) Color(0xFF381515) else GoldAccent.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isExpired) Color(0xFFFF5252) else GoldAccent.copy(alpha = 0.45f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpired) Icons.Default.Shield else Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isExpired) Color(0xFFFF5252) else GoldAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isExpired) "TRIAL BERAKHIR" else AppLocalization.get("pro_badge", language),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (isExpired) Color(0xFFFF8A80) else GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!isExpired) {
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

                // Iconic Crown/Shield Logo
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1B1602),
                    border = BorderStroke(1.5.dp, GoldAccent),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title & Subtitle
                Text(
                    text = AppLocalization.get("paywall_title", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = AppLocalization.get("paywall_subtitle", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Highlighting Plan Card (Rp 7.000 / Bulan + Free Trial 30 Hari)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF141916)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Trial Free 30 Days Badge
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EmeraldPrimary,
                            modifier = Modifier.testTag("trial_highlight_badge")
                        ) {
                            Text(
                                text = AppLocalization.get("paywall_trial_highlight", language),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color(0xFF041E1B),
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Main Price
                        Text(
                            text = AppLocalization.get("paywall_price_headline", language),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent
                        )

                        Text(
                            text = AppLocalization.get("paywall_price_sub", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA5D6A7),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5 Core Pro Benefits Checklist
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProFeatureRow(AppLocalization.get("paywall_feature1", language))
                        ProFeatureRow(AppLocalization.get("paywall_feature2", language))
                        ProFeatureRow(AppLocalization.get("paywall_feature3", language))
                        ProFeatureRow(AppLocalization.get("paywall_feature4", language))
                        ProFeatureRow(AppLocalization.get("paywall_feature5", language))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Google Play Subscription Policy Compliance Note
                Text(
                    text = AppLocalization.get("paywall_policy_transparency", language),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Main CTA Action Button
                if (isPro) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldPrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = AppLocalization.get("pro_active_chip", language),
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (isTrial) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = String.format(
                                    AppLocalization.get("pro_trial_chip", language),
                                    remainingDays
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }

                        Button(
                            onClick = onSubscribePro,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF241800)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("subscribe_pro_button")
                        ) {
                            Text(
                                text = AppLocalization.get("btn_subscribe_now", language),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                } else if (subscription.trialStartDateMillis <= 0L) {
                    // Start 30-Day Free Trial (First time)
                    Button(
                        onClick = onActivateFreeTrial,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_free_trial_button")
                    ) {
                        Text(
                            text = AppLocalization.get("btn_start_free_trial", language),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                } else {
                    // Trial Expired: Lockout state requiring subscription to unlock
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF381515),
                            border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ Masa uji coba 30 hari telah berakhir. Aktifkan langganan Rp 7.000 / bulan untuk membuka kunci dan terus menggunakan ArusKas.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color(0xFFFF8A80),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }

                        Button(
                            onClick = onSubscribePro,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF241800)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("subscribe_pro_button_expired")
                        ) {
                            Text(
                                text = AppLocalization.get("btn_subscribe_now", language),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Links: Restore Purchases & Manage in Google Play
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onRestorePurchase,
                        modifier = Modifier.testTag("restore_purchase_button")
                    ) {
                        Text(
                            text = AppLocalization.get("btn_restore_purchase", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = {
                            val playUrl = "https://play.google.com/store/account/subscriptions"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Ignore if browser is unavailable
                            }
                        },
                        modifier = Modifier.testTag("manage_play_store_subscriptions")
                    ) {
                        Text(
                            text = AppLocalization.get("btn_manage_subscription", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProFeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}
