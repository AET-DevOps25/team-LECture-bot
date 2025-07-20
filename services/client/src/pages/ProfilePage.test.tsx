import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import ProfilePage from "./ProfilePage";

// Mock apiClient and AuthContext
let mockGet: jest.Mock, mockPut: jest.Mock, mockPatch: jest.Mock, mockLogout: jest.Mock;
jest.mock("../api/apiClient", () => {
  mockGet = jest.fn();
  mockPut = jest.fn();
  mockPatch = jest.fn();
  return {
    apiClient: {
      GET: mockGet,
      PUT: mockPut,
      PATCH: mockPatch,
    },
    __esModule: true,
  };
});
jest.mock("../context/AuthContext", () => {
  mockLogout = jest.fn();
  return {
    useAuth: () => ({ logout: mockLogout }),
    __esModule: true,
  };
});

describe("ProfilePage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("shows loading initially", () => {
    mockGet.mockReturnValue(new Promise(() => {})); // never resolves
    render(<ProfilePage />);
    expect(screen.getByText(/loading profile/i)).toBeInTheDocument();
  });

  it("shows error if fetch fails", async () => {
    mockGet.mockResolvedValue({ data: null, error: "fail" });
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByText(/fail/i)).toBeInTheDocument());
  });

  it("renders profile data after fetch", async () => {
    mockGet.mockResolvedValue({ data: { name: "Alice", email: "alice@mail.com" }, error: null });
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Alice")).toBeInTheDocument());
    expect(screen.getByDisplayValue("alice@mail.com")).toBeInTheDocument();
  });

  it("updates profile and shows alert", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    mockPut.mockResolvedValue({ data: { message: "Profile updated!" }, error: null });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText(/name/i), { target: { value: "Bobby" } });
    fireEvent.click(screen.getByRole("button", { name: /update profile/i }));
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith("Profile updated!"));
  });

  it("updates profile and logs out if require_reauth", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    mockPut.mockResolvedValue({ data: { message: "ok", require_reauth: true }, error: null });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.click(screen.getByRole("button", { name: /update profile/i }));
    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith("ok");
      expect(window.alert).toHaveBeenCalledWith("Your email was changed. Please log in again.");
      expect(mockLogout).toHaveBeenCalled();
    });
  });

  it("shows alert if profile update fails", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    mockPut.mockResolvedValue({ data: null, error: "fail" });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.click(screen.getByRole("button", { name: /update profile/i }));
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith("Failed to update profile"));
  });

  it("shows alert if passwords do not match", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText("New Password"), { target: { value: "abc" } });
    fireEvent.change(screen.getByLabelText("Confirm New Password"), { target: { value: "def" } });
    fireEvent.click(screen.getByRole("button", { name: /change password/i }));
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith("New passwords do not match."));
  });

  it("changes password and resets fields", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    mockPatch.mockResolvedValue({ error: null });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText(/current password/i), { target: { value: "old" } });
    fireEvent.change(screen.getByLabelText(/^new password$/i), { target: { value: "newpass" } });
    fireEvent.change(screen.getByLabelText(/confirm new password/i), { target: { value: "newpass" } });
    fireEvent.click(screen.getByRole("button", { name: /change password/i }));
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith("Password changed successfully!"));
    expect(screen.getByLabelText(/current password/i)).toHaveValue("");
    expect(screen.getByLabelText(/^new password$/i)).toHaveValue("");
    expect(screen.getByLabelText(/confirm new password/i)).toHaveValue("");
  });

  it("shows alert if password change fails", async () => {
    mockGet.mockResolvedValue({ data: { name: "Bob", email: "bob@mail.com" }, error: null });
    mockPatch.mockResolvedValue({ error: "fail" });
    window.alert = jest.fn();
    render(<ProfilePage />);
    await waitFor(() => expect(screen.getByDisplayValue("Bob")).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText(/current password/i), { target: { value: "old" } });
    fireEvent.change(screen.getByLabelText(/^new password$/i), { target: { value: "newpass" } });
    fireEvent.change(screen.getByLabelText(/confirm new password/i), { target: { value: "newpass" } });
    fireEvent.click(screen.getByRole("button", { name: /change password/i }));
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith("Failed to change password. Check current password."));
  });
});