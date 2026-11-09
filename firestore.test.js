const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write wallets", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("wallets").get());
});

test("Authenticated user can create and read their own wallet", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("wallets").doc("bca").set({
      userId: ALICE_UID,
      name: "Bank BCA",
      walletType: "BANK",
      balance: 2500000,
      colorHex: "#0066AE",
      iconKey: "bank_bca",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("wallets").doc("bca").get()
  );
});

test("Authenticated user cannot read or modify another user's wallet", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).collection("wallets").doc("dana").set({
      userId: BOB_UID,
      name: "DANA",
      walletType: "EWALLET",
      balance: 500000,
      colorHex: "#118EEA",
      iconKey: "ewallet_dana",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("wallets").doc("dana").get()
  );
});

test("Shadow update with ghost field on wallet is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("wallets").doc("bca").set({
      userId: ALICE_UID,
      name: "Bank BCA",
      walletType: "BANK",
      balance: 2500000,
      colorHex: "#0066AE",
      iconKey: "bank_bca",
      isVerified: true,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Authenticated user can create valid expense transaction and savings goal", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("tx_1").set({
      userId: ALICE_UID,
      type: "EXPENSE",
      amount: 25000,
      categoryKey: "makan_harian",
      categoryLabel: "Makan Harian",
      frequencyTag: "HARIAN",
      walletId: "gopay",
      walletName: "GoPay",
      note: "Beli nasi padang siang",
      transactionDateMillis: Date.now(),
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("goals").doc("goal_laptop").set({
      userId: ALICE_UID,
      title: "Beli Laptop Kerja",
      targetAmount: 12000000,
      savedAmount: 1500000,
      dailyRecommendation: 50000,
      note: "Target 7 bulan lagi dari hasil ngojek & usaha",
      iconKey: "laptop",
      createdAt: now,
      updatedAt: now,
    })
  );
});
