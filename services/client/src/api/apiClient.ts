//import createClient from "openapi-fetch";
//import type { paths } from "../shared/api/generated/api";
//import storage from "../utils/storage";
//
//const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
//
//export const apiClient = createClient<paths>({
//    baseUrl: API_BASE_URL,
//});
//
//// Add an interceptor to automatically include the auth token in requests
//apiClient.use({
//    async onRequest({ request }) {
//        const token = storage.getItem<string>('jwtToken');
//        console.log("jwtToken" + token);
//        if (token) {
//            request.headers.set("Authorization", `Bearer ${token}`);
//        }
//        return request;
//    },
//});

import createClient from "openapi-fetch";
import type { paths } from "../shared/api/generated/api";
import storage from "../utils/storage";

// This will hold the initialized client instance
let apiClient: ReturnType<typeof createClient<paths>>;

// Asynchronous initialization function
async function initializeApiClient() {
    // If the client is already initialized, do nothing.
    if (apiClient) {
        return;
    }

    let baseUrl = 'http://localhost:8080/api/v1/'; // Default for local dev

    try {
        // This fetches the configuration file provided by your Kubernetes setup.
        //
        const response = await fetch('/config.json');
        if (response.ok) {
            const config = await response.json();
            baseUrl = config.PUBLIC_API_URL;
        } else {
            console.warn('config.json not found, using default fallback URL.');
        }
    } catch (error) {
        console.error('Error fetching config.json:', error);
    }

    // Create the client instance
    apiClient = createClient<paths>({ baseUrl });

    // Add the interceptor to include the auth token
    apiClient.use({
        async onRequest({ request }) {
            const token = storage.getItem<string>('jwtToken');
            console.log("jwtToken: " + token);
            if (token) {
                request.headers.set("Authorization", `Bearer ${token}`);
            }
            return request;
        },
    });
}

// Immediately call the initialization function
const apiClientPromise = initializeApiClient();

// Export the instance and the promise for flexibility
export { apiClient, apiClientPromise };

export async function rawPost(url: string, formData: FormData) {
    // Always send uploads to the discovery service on port 8080, no matter what
    const fullUrl = `http://localhost:8080/api/v1${url}`;
    const token = storage.getItem<string>('jwtToken');
    const headers = new Headers();
    if (token) headers.set('Authorization', `Bearer ${token}`);
    // Do NOT set Content-Type for FormData, browser will handle it

    const response = await fetch(fullUrl, {
        method: 'POST',
        body: formData,
        headers,
    });

    let data = null;
    try {
        data = await response.json();
    } catch {
        // ignore if not JSON
    }

    return {
        data,
        error: !response.ok ? data || { message: 'Upload failed' } : undefined,
    };
}
