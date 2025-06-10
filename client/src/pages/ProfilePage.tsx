import React, { useState, useEffect } from "react";

export default function ProfilePage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Helper to get JWT token from localStorage (or another storage)
  function getToken() {
    return localStorage.getItem('token');
  }

  useEffect(() => {
    // Fetch current user profile from backend
    async function fetchProfile() {
      setLoading(true);
      setError("");
      try {
        const token = getToken();
        const res = await fetch("/api/users/me", {
          headers: {
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
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
  }, []);

  // TODO: Add form submission handlers for profile and password update

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
        <form className="space-y-4" autoComplete="off">
          <div>
            <label className="block text-sm font-medium mb-1">Name</label>
            <input
              className="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400"
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <input
              className="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
          >
            Update Profile
          </button>
        </form>
      </div>
      <div>
        <h2 className="text-lg font-semibold mb-2">Change Password</h2>
        <form className="space-y-4" autoComplete="off">
          <div>
            <label className="block text-sm font-medium mb-1">Current Password</label>
            <input
              className="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400"
              type="password"
              value={currentPassword}
              onChange={e => setCurrentPassword(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New Password</label>
            <input
              className="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400"
              type="password"
              value={newPassword}
              onChange={e => setNewPassword(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Confirm New Password</label>
            <input
              className="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-400"
              type="password"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
            />
          </div>
          <button
            type="submit"
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
          >
            Change Password
          </button>
        </form>
      </div>
    </div>
  );
}
