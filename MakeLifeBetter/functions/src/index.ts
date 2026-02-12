import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { initializeApp } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";

initializeApp();

// --- Secrets (configurados via: firebase functions:secrets:set MAPS_API_KEY) ---
const mapsApiKey = defineSecret("MAPS_API_KEY");

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
