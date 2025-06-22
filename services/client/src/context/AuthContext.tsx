import { createContext, useState, useContext } from 'react';
import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import storage from '../utils/storage'; // <-- IMPORT your new utility



interface AuthContextType {
    isAuthenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    // Use the storage utility to safely get the initial state
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(
        !!storage.getItem<string>('jwtToken') // <-- double negation to convert to correct boolean 
    );
    const navigate = useNavigate();

    const login = (token: string) => {
        // Use the utility to set the item
        storage.setItem('jwtToken', token);
        setIsAuthenticated(true);
    };

    const logout = () => {
        // Use the utility to remove the item
        storage.removeItem('jwtToken');
        setIsAuthenticated(false);
        navigate('/login');
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
