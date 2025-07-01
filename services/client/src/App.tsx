import './App.css'
import { Routes, Route, Link } from 'react-router-dom';
import HomePage from '@pages/Home';
import AboutPage from '@pages/About';
import SignUpPage from '@pages/SignUpPage';
import SignInPage from '@pages/SignInPage';
import ProtectedRoute from '@components/auth/ProtectedRoute';
import ProfilePage from '@pages/ProfilePage'; // <-- Add this import
import { useAuth } from './context/AuthContext';
import PdfUploadPage from '@pages/PdfUploadPage';

function Dashboard() {
    return (
        <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center">
            <h1 className="text-4xl font-bold text-indigo-400">Welcome to your Dashboard!</h1>
            <p className="mt-4">You have successfully logged in.</p>
        </div>
    );
}
function App() {
    const { isAuthenticated, logout } = useAuth();

    return (
        <div>
            {/* Basic Navigation (optional, for testing) */}
            <nav className="bg-gray-800 p-4 text-white">
                <ul className="flex space-x-4">
                    <li>
                        <Link to="/" className="hover:text-gray-300">Home</Link>
                    </li>
                    <li>
                        <Link to="/about" className="hover:text-gray-300">About</Link>
                    </li>
                    {isAuthenticated && (
                        <li>
                            <Link to="/profile" className="hover:text-gray-300">Profile</Link>
                        </li>
                    )}
                    {!isAuthenticated ? (
                        <>
                            <li>
                                <Link to="/signup" className="hover:text-gray-300">Sign Up</Link>
                            </li>
                            <li>
                                <Link to="/login" className="hover:text-gray-300">Login</Link>
                            </li>
                        </>
                    ) : (
                        <>
                            <li>
                                <Link to="/dashboard" className="hover:text-gray-300">Dashboard</Link>
                            </li>
                            <li>
                                <Link to="/pdf-upload" className="hover:text-gray-300">PDF Upload</Link>
                            </li>
                            <li>
                                <Link to="/" onClick={logout} className="hover:text-gray-300">Logout</Link>
                            </li>
                        </>
                    )}
                </ul>
            </nav>
            {/* Page Content */}
            <div className="p-6">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/about" element={<AboutPage />} />
                    <Route path="/signup" element={<SignUpPage />} />
                    <Route path="/login" element={<SignInPage />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/profile" element={<ProfilePage />} />
                        <Route path="/pdf-upload" element={<PdfUploadPage />} /> {/* <-- Add this line */}
                    </Route>
                </Routes>
            </div>
        </div>
    );
}

export default App;
