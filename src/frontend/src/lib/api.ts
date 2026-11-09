// Set VITE_API_URL at build time to point at the deployed backend
export const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
