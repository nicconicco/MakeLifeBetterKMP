import { onCall, onRequest, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { initializeApp } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";
import Stripe from "stripe";

initializeApp();

// --- Secrets (configurados via: firebase functions:secrets:set <NAME>) ---
const mapsApiKey = defineSecret("MAPS_API_KEY");
const firebaseWebConfig = defineSecret("WEB_APP_CONFIG");
const stripeSecretKey = defineSecret("STRIPE_SECRET_KEY");

// =============================================================================
// 0. GET WEB CONFIG - Retorna Firebase config para o web app (HTTPS, não callable)
//    Uso: fetch("https://us-central1-makelifebetter-7f3c9.cloudfunctions.net/getWebConfig")
//    Não precisa de autenticação pois o config é público por natureza.
// =============================================================================
export const getWebConfig = onRequest(
  { secrets: [firebaseWebConfig], cors: true },
  (req, res) => {
    try {
      const raw = firebaseWebConfig.value();
      // Tenta parsear como JSON, se falhar retorna como string raw
      try {
        const config = JSON.parse(raw);
        res.json(config);
      } catch {
        // Se o valor já é um objeto serializado de outra forma, retorna raw
        console.error("Failed to parse WEB_APP_CONFIG. Raw value length:", raw.length);
        res.status(500).json({ error: "Invalid config format" });
      }
    } catch (e) {
      console.error("Failed to read WEB_APP_CONFIG secret:", e);
      res.status(500).json({ error: "Config not available" });
    }
  }
);

// =============================================================================
// 1. GET SECURE CONFIG - Retorna chaves sensíveis apenas para usuários autenticados
// =============================================================================
export const getSecureConfig = onCall(
  { secrets: [mapsApiKey] },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    return {
      mapsApiKey: mapsApiKey.value(),
    };
  }
);

// =============================================================================
// 2. SET ADMIN CLAIM - Promove um usuário a admin (apenas admin pode chamar)
// =============================================================================
export const setAdminClaim = onCall(
  {},
  async (request) => {
    if (!request.auth?.token.admin) {
      throw new HttpsError("permission-denied", "Only admins can promote users.");
    }

    const { email } = request.data;
    if (!email || typeof email !== "string") {
      throw new HttpsError("invalid-argument", "Email is required.");
    }

    const user = await getAuth().getUserByEmail(email);
    await getAuth().setCustomUserClaims(user.uid, { admin: true });

    return { message: `${email} is now admin.` };
  }
);

// =============================================================================
// 3. BOOTSTRAP FIRST ADMIN - Cria o primeiro admin (só funciona uma vez)
// =============================================================================
export const bootstrapFirstAdmin = onCall(
  {},
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    // Verifica se já existe algum admin
    const db = getFirestore();
    const bootstrapDoc = await db.doc("_system/bootstrap").get();

    if (bootstrapDoc.exists) {
      throw new HttpsError("already-exists", "Admin already bootstrapped.");
    }

    // Promove o caller a admin
    await getAuth().setCustomUserClaims(request.auth.uid, { admin: true });

    // Marca como bootstrapped para nunca mais funcionar
    await db.doc("_system/bootstrap").set({
      adminUid: request.auth.uid,
      createdAt: new Date().toISOString(),
    });

    return { message: "You are now the first admin." };
  }
);

// =============================================================================
// 4. ADMIN: DELETE ALL DATA - Limpa collections (migrado do SecretScreen)
// =============================================================================
export const adminDeleteAllData = onCall(
  {},
  async (request) => {
    if (!request.auth?.token.admin) {
      throw new HttpsError("permission-denied", "Admin only.");
    }

    const db = getFirestore();
    const collections = [
      "eventos",
      "lista_geral",
      "produtos",
      "categorias",
      "carrinho",
      "pedidos",
      "duvidas",
      "event_location",
      "banners",
    ];

    let totalDeleted = 0;

    for (const collectionName of collections) {
      const snapshot = await db.collection(collectionName).get();
      const batch = db.batch();
      snapshot.docs.forEach((doc) => {
        batch.delete(doc.ref);
        totalDeleted++;
      });
      await batch.commit();
    }

    return { message: `Deleted ${totalDeleted} documents.` };
  }
);

// =============================================================================
// 5. CHECK ADMIN STATUS - Verifica se o usuário atual é admin
// =============================================================================
export const checkAdminStatus = onCall(
  {},
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    return {
      isAdmin: request.auth.token.admin === true,
      email: request.auth.token.email,
    };
  }
);

// =============================================================================
// 6. CREATE PAYMENT INTENT - Cria PaymentIntent no Stripe para processar pagamento
//    Requer autenticação. Recebe amount em centavos (ex: R$50.00 = 5000).
//    Retorna clientSecret, ephemeralKey e customerId para o Payment Sheet.
// =============================================================================
export const createPaymentIntent = onCall(
  { secrets: [stripeSecretKey] },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    const { amount } = request.data;

    if (!amount || typeof amount !== "number" || amount <= 0) {
      throw new HttpsError(
        "invalid-argument",
        "Amount must be a positive number in centavos."
      );
    }

    const stripe = new Stripe(stripeSecretKey.value());

    const uid = request.auth.uid;
    const email = request.auth.token.email || "";

    // Busca ou cria customer no Stripe vinculado ao Firebase UID
    const existingCustomers = await stripe.customers.list({
      limit: 1,
      email: email,
    });

    let customer: Stripe.Customer;

    if (existingCustomers.data.length > 0) {
      customer = existingCustomers.data[0];
    } else {
      customer = await stripe.customers.create({
        email: email,
        metadata: { firebaseUid: uid },
      });
    }

    // Cria EphemeralKey para o customer (necessário para o Payment Sheet)
    const ephemeralKey = await stripe.ephemeralKeys.create(
      { customer: customer.id },
      { apiVersion: "2025-04-30.basil" }
    );

    // Cria PaymentIntent
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amount),
      currency: "brl",
      customer: customer.id,
      automatic_payment_methods: { enabled: true },
      metadata: { firebaseUid: uid },
    });

    return {
      paymentIntent: paymentIntent.client_secret,
      ephemeralKey: ephemeralKey.secret,
      customer: customer.id,
    };
  }
);
