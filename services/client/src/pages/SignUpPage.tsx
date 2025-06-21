import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import for navigation
import { getConfig } from '../config';



function SignUpPage() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [isLoading, setIsLoading] = useState(false); // For loading state
    const [submitError, setSubmitError] = useState<string | null>(null); // For backend error messages

    const navigate = useNavigate(); // Hook for navigation

    const validateForm = (): boolean => {
        const newErrors: { [key: string]: string } = {};
        setSubmitError(null); // Clear previous submit errors

        if (!name.trim()) newErrors.name = 'Name is required';
        if (!email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            newErrors.email = 'Email format is invalid';
        }
        if (!password) {
            newErrors.password = 'Password is required';
        } else if (password.length < 8) {
            newErrors.password = 'Password must be at least 8 characters';
        }
        if (!confirmPassword) { // Check for empty confirmPassword first
            newErrors.confirmPassword = 'Confirm Password is required';
        } else if (confirmPassword !== password) {
            newErrors.confirmPassword = 'Passwords do not match';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // It's often better to validate on submit or on change with debounce,
    // but onBlur is also a valid strategy.
    const handleBlur = (field: string) => {
        // Trigger validation for the specific field or the whole form
        // For simplicity, let's re-validate the whole form on blur,
        // but you could make this more targeted.
        const currentErrors = { ...errors };

        // Clear previous error for the field being blurred
        delete currentErrors[field];
        // Optionally clear submit error when user starts correcting fields
        setSubmitError(null);


        // Perform validation for the specific field
        if (field === 'name' && !name.trim()) currentErrors.name = 'Name is required';
        if (field === 'email') {
            if (!email.trim()) {
                currentErrors.email = 'Email is required';
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                currentErrors.email = 'Email format is invalid';
            }
        }
        if (field === 'password') {
            if (!password) {
                currentErrors.password = 'Password is required';
            } else if (password.length < 8) {
                currentErrors.password = 'Password must be at least 8 characters';
            }
        }
        if (field === 'confirmPassword') {
            if (!confirmPassword) {
                currentErrors.confirmPassword = 'Confirm Password is required';
            } else if (password && confirmPassword !== password) { // Also check password if confirmPassword is being validated
                currentErrors.confirmPassword = 'Passwords do not match';
            }
        }
        // If password is changed, re-validate confirmPassword if it's not empty
        if (field === 'password' && confirmPassword && password !== confirmPassword) {
            currentErrors.confirmPassword = 'Passwords do not match';
        }


        setErrors(currentErrors);
    };


    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => { // Make handleSubmit async
        event.preventDefault();
        setSubmitError(null); // Clear previous submit error

        if (validateForm()) {
            setIsLoading(true); // Start loading
            try {

                const { PUBLIC_API_URL } = getConfig(); // Get API base URL from config
                if (!PUBLIC_API_URL) {
                    console.warn("PUBLIC_API_URL is undefined. Using fallback.");
                }
                const API_BASE_URL = PUBLIC_API_URL || 'http://localhost:8080/api'; // Fallback to localhost if not set

                const response = await fetch(`${API_BASE_URL}/auth/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password }), // Correctly sends these three fields
                });

                const responseBodyText = await response.text(); // Get response body as text

                if (response.ok) {
                    // alert(responseBodyText); // Or use a more sophisticated notification
                    // For example, show a success message for a few seconds then redirect
                    setSubmitError(null); // Clear any previous error
                    // TODO: Implement a more user-friendly success message (e.g., a toast notification)
                    alert("Registration successful! Redirecting to login..."); // Placeholder success message
                    navigate('/login'); // Navigate to login page (ensure you have a '/login' route)
                } else {
                    // Handle errors from the backend (e.g., email already exists, server-side validation)
                    // The backend returns plain text error messages.
                    setSubmitError(responseBodyText || 'Registration failed. Please try again.');
                    setErrors({}); // Clear field-specific errors as this is a submit-level error
                }
            } catch (error) {
                // Handle network errors or other issues with the fetch call
                console.error('Registration API error:', error);
                setSubmitError('An unexpected error occurred. Please check your connection and try again.');
                setErrors({});
            } finally {
                setIsLoading(false); // Stop loading
            }
        }
    };

    // Disable submit button if there are any client-side validation errors OR if it's loading
    const isSubmitDisabled = Object.values(errors).some(error => error) || isLoading;


    return (
        <div className="min-h-screen bg-gray-900 flex items-center justify-center">
            <div className="bg-gray-800 p-8 rounded-lg shadow-2xl w-full max-w-md text-white">
                <h2 className="text-3xl font-bold mb-6 text-center text-indigo-400">Sign Up</h2>
                {/* Display general submission error here */}
                {submitError && (
                    <div className="mb-4 p-3 bg-red-500 text-white rounded-md text-sm">
                        {submitError}
                    </div>
                )}
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Name Field */}
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium text-gray-300">Name</label>
                        <input
                            id="name" name="name" type="text" value={name}
                            onChange={(e) => setName(e.target.value)}
                            onBlur={() => handleBlur('name')}
                            className={`mt-1 p-3 block w-full bg-gray-700 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white placeholder-gray-400 ${errors.name ? 'border-red-500' : 'border-gray-600'}`}
                        />
                        {errors.name && <p className="mt-1 text-sm text-red-500">{errors.name}</p>}
                    </div>
                    {/* Email Field */}
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-300">Email</label>
                        <input
                            id="email" name="email" type="email" value={email}
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
                            id="password" name="password" type="password" value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onBlur={() => handleBlur('password')}
                            className={`mt-1 p-3 block w-full bg-gray-700 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white placeholder-gray-400 ${errors.password ? 'border-red-500' : 'border-gray-600'}`}
                        />
                        {errors.password && <p className="mt-1 text-sm text-red-500">{errors.password}</p>}
                    </div>
                    {/* Confirm Password Field */}
                    <div>
                        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-300">Confirm Password</label>
                        <input
                            id="confirmPassword" name="confirmPassword" type="password" value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            onBlur={() => handleBlur('confirmPassword')}
                            className={`mt-1 p-3 block w-full bg-gray-700 border rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white placeholder-gray-400 ${errors.confirmPassword ? 'border-red-500' : 'border-gray-600'}`}
                        />
                        {errors.confirmPassword && <p className="mt-1 text-sm text-red-500">{errors.confirmPassword}</p>}
                    </div>
                    <button
                        type="submit"
                        disabled={isSubmitDisabled}
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white p-3 rounded-md font-semibold transition-colors duration-200 disabled:bg-gray-500 disabled:cursor-not-allowed"
                    >
                        {isLoading ? 'Signing Up...' : 'Sign Up'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default SignUpPage;
