// Helper to update a course space (since OpenAPI client does not support PUT for /coursespaces/{id})
export async function updateCourseSpace(id: string, data: { title: string; description: string }) {
    const token = storage.getItem<string>('jwtToken');
    const res = await fetch(`${API_BASE_URL}/coursespaces/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to update course space');
    return await res.json();
}
import createClient from "openapi-fetch";
import type { paths } from "../shared/api/generated/api";
import storage from "../utils/storage";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const apiClient = createClient<paths>({
    baseUrl: API_BASE_URL,
});


// Add an interceptor to automatically include the auth token in requests
apiClient.use({
    async onRequest({ request }) {
        const token = storage.getItem<string>('jwtToken');
        if (token) {
            request.headers.set("Authorization", `Bearer ${token}`);
        }
        return request;
    },
});

// Helper to get all courses for the current user
export async function getCourses() {
    const res = await apiClient.GET('/coursespaces');
    if (res.error) throw new Error('Failed to fetch courses');
    return res.data || [];
}
