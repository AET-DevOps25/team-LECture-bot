import { StrictMode } from 'react';

import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.tsx';
import './index.css';
import { AuthProvider } from './context/AuthContext.tsx';
import { loadConfig } from './config.ts';

loadConfig().then(() => {
    createRoot(document.getElementById('root')!).render(
        <StrictMode>
            <BrowserRouter>
              <AuthProvider>
                <App />
              </AuthProvider>
            </BrowserRouter>
        </StrictMode>,
    );
});
