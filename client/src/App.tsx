import './App.css'
import { Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/Home';
import AboutPage from './pages/About';
import SignUpPage from './pages/SignUpPage';

function App() {
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
                    <li>
                        <Link to="/signup" className="hover:text-gray-300">Sign Up</Link>
                    </li>
                    {/* Add a link to Login page later */}
                    {/* <li>
                        <Link to="/login" className="hover:text-gray-300">Login</Link>
                    </li> */}
                </ul>
            </nav>
            {/* Page Content */}
            <div className="p-6">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/about" element={<AboutPage />} />
                    <Route path="/signup" element={<SignUpPage />} />
                    {/* Add Login route later */}
                    {/* <Route path="/login" element={<LoginPage />} /> */}
                </Routes>
            </div>
        </div>
    );
}

export default App;