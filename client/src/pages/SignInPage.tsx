import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getConfig } from '../config';

function SignInPage() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const from = location.state?.from?.pathname || '/dashboard';

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);


    const validateForm = (): boolean => {
        const newErrors: { [key: string]: string } = {};
        setSubmitError(null);

        if (!email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            newErrors.email = 'Email format is invalid';
        }

        if (!password) {
            newErrors.password = 'Password is required';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleBlur = (field: 'email' | 'password') => {
        const currentErrors = { ...errors };
        delete currentErrors[field];
        setSubmitError(null);

        if (field === 'email') {
            if (!email.trim()) {
                currentErrors.email = 'Email is required';
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                currentErrors.email = 'Email format is invalid';
            }
        }

        if (field === 'password' && !password) {
            currentErrors.password = 'Password is required';
        }

        setErrors(currentErrors);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitError(null);

        if (validateForm()) {
            setIsLoading(true);
            try {
                const { PUBLIC_API_URL } = getConfig(); // Get API base URL from config
                if (!PUBLIC_API_URL) {
                    console.warn("PUBLIC_API_URL is undefined. Using fallback.");
                }
                const API_BASE_URL = PUBLIC_API_URL || 'http://localhost:8080/api'; // Fallback to localhost if not set



                const response = await fetch(`${API_BASE_URL}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password }),
                });

                if (response.ok) {
                    const contentType = response.headers.get('content-type');
                    console.log('Content-Type:', contentType);
                    //const responseBody = await response.json();
                    const responseText = await response.text();
                    console.log('Raw response text:', responseText);

                    try {
                        const data = JSON.parse(responseText); // safer than response.json()
                        // Proceed with login...
                        console.log('Login successful, token:', data.token);
                        const token = data.token;
                        login(token);
                        alert("Login successful! Redirecting...");
                        navigate(from, { replace: true });
                    } catch (err) {
                        console.error('Failed to parse JSON:', err);
                        setSubmitError('Server did not return valid JSON. Check console for details.');
                    }

                } else {
                    const responseBodyText = await response.text();
                    setSubmitError(responseBodyText || 'Invalid email or password.');
                    setErrors({});
                }
            } catch (error) {
                console.error('Login API error:', error);
                setSubmitError('An unexpected error occurred. Please check your connection and try again.');
                setErrors({});
            } finally {
                setIsLoading(false);
            }
        }
    };

    const isSubmitDisabled = Object.values(errors).some(error => error) || isLoading;

    return (
        <div className="min-h-screen bg-gray-900 flex items-center justify-center">
            <div className="bg-gray-800 p-8 rounded-lg shadow-2xl w-full max-w-md text-white">
                <h2 className="text-3xl font-bold mb-6 text-center text-indigo-400">Sign In</h2>
                {submitError && (
                    <div className="mb-4 p-3 bg-red-500 text-white rounded-md text-sm">
                        {submitError}
                    </div>
                )}
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Email Field */}
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-300">Email</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            onBlur={() => handleBlur('email')}
                            className={`mt-1 p-3 block w-full bg-gray-700 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white placeholder-gray-400 ${errors.email ? 'border-red-500' : 'border-gray-600'}`}
                        />
                        {errors.email && <p className="mt-1 text-sm text-red-500">{errors.email}</p>}
                    </div>

                    {/* Password Field */}
                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-300">Password</label>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onBlur={() => handleBlur('password')}
                            className={`mt-1 p-3 block w-full bg-gray-700 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white placeholder-gray-400 ${errors.password ? 'border-red-500' : 'border-gray-600'}`}
                        />
                        {errors.password && <p className="mt-1 text-sm text-red-500">{errors.password}</p>}
                    </div>

                    <button
                        type="submit"
                        disabled={isSubmitDisabled}
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white p-3 rounded-md font-semibold transition-colors duration-200 disabled:bg-gray-500 disabled:cursor-not-allowed"
                    >
                        {isLoading ? 'Signing In...' : 'Sign In'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default SignInPage;
