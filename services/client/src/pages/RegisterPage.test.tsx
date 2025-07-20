import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import RegisterPage from "./RegisterPage";

// Mock apiClient
let mockPost: jest.Mock;
jest.mock("../api/apiClient", () => {
  mockPost = jest.fn();
  return {
    apiClient: { POST: mockPost },
    __esModule: true,
  };
});

// Mock useNavigate
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
  Link: ({ children, ...props }: any) => <a {...props}>{children}</a>,
}));

describe("RegisterPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders registration form", () => {
    render(
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    );
    expect(screen.getByLabelText(/full name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /register/i })).toBeInTheDocument();
    expect(screen.getByText(/already have an account/i)).toBeInTheDocument();
  });

  it("allows user to type name, email, and password", () => {
    render(
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    );
    fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: "Alice" } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "alice@mail.com" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "password123" } });

    expect(screen.getByLabelText(/full name/i)).toHaveValue("Alice");
    expect(screen.getByLabelText(/email/i)).toHaveValue("alice@mail.com");
    expect(screen.getByLabelText(/password/i)).toHaveValue("password123");
  });

  it("shows error if registration fails", async () => {
    mockPost.mockResolvedValue({ data: null, error: "Registration failed" });
    render(
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    );
    fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: "Bob" } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "bob@mail.com" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "password123" } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
      expect(screen.getByText(/registration failed/i)).toBeInTheDocument();
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });

  it("shows success and redirects after registration", async () => {
    mockPost.mockResolvedValue({ data: "Registration successful!", error: null });
    render(
      <MemoryRouter>
        <RegisterPage />
      </MemoryRouter>
    );
    fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: "Carol" } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "carol@mail.com" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "password123" } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
      expect(screen.getByText(/registration successful/i)).toBeInTheDocument();
    });

    // Simulate timeout for redirect
    jest.runAllTimers();
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/login");
    });
  });
});

// Enable fake timers for redirect test
beforeAll(() => {
  jest.useFakeTimers();
});
afterAll(() => {
  jest.useRealTimers();
});