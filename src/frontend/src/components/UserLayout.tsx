import { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { onAuthStateChanged, signOut } from "firebase/auth";
import { auth } from "../lib/firebase";
import { API_URL } from "../lib/api";
import { Button } from "./ui/button";

export default function UserLayout() {
    const navigate = useNavigate();
    const [name, setName] = useState("");

    // Look up the signed-in user's name whenever the auth state changes
    useEffect(() => {
        return onAuthStateChanged(auth, async (user) => {
            if (!user) {
                setName("");
                return;
            }
            try {
                const token = await user.getIdToken();
                const res = await fetch(`${API_URL}/users/me`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (res.ok) {
                    const me = await res.json();
                    setName(`${me.firstName} ${me.lastName}`);
                }
            } catch {
                // leave the name blank if the lookup fails
            }
        });
    }, []);

    async function handleSignOut() {
        await signOut(auth);
        navigate("/");
    }

    return (
        <div className="flex h-screen flex-col">
            <header className="flex items-center justify-between border-b px-4 py-2">
                <span className="text-sm font-medium">{name || "Not signed in"}</span>
                <Button variant="outline" size="sm" onClick={handleSignOut}>
                    Sign out
                </Button>
            </header>
            <div className="min-h-0 flex-1 overflow-auto">
                <Outlet />
            </div>
        </div>
    );
}