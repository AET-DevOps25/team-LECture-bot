import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import type { components } from '../shared/api/generated/api';

type RegisterRequest = components['schemas']['RegisterRequest'];

const RegisterPage: React.FC = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        const body: RegisterRequest = { name, email, password };

        try {
            const { data, error: apiError, response } = await apiClient.POST('/auth/register', { body });

            if (apiError) {
                // Try to get a meaningful message from the error object or response
                const errorMessage =
                    (apiError as any).detail ||
                    (response && response.status === 400 && "Email already exists") ||
                    'Registration failed. Please try again.';
                throw new Error(errorMessage);
            }

            if (response && response.status === 200) {
                // Always read as text if data is undefined
                let message = "Registration successful! Redirecting to login...";
                try {
                    if (typeof data === 'string') {
                        message = data;
                    } else if (!data && response) {
                        // Always read the response as text
                        message = await response.clone().text();
                    } else if (data && typeof data === 'object' && 'message' in data) {
                        message = (data as any).message;
                    }
                } catch (e) {
                    // fallback to default message
                }
                setSuccess(message);
                setTimeout(() => {
                    navigate('/login');
                }, 2000);
            } else {
                setError("Registration failed. Please try again.");
            }
        } catch (err) {
            setError((err as Error).message);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                <h2 className="text-2xl font-bold text-center">Create an Account</h2>
                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium">Full Name</label>
                        <input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} required className="w-full px-3 py-2 mt-1 border rounded-md" />
                    </div>
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium">Email</label>
                        <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required className="w-full px-3 py-2 mt-1 border rounded-md" />
                    </div>
                    <div>
                        <label htmlFor="password" className="block text-sm font-medium">Password</label>
                        <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={8} className="w-full px-3 py-2 mt-1 border rounded-md" />
                    </div>
                    {error && <p className="text-red-500 text-sm">{error}</p>}
                    {success && <p className="text-green-500 text-sm">{success}</p>}
                    <button type="submit" className="w-full px-4 py-2 font-bold text-white bg-blue-600 rounded-md hover:bg-blue-700">Register</button>
                </form>
                <p className="text-sm text-center">
                    Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Login here</Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;