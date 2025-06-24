import React, { useState, useEffect, type FormEvent } from 'react'; // Add 'type' to FormEvent
import { useAuth } from '../context/AuthContext';
import { getUserProfile, updateUserProfile, changePassword } from '../api/apiClient';
import type { components } from '../shared/api/generated/api';
import { useNavigate } from 'react-router-dom';

type UserProfile = components['schemas']['UserProfile'];
type UpdateUserProfileRequest = components['schemas']['UpdateUserProfileRequest'];
type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];

const ProfilePage: React.FC = () => {
    const { isAuthenticated } = useAuth(); // Only isAuthenticated is needed here
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [editMode, setEditMode] = useState(false);
    const [newName, setNewName] = useState('');
    const [newEmail, setNewEmail] = useState('');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmNewPassword, setConfirmNewPassword] = useState('');
    const [passwordChangeError, setPasswordChangeError] = useState<string | null>(null);
    const [passwordChangeSuccess, setPasswordChangeSuccess] = useState<string | null>(null);

    const navigate = useNavigate(); // Import useNavigate for navigation

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            setError('Not authenticated. Please sign in.');
            return;
        }

        const fetchUserProfile = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await getUserProfile(); // Use the API client function
                setProfile(data);
                setNewName(data.name || ''); // Initialize form fields with current profile data
                setNewEmail(data.email || '');
            } catch (err: any) {
                console.error('Failed to fetch profile:', err);
                setError(err.message || 'Failed to fetch profile. Please check your connection.');
            } finally {
                setLoading(false);
            }
        };

        fetchUserProfile();
    }, [isAuthenticated]); // Depend on isAuthenticated

    const handleUpdateProfile = async (e: FormEvent) => {
        e.preventDefault();
        setError(null); // Clear general error
        if (!profile) return; // Should not happen if profile is loaded

        const requestBody: UpdateUserProfileRequest = {
            name: newName,
            email: newEmail,
        };

        try {
            const data = await updateUserProfile(requestBody);

            if (data.requireReauth) {
                localStorage.removeItem("jwtToken");
                alert("Your email was changed. Please log in again.");
                navigate('/login', { replace: true });
                return;
            }

            if (data && 'userProfile' in data && data.userProfile) {
                setProfile(data.userProfile);
                setEditMode(false);
                alert('Profile updated successfully!');
            } else {
                setProfile(null); // fallback, should not happen if backend is correct
                setEditMode(false);
                setError('Failed to update profile: No profile data returned.');
            }
        } catch (err: any) {
            console.error('Failed to update profile:', err);
            setError(err.message || 'Failed to update profile.'); // Display error
        }
    };

    const handleChangePassword = async (e: FormEvent) => {
        e.preventDefault();
        setPasswordChangeError(null); // Clear previous password change error
        setPasswordChangeSuccess(null); // Clear previous password change success

        // Client-side validation for password change
        if (newPassword !== confirmNewPassword) {
            setPasswordChangeError('New passwords do not match.');
            return;
        }
        if (newPassword.length < 8) {
            setPasswordChangeError('New password must be at least 8 characters long.');
            return;
        }

        const requestBody: ChangePasswordRequest = {
            oldPassword: oldPassword,
            newPassword: newPassword,
        };

        try {
            await changePassword(requestBody); // Use API client function
            setPasswordChangeSuccess('Password changed successfully!'); // Success feedback
            // Clear form fields
            setOldPassword('');
            setNewPassword('');
            setConfirmNewPassword('');
        } catch (err: any) {
            console.error('Failed to change password:', err);
            setPasswordChangeError(err.message || 'Failed to change password.'); // Display error
        }
    };

    if (loading) {
        return <div className="min-h-screen bg-gray-900 flex items-center justify-center text-white">Loading profile...</div>;
    }

    if (error) {
        return <div className="min-h-screen bg-gray-900 flex items-center justify-center text-red-500">Error: {error}</div>;
    }

    // If profile is null after loading, it means there was an error or no data
    if (!profile) {
        return <div className="min-h-screen bg-gray-900 flex items-center justify-center text-white">No profile data available.</div>;
    }

    return (
        <div className="min-h-screen bg-gray-900 flex items-center justify-center p-4">
            <div className="bg-gray-800 p-8 rounded-lg shadow-2xl w-full max-w-md text-white">
                <h2 className="text-3xl font-bold mb-6 text-center text-indigo-400">User Profile</h2>
                {error && <div className="mb-4 p-3 bg-red-500 text-white rounded-md text-sm">{error}</div>}

                <div className="space-y-4 mb-8">
                    {editMode ? (
                        <form onSubmit={handleUpdateProfile} className="space-y-4">
                            <div>
                                <label htmlFor="name" className="block text-sm font-medium text-gray-300">Name</label>
                                <input
                                    type="text"
                                    id="name"
                                    value={newName}
                                    onChange={(e) => setNewName(e.target.value)}
                                    className="mt-1 p-3 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white"
                                />
                            </div>
                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-300">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    value={newEmail}
                                    onChange={(e) => setNewEmail(e.target.value)}
                                    className="mt-1 p-3 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white"
                                />
                            </div>
                            <div className="flex justify-end space-x-2">
                                <button
                                    type="button"
                                    onClick={() => setEditMode(false)}
                                    className="px-4 py-2 bg-gray-600 hover:bg-gray-700 rounded-md font-semibold transition-colors duration-200"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 rounded-md font-semibold transition-colors duration-200"
                                >
                                    Save Changes
                                </button>
                            </div>
                        </form>
                    ) : (
                        <>
                            <p><strong>Name:</strong> {profile.name}</p>
                            <p><strong>Email:</strong> {profile.email}</p>
                            <button
                                onClick={() => setEditMode(true)}
                                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 rounded-md font-semibold transition-colors duration-200"
                            >
                                Edit Profile
                            </button>
                        </>
                    )}
                </div>

                <h3 className="text-2xl font-bold mb-4 text-indigo-400">Change Password</h3>
                {passwordChangeError && <div className="mb-4 p-3 bg-red-500 text-white rounded-md text-sm">{passwordChangeError}</div>}
                {passwordChangeSuccess && <div className="mb-4 p-3 bg-green-500 text-white rounded-md text-sm">{passwordChangeSuccess}</div>}
                <form onSubmit={handleChangePassword} className="space-y-4">
                    <div>
                        <label htmlFor="oldPassword" className="block text-sm font-medium text-gray-300">Old Password</label>
                        <input type="password" id="oldPassword" value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} className="mt-1 p-3 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white" />
                    </div>
                    <div>
                        <label htmlFor="newPassword" className="block text-sm font-medium text-gray-300">New Password</label>
                        <input type="password" id="newPassword" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} className="mt-1 p-3 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white" />
                    </div>
                    <div>
                        <label htmlFor="confirmNewPassword" className="block text-sm font-medium text-gray-300">Confirm New Password</label>
                        <input type="password" id="confirmNewPassword" value={confirmNewPassword} onChange={(e) => setConfirmNewPassword(e.target.value)} className="mt-1 p-3 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 text-white" />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white p-3 rounded-md font-semibold transition-colors duration-200"
                    >
                        Change Password
                    </button>
                </form>
            </div>
        </div>
    );
}

export default ProfilePage;
