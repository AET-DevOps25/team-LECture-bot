import React, { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { apiClient } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import type { components } from '../shared/api/generated/api';

type UpdateUserProfileRequest = components['schemas']['UpdateUserProfileRequest'];
type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];

const ProfilePage: React.FC = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const { logout } = useAuth();

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const { data, error: apiError } = await apiClient.GET('/profile');
                if (apiError) throw new Error('Failed to fetch profile');
                if (data) {
                    setName(data.name || '');
                    setEmail(data.email || '');
                }
            } catch (err) {
                setError((err as Error).message);
            } finally {
                setLoading(false);
            }
        };
        fetchProfile();
    }, []);

    const handleProfileUpdate = async (e: FormEvent) => {
        e.preventDefault();
        const body: UpdateUserProfileRequest = { name, email };
        try {
            const { data, error: apiError } = await apiClient.PUT('/profile', { body });
            if (apiError) throw new Error('Failed to update profile');
            if (data) {
                alert(data.message || 'Profile updated!');
                if (data.require_reauth) {
                    alert('Your email was changed. Please log in again.');
                    logout();
                }
            }
        } catch (err) {
            alert((err as Error).message);
        }
    };

    const handlePasswordChange = async (e: FormEvent) => {
        e.preventDefault();
        if (newPassword !== confirmPassword) {
            alert('New passwords do not match.');
            return;
        }
        const body: ChangePasswordRequest = {
            old_password: currentPassword,
            new_password: newPassword,
        };
        try {
            const { error: apiError } = await apiClient.PATCH('/profile/password', { body });
            if (apiError) throw new Error('Failed to change password. Check current password.');
            alert('Password changed successfully!');
            setCurrentPassword('');
            setNewPassword('');
            setConfirmPassword('');
        } catch (err) {
            alert((err as Error).message);
        }
    };

    if (loading) return <div>Loading profile...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">User Profile</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div>
                    <h2 className="text-xl font-semibold mb-2">Update Information</h2>
                    <form onSubmit={handleProfileUpdate} className="space-y-4">
                        <div>
                            <label htmlFor="name" className="block text-sm font-medium text-gray-700">Name</label>
                            <input
                                id="name"
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            />
                        </div>
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            />
                        </div>
                        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">Update Profile</button>
                    </form>
                </div>
                <div>
                    <h2 className="text-xl font-semibold mb-2">Change Password</h2>
                    <form onSubmit={handlePasswordChange} className="space-y-4">
                        <div>
                            <label htmlFor="currentPassword"
                                   className="block text-sm font-medium text-gray-700">Current Password</label>
                            <input
                                id="currentPassword"
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            />
                        </div>
                        <div>
                            <label htmlFor="newPassword"
                                   className="block text-sm font-medium text-gray-700">New Password</label>
                            <input
                                id="newPassword"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            />
                        </div>
                        <div>
                            <label htmlFor="confirmPassword"
                                   className="block text-sm font-medium text-gray-700">Confirm New Password</label>
                            <input
                                id="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                            />
                        </div>
                        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">Change Password</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
