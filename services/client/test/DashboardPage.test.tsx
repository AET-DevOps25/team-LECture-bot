import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import DashboardPage from "../src/pages/DashboardPage";

// Mock dependencies
jest.mock("../src/context/CourseSpaceContext", () => ({
  useCourseSpaces: jest.fn(),
}));
jest.mock("../src/components/CourseSpaceModal", () => (props: any) =>
  props.isOpen ? <div data-testid="create-modal">CreateModal</div> : null
);
jest.mock("../src/components/EditCourseSpaceModal", () => (props: any) =>
  props.isOpen ? <div data-testid="edit-modal">EditModal</div> : null
);
jest.mock("../src/api/apiClient", () => ({
  apiClient: {
    DELETE: jest.fn(),
  },
}));

const mockCourse = { id: "1", name: "Test Course", description: "desc" };

describe("DashboardPage", () => {
  const useCourseSpaces = require("../src/context/CourseSpaceContext").useCourseSpaces;
  const apiClient = require("../src/api/apiClient").apiClient;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("shows loading state", () => {
    useCourseSpaces.mockReturnValue({ loading: true, error: null, courseSpaces: [] });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    expect(screen.getByText(/loading courses/i)).toBeInTheDocument();
  });

  it("shows error state", () => {
    useCourseSpaces.mockReturnValue({ loading: false, error: "fail", courseSpaces: [] });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    expect(screen.getByText(/error: fail/i)).toBeInTheDocument();
  });

  it("shows empty state", () => {
    useCourseSpaces.mockReturnValue({ loading: false, error: null, courseSpaces: [] });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    expect(screen.getByText(/you haven't created any course spaces yet/i)).toBeInTheDocument();
  });

  it("renders course spaces", () => {
    useCourseSpaces.mockReturnValue({
      loading: false,
      error: null,
      courseSpaces: [mockCourse],
      createCourseSpace: jest.fn(),
      fetchCourseSpaces: jest.fn(),
      updateCourseSpace: jest.fn(),
    });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    expect(screen.getByText("Test Course")).toBeInTheDocument();
    expect(screen.getByText(/edit/i)).toBeInTheDocument();
    expect(screen.getByText(/enter/i)).toBeInTheDocument();
  });

  it("opens create modal", () => {
    useCourseSpaces.mockReturnValue({ loading: false, error: null, courseSpaces: [], createCourseSpace: jest.fn() });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByText(/create new space/i));
    expect(screen.getByTestId("create-modal")).toBeInTheDocument();
  });

  it("opens edit modal", () => {
    useCourseSpaces.mockReturnValue({
      loading: false,
      error: null,
      courseSpaces: [mockCourse],
      updateCourseSpace: jest.fn(),
      fetchCourseSpaces: jest.fn(),
      createCourseSpace: jest.fn(),
    });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByText(/edit/i));
    expect(screen.getByTestId("edit-modal")).toBeInTheDocument();
  });

  it("calls delete API on delete confirm", async () => {
    window.confirm = jest.fn(() => true);
    useCourseSpaces.mockReturnValue({
      loading: false,
      error: null,
      courseSpaces: [mockCourse],
      updateCourseSpace: jest.fn(),
      fetchCourseSpaces: jest.fn(),
      createCourseSpace: jest.fn(),
    });
    apiClient.DELETE.mockResolvedValue({ error: null });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByLabelText(/delete course space/i));
    await waitFor(() => expect(apiClient.DELETE).toHaveBeenCalled());
  });

  it("does not call delete API if confirm is false", () => {
    window.confirm = jest.fn(() => false);
    useCourseSpaces.mockReturnValue({
      loading: false,
      error: null,
      courseSpaces: [mockCourse],
      updateCourseSpace: jest.fn(),
      fetchCourseSpaces: jest.fn(),
      createCourseSpace: jest.fn(),
    });
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByLabelText(/delete course space/i));
    expect(apiClient.DELETE).not.toHaveBeenCalled();
  });
});