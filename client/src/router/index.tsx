import { createBrowserRouter } from 'react-router-dom';
import App from '@src/App';
import Home from "@pages/Home";
import About from '@pages/About';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                path: '',
                element: <Home />,
            },
            {
                path: 'about',
                element: <About />,
            },
        ],
    },
]);
