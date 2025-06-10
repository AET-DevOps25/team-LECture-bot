import axios from 'axios';
import storage from '../utils/storage'; // Import our storage utility

// Create an Axios instance
const apiClient = axios.create({
    // Your base URL for backend API calls (currently hardcoded for local development)
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Use an interceptor to inject the token into every request
apiClient.interceptors.request.use(
    (config) => {
        // Retrieve the token from localStorage
        const token = storage.getItem<string>('jwtToken');

        // If the token exists, add it to the Authorization header
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        // Do something with request error
        return Promise.reject(error);
    }
);

export default apiClient;
