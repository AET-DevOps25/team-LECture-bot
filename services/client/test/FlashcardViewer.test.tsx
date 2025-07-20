import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { apiClient } from "@src/api/apiClient";
import FlashcardViewer from "../src/components/flashcards/FlashcardViewer";

// Mock FlashcardView to avoid rendering its internals
jest.mock("../src/components/flashcards/FlashcardView", () => ({
  FlashcardView: ({ flashcardData }: any) => (
    <div data-testid="flashcard-view">{JSON.stringify(flashcardData)}</div>
  ),
}));

jest.mock("@src/api/apiClient", () => ({
  apiClient: {
    POST: jest.fn(),
  },
}));

const mockFlashcards = {
  flashcards: [
    {
      documentId: "doc1",
      flashcards: [{ question: "Q1?", answer: "A1" }],
    },
  ],
};

describe("FlashcardViewer", () => {
  const courseSpaceId = "course-123";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("shows initial prompt before generating", () => {
    render(<FlashcardViewer courseSpaceId={courseSpaceId} />);
    expect(screen.getByText(/no flashcards generated yet/i)).toBeInTheDocument();
    expect(screen.getByText(/click the button to start/i)).toBeInTheDocument();
  });

  it("shows loading state when generating", async () => {
    (apiClient.POST as jest.Mock).mockReturnValue(new Promise(() => {})); // never resolves
    render(<FlashcardViewer courseSpaceId={courseSpaceId} />);
    fireEvent.click(screen.getByText(/generate flashcards/i));
    expect(screen.getByText(/generating/i)).toBeInTheDocument();
  });

  it("shows error if API returns error", async () => {
    (apiClient.POST as jest.Mock).mockResolvedValue({ error: "fail" });
    render(<FlashcardViewer courseSpaceId={courseSpaceId} />);
    fireEvent.click(screen.getByText(/generate flashcards/i));
    await waitFor(() =>
      expect(screen.getByText(/no flashcards generated yet/i)).toBeInTheDocument()
    );
  });

  it("shows error if fetch throws", async () => {
    (apiClient.POST as jest.Mock).mockRejectedValue(new Error("fail"));
    render(<FlashcardViewer courseSpaceId={courseSpaceId} />);
    fireEvent.click(screen.getByText(/generate flashcards/i));
    await waitFor(() =>
      expect(
        screen.getByText(/an error occurred while generating flashcards/i)
      ).toBeInTheDocument()
    );
  });

  it("renders FlashcardView on successful fetch", async () => {
    (apiClient.POST as jest.Mock).mockResolvedValue({ data: mockFlashcards });
    render(<FlashcardViewer courseSpaceId={courseSpaceId} />);
    fireEvent.click(screen.getByText(/generate flashcards/i));
    await waitFor(() =>
      expect(screen.getByTestId("flashcard-view")).toBeInTheDocument()
    );
    expect(screen.getByText(/Q1\?/)).toBeInTheDocument();
  });
});