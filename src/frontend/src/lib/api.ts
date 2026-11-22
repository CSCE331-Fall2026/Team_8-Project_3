import { getToken, type DevRole } from "./firebase";

export const API_URL: string =
    import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export async function apiFetch(
    path: string,
    options: RequestInit = {},
    { auth = true, role }: { auth?: boolean; role?: DevRole } = {},
): Promise<Response> {
    const headers = new Headers(options.headers);
    if (options.body && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }
    if (auth) {
        headers.set("Authorization", `Bearer ${await getToken(role)}`);
    }
    return fetch(`${API_URL}${path}`, { ...options, headers });
}