

// Mock dependencies (all at top, all using ../src/ paths)
jest.mock("../src/context/AuthContext", () => ({
  useAuth: jest.fn(),
}));
jest.mock("../src/components/QnAChat", () => () => <div>QnAChatMock</div>);
jest.mock("../src/components/flashcards/FlashcardViewer", () => () => <div>FlashcardViewerMock</div>);
jest.mock("../src/components/PdfUpload", () => () => <div>PdfUploadMock</div>);
jest.mock("../src/api/apiClient", () => ({
  apiClient: {
    GET: jest.fn(),
  },
}));

import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import CourseSpacePage from "../src/pages/CourseSpacePage";

const mockCourseSpace = { name: "Test Course" };

function renderWithRouter(courseSpaceId = "abc") {
  return render(
    <MemoryRouter initialEntries={[`/space/${courseSpaceId}`]}>
      <Routes>
        <Route path="/space/:courseSpaceId" element={<CourseSpacePage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("CourseSpacePage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });


  it("shows loading initially", () => {
    require("../src/context/AuthContext").useAuth.mockReturnValue({ token: "t" });
    renderWithRouter();
    expect(screen.getByText(/loading course space/i)).toBeInTheDocument();
  });

  it("shows error if fetch fails", async () => {
    require("../src/context/AuthContext").useAuth.mockReturnValue({ token: "t" });
    require("../src/api/apiClient").apiClient.GET.mockResolvedValue({ data: null, error: "fail" });
    renderWithRouter();
    await waitFor(() => expect(screen.getByText(/error:/i)).toBeInTheDocument());
  });

  it("shows not found if no courseSpace", async () => {
    require("../src/context/AuthContext").useAuth.mockReturnValue({ token: "t" });
    require("../src/api/apiClient").apiClient.GET.mockResolvedValue({ data: null, error: null });
    renderWithRouter();
    await waitFor(() =>
      expect(screen.getByText(/failed to fetch course space details/i)).toBeInTheDocument()
    );
  });

  it("renders course space and tabs, switches tabs", async () => {
    require("../src/context/AuthContext").useAuth.mockReturnValue({ token: "t" });
    require("../src/api/apiClient").apiClient.GET.mockResolvedValue({ data: mockCourseSpace, error: null });
    renderWithRouter("xyz");
    await waitFor(() => expect(screen.getByText(/course space: test course/i)).toBeInTheDocument());
    // QnA tab
    expect(screen.getByText("QnAChatMock")).toBeInTheDocument();
    // Flashcards tab
    fireEvent.click(screen.getByText(/flashcards/i));
    expect(screen.getByText("FlashcardViewerMock")).toBeInTheDocument();
    // Documents tab
    fireEvent.click(screen.getByText(/documents/i));
    expect(screen.getByText("PdfUploadMock")).toBeInTheDocument();
  });

  it("shows Q&A login warning if not authenticated", async () => {
    require("../src/context/AuthContext").useAuth.mockReturnValue({ token: null });
    require("../src/api/apiClient").apiClient.GET.mockResolvedValue({ data: mockCourseSpace, error: null });
    renderWithRouter();
    await waitFor(() => expect(screen.getByText(/course space:/i)).toBeInTheDocument());
    expect(screen.getByText(/you must be logged in to use q&a/i)).toBeInTheDocument();
  });
});