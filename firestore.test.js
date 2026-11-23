const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_001";
const BOB_UID = "bob_uid_002";
const ADMIN_UID = "admin_uid_003";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
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

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("reminders").get()
  );
  await assertFails(
    unauthDb.collection("admin_medicines").where("published", "==", true).get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Intruder",
      preferredLanguage: "English",
      healthConsentGranted: false,
      savedMedicineIds: [],
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("User profile: owner create, partial update, cross-user denial, shadow-key rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Dr. Alice Mehta",
      email: "alice@example.com",
      preferredLanguage: "English",
      darkMode: false,
      healthConsentGranted: true,
      heightCm: 168,
      weightKg: 62,
      savedMedicineIds: ["med_amoxicillin"],
      expiryAlertDays: 30,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Partial update succeeds while preserving createdAt
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      preferredLanguage: "Hindi",
      darkMode: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Cross-user PII read & write denied
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      fullName: "Hijacked",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Shadow field injection (isAdmin) denied
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Immutable createdAt tamper denied
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("User subcollections: reminders, cabinet, bmi_history, reports, notifications", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);

  // Reminders
  const remRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("reminders")
    .doc("rem_1");
  await assertSucceeds(
    remRef.set({
      medicineName: "Metformin",
      strength: "500 mg",
      dosageForm: "Tablet",
      prescribedDose: "1 tablet",
      scheduleType: "Twice daily",
      reminderTimes: ["08:00", "20:00"],
      startDate: "2026-11-23",
      status: "UPCOMING",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("reminders").get()
  );
  await assertSucceeds(
    remRef.update({
      status: "TAKEN",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("reminders").get()
  );

  // Cabinet / Expiry Tracker
  const cabRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("cabinet")
    .doc("cab_1");
  await assertSucceeds(
    cabRef.set({
      medicineName: "Paracetamol",
      brand: "Dolo 650",
      batchNumber: "B2026X",
      expiryDate: "2027-06-30",
      quantity: 15,
      strength: "650 mg",
      storageLocation: "Home Kit",
      alertDaysBefore: 30,
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertFails(
    cabRef.set({
      id: "cab_1", // Forbidden @DocumentId key
      medicineName: "Bad",
      expiryDate: "2027-06-30",
      quantity: 10,
      alertDaysBefore: 30,
      createdAt: created,
      updatedAt: created,
    })
  );

  // BMI History
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("bmi_history")
      .doc("bmi_1")
      .set({
        bmiValue: 22.1,
        category: "Normal weight",
        heightCm: 170,
        weightKg: 64,
        recordedDate: "2026-11-23",
        createdAt: created,
      })
  );

  // Saved Reports
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("reports")
      .doc("rep_1")
      .set({
        reportType: "INTERACTION",
        title: "Warfarin + Aspirin",
        summary: "High bleeding risk interaction.",
        severity: "HIGH",
        createdAt: created,
      })
  );

  // Notifications
  const notifRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("notifications")
    .doc("notif_1");
  await assertSucceeds(
    notifRef.set({
      category: "Medicine reminders",
      title: "Morning Dose",
      message: "Time for Metformin 500 mg",
      read: false,
      createdAt: created,
    })
  );
  await assertSucceeds(notifRef.update({ read: true }));
  await assertFails(
    notifRef.update({ read: true, title: "Tampered Title" })
  );
});

test("Admin medicines catalog: verified admin writes, unverified spoof fails, published query succeeds", async () => {
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, {
      email: "buildolive4@gmail.com",
      email_verified: true,
    })
    .firestore();
  const spoofAdminDb = testEnv
    .authenticatedContext("spoof_uid", {
      email: "buildolive4@gmail.com",
      email_verified: false,
    })
    .firestore();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  const medPayload = {
    genericName: "Atorvastatin",
    brandNames: ["Lipitor", "Atorva"],
    drugClass: "Antihyperlipidemic (Statin)",
    strengths: ["10 mg", "20 mg", "40 mg"],
    dosageForms: ["Tablet"],
    composition: "Atorvastatin Calcium",
    therapeuticUses: "Management of hypercholesterolemia and cardiovascular risk reduction.",
    mechanismOfAction: "Competitively inhibits HMG-CoA reductase.",
    commonSideEffects: "Mild myalgia, nasopharyngitis, GI discomfort.",
    precautions: "Monitor liver enzymes; avoid grapefruit excess.",
    published: true,
    updatedBy: ADMIN_UID,
    createdAt: created,
    updatedAt: created,
  };

  // Verified admin succeeds
  await assertSucceeds(
    adminDb.collection("admin_medicines").doc("med_atorvastatin").set(medPayload)
  );

  // Email spoofing without email_verified fails
  await assertFails(
    spoofAdminDb.collection("admin_medicines").doc("med_spoof").set({
      ...medPayload,
      updatedBy: "spoof_uid",
    })
  );

  // Regular user cannot write admin_medicines
  await assertFails(
    aliceDb.collection("admin_medicines").doc("med_hack").set({
      ...medPayload,
      updatedBy: ALICE_UID,
    })
  );

  // Regular user filtered published query succeeds; unfiltered query fails
  await assertSucceeds(
    aliceDb.collection("admin_medicines").where("published", "==", true).get()
  );
  await assertFails(aliceDb.collection("admin_medicines").get());

  // Future timestamp rejected
  await assertFails(
    adminDb.collection("admin_medicines").doc("med_future").set({
      ...medPayload,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});
