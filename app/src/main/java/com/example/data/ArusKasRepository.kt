package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ArusKasRepository(
    private val db: FirebaseFirestore,
    private val auth: com.google.firebase.auth.FirebaseAuth = Firebase.auth
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeWallets(userId: String = requireUserId()): Flow<List<Wallet>> = flow {
        val path = "users/$userId/wallets"
        emitAll(
            db.collection("users").document(userId).collection("wallets")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Wallet::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeTransactions(userId: String = requireUserId()): Flow<List<TransactionRecord>> = flow {
        val path = "users/$userId/transactions"
        emitAll(
            db.collection("users").document(userId).collection("transactions")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        TransactionRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.transactionDateMillis }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeGoals(userId: String = requireUserId()): Flow<List<SavingsGoal>> = flow {
        val path = "users/$userId/goals"
        emitAll(
            db.collection("users").document(userId).collection("goals")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SavingsGoal::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getWalletById(userId: String, walletId: String): Wallet? {
        val docRef = db.collection("users").document(userId).collection("wallets").document(walletId)
        return try {
            docRef.get().await().toObject(
                Wallet::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun upsertWallet(
        walletId: String? = null,
        name: String,
        walletType: String,
        balance: Long,
        colorHex: String,
        iconKey: String
    ): String {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("wallets")
        val docRef = if (walletId.isNullOrBlank()) collectionRef.document() else collectionRef.document(walletId)
        val payload = mapOf(
            "userId" to uid,
            "name" to name.trim().take(60),
            "walletType" to walletType,
            "balance" to balance,
            "colorHex" to colorHex,
            "iconKey" to iconKey,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateWalletBalance(wallet: Wallet, newBalance: Long) {
        val uid = requireUserId()
        require(wallet.id.isNotBlank()) { "Wallet ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("wallets").document(wallet.id)
        try {
            docRef.update(
                mapOf(
                    "balance" to newBalance,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateWalletDetails(wallet: Wallet, newName: String, newBalance: Long, newType: String) {
        val uid = requireUserId()
        require(wallet.id.isNotBlank()) { "Wallet ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("wallets").document(wallet.id)
        try {
            docRef.update(
                mapOf(
                    "name" to newName.trim().take(60),
                    "walletType" to newType,
                    "balance" to newBalance,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteWallet(walletId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("wallets").document(walletId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun recordTransactionAndAdjustWallet(
        wallet: Wallet,
        type: String, // EXPENSE, INCOME, SAVINGS_ALLOCATION
        amount: Long,
        categoryKey: String,
        categoryLabel: String,
        frequencyTag: String,
        note: String,
        transactionDateMillis: Long = System.currentTimeMillis()
    ): String {
        val uid = requireUserId()
        require(amount > 0) { "Nominal harus lebih dari 0" }
        val txRef = db.collection("users").document(uid).collection("transactions").document()
        val walletRef = db.collection("users").document(uid).collection("wallets").document(wallet.id)

        val delta = if (type == "INCOME") amount else -amount
        val updatedBalance = wallet.balance + delta

        val txPayload = mapOf(
            "userId" to uid,
            "type" to type,
            "amount" to amount,
            "categoryKey" to categoryKey.take(50),
            "categoryLabel" to categoryLabel.take(60),
            "frequencyTag" to frequencyTag,
            "walletId" to wallet.id,
            "walletName" to wallet.name.take(60),
            "note" to note.trim().ifBlank { categoryLabel }.take(300),
            "transactionDateMillis" to transactionDateMillis,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            val batch = db.batch()
            batch.set(txRef, txPayload)
            batch.update(
                walletRef,
                mapOf(
                    "balance" to updatedBalance,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            return txRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }
    }

    suspend fun deleteTransactionAndRevertWallet(
        tx: TransactionRecord,
        currentWallet: Wallet?
    ) {
        val uid = requireUserId()
        val txRef = db.collection("users").document(uid).collection("transactions").document(tx.id)
        try {
            val batch = db.batch()
            batch.delete(txRef)
            if (currentWallet != null && currentWallet.id == tx.walletId) {
                val walletRef = db.collection("users").document(uid).collection("wallets").document(currentWallet.id)
                val revertDelta = if (tx.type == "INCOME") -tx.amount else tx.amount
                batch.update(
                    walletRef,
                    mapOf(
                        "balance" to (currentWallet.balance + revertDelta),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, txRef.path)
            throw e
        }
    }

    suspend fun createSavingsGoal(
        title: String,
        targetAmount: Long,
        savedAmount: Long = 0L,
        dailyRecommendation: Long = 25000L,
        note: String,
        iconKey: String = "laptop"
    ): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("goals").document()
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().take(80),
            "targetAmount" to targetAmount.coerceAtLeast(1L),
            "savedAmount" to savedAmount.coerceAtLeast(0L),
            "dailyRecommendation" to dailyRecommendation.coerceAtLeast(0L),
            "note" to note.trim().take(300),
            "iconKey" to iconKey,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun allocateToGoalFromWallet(
        goal: SavingsGoal,
        wallet: Wallet,
        amount: Long,
        note: String
    ) {
        val uid = requireUserId()
        require(amount > 0) { "Nominal tabungan harus > 0" }
        val goalRef = db.collection("users").document(uid).collection("goals").document(goal.id)
        val walletRef = db.collection("users").document(uid).collection("wallets").document(wallet.id)
        val txRef = db.collection("users").document(uid).collection("transactions").document()

        val txPayload = mapOf(
            "userId" to uid,
            "type" to "SAVINGS_ALLOCATION",
            "amount" to amount,
            "categoryKey" to "tabungan_impian",
            "categoryLabel" to "Tabungan: ${goal.title}".take(60),
            "frequencyTag" to "TABUNGAN",
            "walletId" to wallet.id,
            "walletName" to wallet.name.take(60),
            "note" to note.trim().ifBlank { "Setor ke impian ${goal.title}" }.take(300),
            "transactionDateMillis" to System.currentTimeMillis(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            val batch = db.batch()
            batch.update(
                goalRef,
                mapOf(
                    "savedAmount" to (goal.savedAmount + amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                walletRef,
                mapOf(
                    "balance" to (wallet.balance - amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(txRef, txPayload)
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, goalRef.path)
            throw e
        }
    }

    suspend fun deleteSavingsGoal(goalId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("goals").document(goalId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun seedStarterSetup(
        walletBalances: Map<String, Long>,
        initialGoalTitle: String,
        initialGoalTarget: Long,
        initialGoalDaily: Long,
        initialGoalNote: String
    ) {
        val uid = requireUserId()
        val userDoc = db.collection("users").document(uid)
        try {
            val batch = db.batch()
            PresetCategories.defaultStarterWallets.forEach { preset ->
                val ref = userDoc.collection("wallets").document(preset.id)
                val customBal = walletBalances[preset.id] ?: preset.balance
                batch.set(
                    ref,
                    mapOf(
                        "userId" to uid,
                        "name" to preset.name,
                        "walletType" to preset.walletType,
                        "balance" to customBal,
                        "colorHex" to preset.colorHex,
                        "iconKey" to preset.iconKey,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }

            if (initialGoalTitle.isNotBlank() && initialGoalTarget > 0) {
                val goalRef = userDoc.collection("goals").document("goal_starter_laptop")
                batch.set(
                    goalRef,
                    mapOf(
                        "userId" to uid,
                        "title" to initialGoalTitle.trim().take(80),
                        "targetAmount" to initialGoalTarget,
                        "savedAmount" to 0L,
                        "dailyRecommendation" to initialGoalDaily.coerceAtLeast(10000L),
                        "note" to initialGoalNote.trim().ifBlank { "Fokus sisihkan dari gaji, ngojek, dan usaha!" }.take(300),
                        "iconKey" to "laptop",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/$uid/wallets")
            throw e
        }
    }
}
