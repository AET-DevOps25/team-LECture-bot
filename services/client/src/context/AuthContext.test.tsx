import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { AuthProvider, useAuth } from "./AuthContext";
import storage from "../utils/storage";
import { MemoryRouter } from "react-router-dom";

// Mock storage and useNavigate
jest.mock("../utils/storage", () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
}));
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

function TestComponent() {
  const { token, isAuthenticated, login, logout } = useAuth();
  return (
    <div>
      <span>token:{token}</span>
      <span>auth:{isAuthenticated ? "yes" : "no"}</span>
      <button onClick={() => login("abc")}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

describe("AuthContext", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (storage.getItem as jest.Mock).mockReturnValue(null);
  });

  it("provides default values", () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      </MemoryRouter>
    );
    expect(screen.getByText("token:")).toBeInTheDocument();
    expect(screen.getByText("auth:no")).toBeInTheDocument();
  });

  it("login sets token, storage, and navigates", async () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      </MemoryRouter>
    );
    screen.getByText("Login").click();
    expect(storage.setItem).toHaveBeenCalledWith("jwtToken", "abc");
    await waitFor(() =>
      expect(screen.getByText((content) => content.includes("token:") && content.includes("abc"))).toBeInTheDocument()
    );
    await waitFor(() =>
      expect(screen.getByText((content) => content.includes("auth:") && content.includes("yes"))).toBeInTheDocument()
    );
    expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
  });

  it("logout clears token, storage, and navigates", async () => {
    (storage.getItem as jest.Mock).mockReturnValue("abc");
    render(
      <MemoryRouter>
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      </MemoryRouter>
    );
    screen.getByText("Logout").click();
    expect(storage.removeItem).toHaveBeenCalledWith("jwtToken");
    await waitFor(() =>
      expect(
        screen.getByText(
          (content) => content.includes("token:") && !content.includes("abc")
        )
      ).toBeInTheDocument()
    );
    await waitFor(() =>
      expect(
        screen.getByText(
          (content) => content.includes("auth:") && content.includes("no")
        )
      ).toBeInTheDocument()
    );
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("throws if useAuth is used outside provider", () => {
    // Suppress error boundary output
    const spy = jest.spyOn(console, "error").mockImplementation(() => {});
    expect(() => render(<TestComponent />)).toThrow(
      /useAuth must be used within an AuthProvider/
    );
    spy.mockRestore();
  });
});