// This file contains the core API client logic for the frontend.
// It will be refactored in Sub-Issue 5 to use the generated API client.

import type { components } from '../shared/api/generated/api';

// Define types for request and response bodies based on OpenAPI paths
type RegisterRequestBody = components['schemas']['RegisterRequest'];
type LoginRequestBody = components['schemas']['LoginRequest'];
type LoginResponseBody = components['schemas']['LoginResponse'];

// Correct base URL for the backend API, including the /api/v1 context path.
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Generic fetch wrapper to handle common headers and error responses,
// and to provide type safety for responses.
async function fetchApi<TResponse, TBody = undefined>(
  path: string,
  method: string,
  body?: TBody,
  headers?: HeadersInit
): Promise<TResponse> {
  const options: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
  };

  if (body !== undefined) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, options);

  if (!response.ok) {
    // Attempt to parse error message from response body if available
    const errorBody = await response.text();
    // For 400/401 errors, the server might return a plain text message.
    // For other errors, response.statusText is a good fallback.
    throw new Error(errorBody || response.statusText);
  }

  // Handle cases where the response might be empty (e.g., 204 No Content)
  // or where the expected response is plain text (like registerUser success)
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json() as Promise<TResponse>;
  } else {
    // If not JSON, assume plain text or no content
    return response.text() as Promise<TResponse>;
  }
}


// Specific API calls using the generated types
export const registerUser = async (data: RegisterRequestBody): Promise<string> => {
  // Path for registerUser is /auth/register
  return fetchApi<string, RegisterRequestBody>('/auth/register', 'POST', data);
};

export const loginUser = async (data: LoginRequestBody): Promise<LoginResponseBody> => {
  // Path for loginUser is /auth/login
  return fetchApi<LoginResponseBody, LoginRequestBody>('/auth/login', 'POST', data);
};