import { initializeApp } from "firebase/app";
import {
    getAuth,
    connectAuthEmulator,
    signInWithEmailAndPassword,
    createUserWithEmailAndPassword,
} from "firebase/auth";

export type DevRole = "cashier" | "customer" | "manager" | "kitchen";

const USE_EMULATOR = import.meta.env.VITE_USE_AUTH_EMULATOR === "true";
const API_URL: string = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

const app = initializeApp({
    apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
    authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
    projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
});

export const auth = getAuth(app);

if (USE_EMULATOR) {
    connectAuthEmulator(auth, "http://127.0.0.1:9099", {
        disableWarnings: true,
    });
}

const DEV_EMAILS: Record<DevRole, string | undefined> = {
    cashier: import.meta.env.VITE_DEV_CASHIER_EMAIL,
    customer: import.meta.env.VITE_DEV_CUSTOMER_EMAIL,
    manager: import.meta.env.VITE_DEV_MANAGER_EMAIL,
    kitchen: import.meta.env.VITE_DEV_KITCHEN_EMAIL,
};

const inflight: Partial<Record<DevRole, Promise<void>>> = {};

// Dev only: sign in as the role's account, creating it and its profile if needed.
function devSignIn(role: DevRole): Promise<void> {
    if (inflight[role]) return inflight[role]!;

    inflight[role] = (async () => {
        const email = DEV_EMAILS[role];
        const password = import.meta.env.VITE_DEV_PASSWORD;
        if (!email || !password) throw new Error("Not signed in");

        try {
            await signInWithEmailAndPassword(auth, email, password);
        } catch {
            await createUserWithEmailAndPassword(auth, email, password);
        }

        const token = await auth.currentUser!.getIdToken();
        const res = await fetch(`${API_URL}/users`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                firstName: "Dev",
                lastName: role.charAt(0).toUpperCase() + role.slice(1),
                phoneNumber: "9795551234",
                userRole: role,
            }),
        });
        if (!res.ok && res.status !== 409) {
            throw new Error(`Profile creation failed: ${res.status}`);
        }
    })().finally(() => {
        delete inflight[role];
    });

    return inflight[role]!;
}

export async function getToken(role: DevRole = "cashier"): Promise<string> {
    await auth.authStateReady();

    if (USE_EMULATOR) {
        if (!auth.currentUser) await devSignIn(role);
    } else if (!auth.currentUser) {
        throw new Error("Not signed in");
    }

    return auth.currentUser!.getIdToken();
}