import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from './LoginPage';

// Mock useAuth
let mockLogin: jest.Mock;
jest.mock('../context/AuthContext', () => {
    mockLogin = jest.fn();
    return {
        useAuth: () => ({ login: mockLogin }),
        __esModule: true,
    };
});

// Mock apiClient
let mockPost: jest.Mock;
jest.mock('../api/apiClient', () => {
    mockPost = jest.fn();
    return {
        apiClient: { POST: mockPost },
        __esModule: true,
    };
});

// Mock Link to avoid react-router-dom dependency
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    Link: ({ children, ...props }: any) => <a {...props}>{children}</a>,
}));

describe('LoginPage', () => {
    beforeEach(() => {
        mockLogin.mockClear();
        mockPost.mockClear();
    });

    it('renders login form', () => {
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );
        expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
        expect(screen.getByText(/register here/i)).toBeInTheDocument();
    });

    it('allows user to type email and password', () => {
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );
        const emailInput = screen.getByLabelText(/email/i);
        const passwordInput = screen.getByLabelText(/password/i);

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'secret' } });

        expect(emailInput).toHaveValue('test@example.com');
        expect(passwordInput).toHaveValue('secret');
    });

    it('successful login calls login and does not show error', async () => {
        mockPost.mockResolvedValue({ data: { token: 'abc123' }, error: null });
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );
        fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@mail.com' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'pass' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(mockPost).toHaveBeenCalledWith('/auth/login', { body: { email: 'user@mail.com', password: 'pass' } });
            expect(mockLogin).toHaveBeenCalledWith('abc123');
            expect(screen.queryByText(/invalid credentials/i)).not.toBeInTheDocument();
        });
    });

    it('shows error on invalid credentials', async () => {
        mockPost.mockResolvedValue({ data: null, error: { token: 'Invalid credentials' } });
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );
        fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'bad@mail.com' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'wrong' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(screen.getByText(/invalid credentials/i)).toBeInTheDocument();
            expect(mockLogin).not.toHaveBeenCalled();
        });
    });

    it('shows error if no token received', async () => {
        mockPost.mockResolvedValue({ data: {}, error: null });
        render(
            <MemoryRouter>
                <LoginPage />
            </MemoryRouter>
        );
        fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@mail.com' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'pass' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(screen.getByText(/no token received/i)).toBeInTheDocument();
            expect(mockLogin).not.toHaveBeenCalled();
        });
    });
});