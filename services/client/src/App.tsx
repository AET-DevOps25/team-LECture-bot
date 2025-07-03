import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProfilePage from '@pages/ProfilePage';
import CourseSpacePage from './pages/CourseSpacePage';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
    const auth = useAuth();
    const location = useLocation();

    // Do not render Navbar on login/register pages
    const showNavbar = auth.isAuthenticated && location.pathname !== '/login' && location.pathname !== '/register';

    return (
        <div className="min-h-screen bg-gray-50">
            {showNavbar && <Navbar />}
            <main className="p-4 sm:p-6">
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />

                    {/* Protected Routes */}
                    <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
                    <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
                    <Route path="/coursespaces/:courseId" element={<ProtectedRoute><CourseSpacePage /></ProtectedRoute>} />

                    {/* Default route */}
                    <Route
                        path="*"
                        element={
                            <Navigate to={auth.isAuthenticated ? "/dashboard" : "/login"} replace />
                        }
                    />
                </Routes>
            </main>
        </div>
    );
}

export default App;
