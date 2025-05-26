import './App.css'
import { Link, Outlet } from 'react-router-dom';

function App() {

    return (
        <div className="p-4">
            <nav className="mb-4 space-x-4">
                <Link to="/" className="text-blue-600">Home</Link>
                <Link to="/about" className='text-blue-600'>About</Link>
            </nav>
            <Outlet />
        </div>
    )
}

export default App
