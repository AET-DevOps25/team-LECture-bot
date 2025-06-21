import React, { useState, useEffect } from "react";
import { getConfig } from '../config';
import storage from '../utils/storage';
import { useNavigate } from "react-router-dom";

export default function ProfilePage() {
  const { PUBLIC_API_URL } = getConfig();
  const API_BASE_URL = PUBLIC_API_URL || "http://localhost:8080/api";

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  // Validation state
  const [profileErrors, setProfileErrors] = useState<{ name?: string; email?: string; form?: string }>({});
  const [passwordErrors, setPasswordErrors] = useState<{ currentPassword?: string; newPassword?: string; confirmPassword?: string; form?: string }>({});
  const [profileSubmitting, setProfileSubmitting] = useState(false);
  const [passwordSubmitting, setPasswordSubmitting] = useState(false);
  const [profileSuccess, setProfileSuccess] = useState("");
  const [passwordSuccess, setPasswordSuccess] = useState("");

  // Helper to get JWT token from localStorage (correct key: jwtToken)
  function getToken() {
    return storage.getItem<string>('jwtToken') || null;
  }

  useEffect(() => {
    // Fetch current user profile from backend
    async function fetchProfile() {
      setLoading(true);
      setError("");
      try {
        const token = getToken();
        console.log(`Bearer ${token}`)
        if (!token) throw new Error("Not authenticated");
        // Fetch user profile
        // Use API_BASE_URL from config
        const res = await fetch(`${API_BASE_URL}/users/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
        });
        if (!res.ok) throw new Error("Failed to fetch profile");
        const data = await res.json();
        setName(data.name || "");
        setEmail(data.email || "");
      } catch (err: any) {
        setError(err.message || "Error loading profile");
      } finally {
        setLoading(false);
      }
    }
    fetchProfile();
    // eslint-disable-next-line
  }, []);

  // Profile validation
  function validateProfile() {
    const errors: { name?: string; email?: string } = {};
    if (!name.trim()) errors.name = "Name is required";
    if (!email.trim()) errors.email = "Email is required";
    else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) errors.email = "Invalid email address";
    setProfileErrors(errors);
    return Object.keys(errors).length === 0;
  }

  // Password validation
  function validatePassword() {
    const errors: { currentPassword?: string; newPassword?: string; confirmPassword?: string; form?: string } = {};
    if (!currentPassword) errors.currentPassword = "Current password is required";
    if (!newPassword) errors.newPassword = "New password is required";
    else if (newPassword.length < 8) errors.newPassword = "New password must be at least 8 characters";
    if (!confirmPassword) errors.confirmPassword = "Please confirm new password";
    else if (newPassword !== confirmPassword) errors.confirmPassword = "Passwords do not match";
    setPasswordErrors(errors);
    return Object.keys(errors).length === 0;
  }

  // Profile form submit
  async function handleProfileSubmit(e: React.FormEvent) {
    e.preventDefault();
    setProfileSuccess("");
    if (!validateProfile()) return;
    setProfileSubmitting(true);
    setProfileErrors({});
    try {
      const token = getToken();
      const res = await fetch(`${API_BASE_URL}/users/me`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({ name, email }),
      });
      if (!res.ok) {
        let msg = "Failed to update profile";
        try {
          const errData = await res.json();
          msg = errData.message || msg;
        } catch {}
        throw new Error(msg);
      }
      const data = await res.json();
      if (data.requireReauth) {
        storage.removeItem("jwtToken");
        alert("Your email was changed. Please log in again.");
        // Use navigate from react-router-dom to redirect to login
        navigate("/login", { replace: true });
        return;
      }
      // Update local state with new values (in case backend returns updated user)
      setProfileSuccess("Profile updated successfully");
      const updated = await res.json();
      setName(updated.name); setEmail(updated.email);
    } catch (err: any) {
      setProfileErrors({ form: err.message || "Error updating profile" });
    } finally {
      setProfileSubmitting(false);
    }
  }

  // Password form submit
  async function handlePasswordSubmit(e: React.FormEvent) {
    e.preventDefault();
    setPasswordSuccess("");
    if (!validatePassword()) return;
    setPasswordSubmitting(true);
    setPasswordErrors({});
    try {
      const token = getToken();
      const res = await fetch(`${API_BASE_URL}/users/me/change-password`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({ currentPassword, newPassword }),
      });
      if (!res.ok) {
        let msg = "Failed to change password";
        try {
          const errData = await res.json();
          msg = errData.message || msg;
        } catch {}
        throw new Error(msg);
      }
      setPasswordSuccess("Password changed successfully");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err: any) {
      setPasswordErrors({ form: err.message || "Error changing password" });
    } finally {
      setPasswordSubmitting(false);
    }
  }

  if (loading) {
    return <div className="text-center mt-10">Loading profile...</div>;
  }
  if (error) {
    return <div className="text-center mt-10 text-red-600">{error}</div>;
  }

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
      <h1 className="text-2xl font-bold mb-6">Manage Profile</h1>
      <div className="mb-8">
        <h2 className="text-lg font-semibold mb-2">Profile Information</h2>
        <form className="space-y-4" autoComplete="off" onSubmit={handleProfileSubmit}>
          <div>
            <label className="block text-sm font-medium mb-1">Name</label>
            <input
              className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400 ${profileErrors.name ? 'border-red-500' : ''}`}
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              onBlur={validateProfile}
              disabled={profileSubmitting}
            />
            {profileErrors.name && <div className="text-red-600 text-sm mt-1">{profileErrors.name}</div>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <input
              className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400 ${profileErrors.email ? 'border-red-500' : ''}`}
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              onBlur={validateProfile}
              disabled={profileSubmitting}
            />
            {profileErrors.email && <div className="text-red-600 text-sm mt-1">{profileErrors.email}</div>}
          </div>
          {profileErrors.form && <div className="text-red-600 text-sm mt-1">{profileErrors.form}</div>}
          {profileSuccess && <div className="text-green-600 text-sm mt-1">{profileSuccess}</div>}
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
            disabled={profileSubmitting || Object.keys(profileErrors).length > 0}
          >
            {profileSubmitting ? "Updating..." : "Update Profile"}
          </button>
        </form>
      </div>
      <div>
        <h2 className="text-lg font-semibold mb-2">Change Password</h2>
        <form className="space-y-4" autoComplete="off" onSubmit={handlePasswordSubmit}>
          <div>
            <label className="block text-sm font-medium mb-1">Current Password</label>
            <input
              className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400 ${passwordErrors.currentPassword ? 'border-red-500' : ''}`}
              type="password"
              value={currentPassword}
              onChange={e => setCurrentPassword(e.target.value)}
              onBlur={validatePassword}
              disabled={passwordSubmitting}
            />
            {passwordErrors.currentPassword && <div className="text-red-600 text-sm mt-1">{passwordErrors.currentPassword}</div>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New Password</label>
            <input
              className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400 ${passwordErrors.newPassword ? 'border-red-500' : ''}`}
              type="password"
              value={newPassword}
              onChange={e => setNewPassword(e.target.value)}
              onBlur={validatePassword}
              disabled={passwordSubmitting}
            />
            {passwordErrors.newPassword && <div className="text-red-600 text-sm mt-1">{passwordErrors.newPassword}</div>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Confirm New Password</label>
            <input
              className={`w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400 ${passwordErrors.confirmPassword ? 'border-red-500' : ''}`}
              type="password"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
              onBlur={validatePassword}
              disabled={passwordSubmitting}
            />
            {passwordErrors.confirmPassword && <div className="text-red-600 text-sm mt-1">{passwordErrors.confirmPassword}</div>}
          </div>
          {passwordErrors.form && <div className="text-red-600 text-sm mt-1">{passwordErrors.form}</div>}
          {passwordSuccess && <div className="text-green-600 text-sm mt-1">{passwordSuccess}</div>}
          <button
            type="submit"
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
            disabled={passwordSubmitting || Object.keys(passwordErrors).length > 0}
          >
            {passwordSubmitting ? "Changing..." : "Change Password"}
          </button>
        </form>
      </div>
    </div>
  );
}
