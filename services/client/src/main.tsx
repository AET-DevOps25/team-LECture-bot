import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import App from './App';
import './index.css'
import { CourseSpaceProvider } from './context/CourseSpaceContext';
import { apiClientPromise } from './api/apiClient';

apiClientPromise.then(() => {
    ReactDOM.createRoot(document.getElementById('root')!).render(
        <React.StrictMode>
            <BrowserRouter>
                <AuthProvider>
                    <CourseSpaceProvider>
                        <App />
                    </CourseSpaceProvider>
                </AuthProvider>
            </BrowserRouter>
        </React.StrictMode>,
    )
});
