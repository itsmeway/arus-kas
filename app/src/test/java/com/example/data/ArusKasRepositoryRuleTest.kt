package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ArusKasRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun upsertAndObserveWallet_authenticatedOwner_succeedsAndDeductsExpense() = runBlocking {
        val uid = signInTestUser("alice_${UUID.randomUUID()}@test.com")
        val repository = ArusKasRepository(firestore, auth)

        val walletId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertWallet(
                walletId = "wallet_bca_${UUID.randomUUID().toString().take(8)}",
                name = "Bank BCA",
                walletType = "BANK",
                balance = 1000000L,
                colorHex = "#0066AE",
                iconKey = "bank_bca"
            )
        }

        val wallets = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeWallets(uid).first { list -> list.any { it.id == walletId } }
        }
        val bca = wallets.first { it.id == walletId }
        assertEquals(1000000L, bca.balance)

        // Record an expense of 35,000 from BCA
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.recordTransactionAndAdjustWallet(
                wallet = bca,
                type = "EXPENSE",
                amount = 35000L,
                categoryKey = "makan_harian",
                categoryLabel = "Makan & Kopi",
                frequencyTag = "HARIAN",
                note = "Makan siang warteg"
            )
        }

        val updatedWallet = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getWalletById(uid, walletId)
        }
        assertNotNull(updatedWallet)
        assertEquals(965000L, updatedWallet!!.balance)
    }

    @Test
    fun getWalletById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser("alice_cross_${UUID.randomUUID()}@test.com")
        val repository = ArusKasRepository(firestore, auth)

        val walletId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertWallet(
                walletId = "wallet_dana_${UUID.randomUUID().toString().take(8)}",
                name = "DANA",
                walletType = "EWALLET",
                balance = 250000L,
                colorHex = "#118EEA",
                iconKey = "ewallet_dana"
            )
        }

        // Switch to Bob and try reading Alice's wallet
        signInTestUser("bob_cross_${UUID.randomUUID()}@test.com")
        var caughtException: Exception? = null
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                repository.getWalletById(aliceUid, walletId)
            }
        } catch (e: Exception) {
            caughtException = e
        }
        assertNotNull("Expected exception on cross-user access", caughtException)
        val firestoreEx = caughtException!!.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx!!.code)
    }

    @Test
    fun observeWallets_unauthenticatedCaller_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser("alice_unauth_${UUID.randomUUID()}@test.com")
        auth.signOut()
        val repository = ArusKasRepository(firestore, auth)

        var caughtException: Exception? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeWallets(aliceUid).first()
            }
        } catch (e: Exception) {
            caughtException = e
        }
        assertNotNull("Expected exception on unauthenticated access", caughtException)
        val firestoreEx = caughtException!!.unwrapFirestoreException()
        assertNotNull("Expected nested FirebaseFirestoreException", firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx!!.code)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
