// src/ProtectedRoute.tsx
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ProtectedRoute = () => {
    const { isAuthenticated } = useAuth();
    const location = useLocation();

    if (!isAuthenticated) {
        // Redirect them to the /signin page, but save the current location they were
        // trying to go to. This allows us to send them back to that page after they login.
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // If authenticated, render the child route content
    return <Outlet />;
};

export default ProtectedRoute;
