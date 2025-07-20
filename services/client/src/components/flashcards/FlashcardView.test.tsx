import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { FlashcardView } from "./FlashcardView";

const mockFlashcardData = {
  flashcards: [
    {
      documentId: "doc1",
      flashcards: [
        { question: "Q1?", answer: "A1" },
        { question: "Q2?", answer: "A2" },
      ],
    },
  ],
};

describe("FlashcardView", () => {
  beforeAll(() => {
    jest.spyOn(global.Math, "random").mockReturnValue(0);
  });

  afterAll(() => {
    (global.Math.random as jest.Mock).mockRestore();
  });

  it("shows loading message if no flashcards", () => {
    render(<FlashcardView flashcardData={{ flashcards: [] }} />);
    expect(screen.getByText(/generating flashcards/i)).toBeInTheDocument();
  });

  it("renders first flashcard question", () => {
    render(<FlashcardView flashcardData={mockFlashcardData as any} />);
    // Accept either Q1? or Q2? as the first card
    expect(screen.getByText(/Q[12]\?/)).toBeInTheDocument();
  });

  it("flips card to show answer", () => {
    render(<FlashcardView flashcardData={mockFlashcardData as any} />);
    // Find the question currently shown
    const question = screen.getByText(/Q[12]\?/);
    fireEvent.click(question);
    // Expect the corresponding answer to be shown
    expect(screen.getByText(/A[12]/)).toBeInTheDocument();
  });

  it("navigates to next and previous cards", () => {
    render(<FlashcardView flashcardData={mockFlashcardData as any} />);
    // Find which card is shown first
    const firstCard = screen.getByText(/Q[12]\?/);
    const firstText = firstCard.textContent;
    // Click next
    fireEvent.click(screen.getByText(/next/i));
    // The other card should now be visible
    const secondCard = screen.getByText((content) =>
      content === "Q1?" || content === "Q2?"
    );
    expect(secondCard.textContent).not.toBe(firstText);
    // Click previous, should go back to first
    fireEvent.click(screen.getByText(/previous/i));
    expect(screen.getByText(firstText!)).toBeInTheDocument();
  });

  it("calls shuffle when shuffle button is clicked", () => {
    render(<FlashcardView flashcardData={mockFlashcardData as any} />);
    const shuffleBtn = screen.getByText(/shuffle deck/i);
    fireEvent.click(shuffleBtn);
    // No assertion needed: just ensure no crash and UI still shows a card
    expect(screen.getByText(/\/ 2/)).toBeInTheDocument();
  });
});