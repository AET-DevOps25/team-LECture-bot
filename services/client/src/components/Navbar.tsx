import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar: React.FC = () => {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="bg-gray-800 text-white p-4 mb-4">
            <div className="container mx-auto flex justify-between items-center">
                <Link to="/dashboard" className="text-xl font-bold">LECture-bot</Link>
                <div>
                    <Link to="/dashboard" className="px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">Dashboard</Link>
                    <Link to="/profile" className="px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">Profile</Link>
                    <button onClick={handleLogout} className="ml-4 px-3 py-2 rounded-md text-sm font-medium bg-red-600 hover:bg-red-700">
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;