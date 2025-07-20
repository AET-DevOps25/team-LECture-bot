import { render, screen, waitFor } from "@testing-library/react";
import { CourseSpaceProvider, useCourseSpaces } from "../src/context/CourseSpaceContext";
import { act } from "react-dom/test-utils";

// Mock dependencies
jest.mock("../src/api/apiClient", () => ({
  apiClient: {
    GET: jest.fn(),
    POST: jest.fn(),
    PUT: jest.fn(),
  },
}));
jest.mock("../src/context/AuthContext", () => ({
  useAuth: () => ({ isAuthenticated: true }),
}));

const mockCourseSpace = { id: "1", name: "Test", description: "desc" };

function Consumer() {
  const { courseSpaces, loading, error, fetchCourseSpaces, createCourseSpace, updateCourseSpace } = useCourseSpaces();
  return (
    <div>
      <div>count:{courseSpaces.length}</div>
      <div>loading:{loading ? "yes" : "no"}</div>
      <div>error:{error || ""}</div>
      <button onClick={fetchCourseSpaces}>fetch</button>
      <button onClick={() => createCourseSpace("New", "desc")}>create</button>
      <button onClick={() => updateCourseSpace("1", "Updated", "desc")}>update</button>
    </div>
  );
}

describe("CourseSpaceContext", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("provides default values and fetches on mount", async () => {
    const { apiClient } = require("../src/api/apiClient");
    apiClient.GET.mockResolvedValue({ data: [mockCourseSpace], error: null });
    render(
      <CourseSpaceProvider>
        <Consumer />
      </CourseSpaceProvider>
    );
    await waitFor(() => expect(screen.getByText("count:1")).toBeInTheDocument());
  });

  it("handles fetch error", async () => {
    const { apiClient } = require("../src/api/apiClient");
    apiClient.GET.mockResolvedValue({ data: null, error: "fail" });
    render(
      <CourseSpaceProvider>
        <Consumer />
      </CourseSpaceProvider>
    );
    await waitFor(() => expect(screen.getByText(/error:Failed to fetch course spaces/)).toBeInTheDocument());
  });

  it("can create a course space", async () => {
    const { apiClient } = require("../src/api/apiClient");
    apiClient.POST.mockResolvedValue({ data: mockCourseSpace, error: null });
    render(
      <CourseSpaceProvider>
        <Consumer />
      </CourseSpaceProvider>
    );
    await act(async () => {
      screen.getByText("create").click();
    });
    expect(screen.getByText("count:1")).toBeInTheDocument();
  });

  it("can update a course space", async () => {
    const { apiClient } = require("../src/api/apiClient");
    apiClient.GET.mockResolvedValue({ data: [mockCourseSpace], error: null });
    apiClient.PUT.mockResolvedValue({ data: { ...mockCourseSpace, name: "Updated" }, error: null });
    render(
      <CourseSpaceProvider>
        <Consumer />
      </CourseSpaceProvider>
    );
    await waitFor(() => expect(screen.getByText("count:1")).toBeInTheDocument());
    await act(async () => {
      screen.getByText("update").click();
    });
    expect(screen.getByText("count:1")).toBeInTheDocument();
  });

  it("throws if used outside provider", () => {
    // Suppress error boundary output
    const spy = jest.spyOn(console, "error").mockImplementation(() => {});
    expect(() => render(<Consumer />)).toThrow(/useCourseSpaces must be used within a CourseSpaceProvider/);
    spy.mockRestore();
  });
});