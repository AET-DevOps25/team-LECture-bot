import React, { createContext, useState, useContext } from 'react';
import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import storage from '../utils/storage'; // <-- IMPORT your new utility

interface AuthContextType {
    token: string | null;
    isAuthenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
}

// Provide a non-null default value to avoid checking for undefined.
const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [token, setTokenState] = useState<string | null>(storage.getItem<string>('jwtToken'));
    const navigate = useNavigate();

    const login = (newToken: string) => {
        storage.setItem('jwtToken', newToken);
        setTokenState(newToken);
        navigate('/dashboard'); // Navigate to dashboard on successful login
    };

    const logout = () => {
        storage.removeItem('jwtToken');
        setTokenState(null);
        navigate('/login');
    };

    const value = {
        token,
        isAuthenticated: !!token,
        login,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
