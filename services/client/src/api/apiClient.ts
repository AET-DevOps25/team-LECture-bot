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
