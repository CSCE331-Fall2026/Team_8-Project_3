import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import {
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword,
} from "firebase/auth";
import { auth } from "../lib/firebase";
import { API_URL } from "../lib/api";

type Mode = "signin" | "register" | "profile";

const HOME: Record<string, string> = {
    manager: "/manager",
    cashier: "/cashier",
    kitchen: "/staff",
    customer: "/customer",
};

const ROLES = [
    { value: "customer", label: "Customer" },
    { value: "cashier", label: "Cashier" },
    { value: "kitchen", label: "Kitchen" },
    { value: "manager", label: "Manager" },
];

// Calls the backend with the signed-in user's token
async function backend(path: string, init: RequestInit = {}) {
    const token = await auth.currentUser!.getIdToken();
    return fetch(`${API_URL}${path}`, {
        ...init,
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
}

export default function Login() {
    const navigate = useNavigate();
    const [mode, setMode] = useState<Mode>("signin");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [error, setError] = useState("");
    const [busy, setBusy] = useState(false);
    const [userRole, setUserRole] = useState("customer");

    // After signing in, ask the backend who this is and where to send them
    async function routeByProfile() {
        const res = await backend("/users/me");
        if (res.status === 404) {
            setMode("profile");
            return;
        }
        if (!res.ok) throw new Error(await res.text());
        const user = await res.json();
        navigate(HOME[String(user.userRole).toLowerCase()] ?? "/customer");
    }

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        setError("");
        setBusy(true);
        try {
            if (mode === "signin") {
                await signInWithEmailAndPassword(auth, email, password);
                await routeByProfile();
            } else if (mode === "register") {
                await createUserWithEmailAndPassword(auth, email, password);
                setMode("profile");
            } else {
                const res = await backend("/users", {
                    method: "POST",
                    body: JSON.stringify({
                        firstName,
                        lastName,
                        phoneNumber: phoneNumber || null,
                        userRole: userRole,
                    }),
                });
                if (!res.ok && res.status !== 409) throw new Error(await res.text());
                await routeByProfile();
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : "Something went wrong");
        } finally {
            setBusy(false);
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-3">
                <h1 className="text-2xl font-bold">
                    {mode === "signin" && "Sign in"}
                    {mode === "register" && "Create account"}
                    {mode === "profile" && "Finish your profile"}
                </h1>

                {mode !== "profile" && (
                    <>
                        <input className="w-full border rounded p-2" type="email"
                               placeholder="Email" value={email} required
                               onChange={(e) => setEmail(e.target.value)} />
                        <input className="w-full border rounded p-2" type="password"
                               placeholder="Password" value={password} required minLength={6}
                               onChange={(e) => setPassword(e.target.value)} />
                    </>
                )}

                {mode === "profile" && (
                    <>
                        <input className="w-full border rounded p-2" placeholder="First name"
                               value={firstName} required
                               onChange={(e) => setFirstName(e.target.value)} />
                        <input className="w-full border rounded p-2" placeholder="Last name"
                               value={lastName} required
                               onChange={(e) => setLastName(e.target.value)} />
                        <input className="w-full border rounded p-2" placeholder="Phone (optional)"
                               value={phoneNumber}
                               onChange={(e) => setPhoneNumber(e.target.value)} />
                        <label className="block text-sm">
                            I am a:
                            <select className="mt-1 w-full border rounded p-2" value={userRole}
                                    onChange={(e) => setUserRole(e.target.value)}>
                                {ROLES.map((r) => (
                                    <option key={r.value} value={r.value}>{r.label}</option>
                                ))}
                            </select>
                        </label>
                    </>
                )}

                {error && <p className="text-red-600 text-sm">{error}</p>}

                <button className="w-full rounded bg-red-700 text-white p-2 disabled:opacity-50"
                        disabled={busy}>
                    {mode === "signin" && "Sign in"}
                    {mode === "register" && "Create account"}
                    {mode === "profile" && "Save and continue"
                    }
                </button>

                {mode !== "profile" && (
                    <button type="button" className="w-full text-sm underline"
                            onClick={() => setMode(mode === "signin" ? "register" : "signin")}>
                        {mode === "signin" ? "New here? Create an account" : "Have an account? Sign in"}
                    </button>
                )}
            </form>
        </div>
    );
}