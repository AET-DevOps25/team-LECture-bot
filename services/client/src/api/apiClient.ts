import storage from '../utils/storage';
import type { components } from '../shared/api/generated/api';

// Define types for request and response bodies based on OpenAPI paths
type RegisterRequestBody = components['schemas']['RegisterRequest'];
type LoginRequestBody = components['schemas']['LoginRequest'];
type LoginResponseBody = components['schemas']['LoginResponse'];
type UserProfileResponseBody = components['schemas']['UserProfile'];
type UpdateUserProfileRequestBody = components['schemas']['UpdateUserProfileRequest'];
type UpdateUserProfileResponseBody = components['schemas']['UpdateUserProfileResponse'];
type ChangePasswordRequestBody = components['schemas']['ChangePasswordRequest'];


// Correct base URL for the backend API, including the /api/v1 context path.
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Generic fetch wrapper to handle common headers and error responses,
// and to provide type safety for responses.
async function fetchApi<TResponse, TBody = undefined>(
  path: string,
  method: string,
  body?: TBody,
  customHeaders?: HeadersInit // Corrected syntax for customHeaders
): Promise<TResponse> {
  // Use the correct key for the JWT token
  const token = storage.getItem<string>('jwtToken');

  // If token is null, authHeader will be an empty object
  // If token is a string, authHeader will contain the Authorization header
  const authHeader = token ? { 'Authorization': `Bearer ${token}` } : {};

  const options: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // Build headers using the Headers API for robustness
  const finalHeaders = new Headers(options.headers);
  Object.entries(authHeader).forEach(([key, value]) => finalHeaders.set(key, value));
  if (customHeaders) {
    if (customHeaders instanceof Headers) {
      customHeaders.forEach((value, key) => finalHeaders.set(key, value));
    } else if (Array.isArray(customHeaders)) {
      customHeaders.forEach(([key, value]) => finalHeaders.set(key, value));
    } else { // Assume Record<string, string>
      Object.entries(customHeaders).forEach(([key, value]) => finalHeaders.set(key, value));
    }
  }
  options.headers = finalHeaders;

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

// --- Profile API Calls ---
export const getUserProfile = async (): Promise<UserProfileResponseBody> => {
  // Path for getUserProfile is /profile
  return fetchApi<UserProfileResponseBody>('/profile', 'GET');
};

export const updateUserProfile = async (
  data: UpdateUserProfileRequestBody
): Promise<UpdateUserProfileResponseBody> => {
  // Path for updateUserProfile is /profile
  return fetchApi<UpdateUserProfileResponseBody, UpdateUserProfileRequestBody>('/profile', 'PUT', data);
};

export const changePassword = async (data: ChangePasswordRequestBody): Promise<void> => {
  // Path for changePassword is /profile/password
  // The server returns 200 OK with no content for success, so TResponse is void
  return fetchApi<void, ChangePasswordRequestBody>('/profile/password', 'PATCH', data);
};
