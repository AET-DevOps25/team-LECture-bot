import React, { useEffect, useState, useMemo } from "react";
import type { components } from "@src/shared/api/generated/api";

export type Flashcard = components["schemas"]["Flashcard"];
export type FlashcardResponse = components["schemas"]["FlashcardResponse"];
export type FlashcardsForDocument = components["schemas"]["FlashcardsForDocument"];

interface FlashcardViewProps {
    flashcardData: FlashcardResponse;
}



/**
 * A Component that displays a set of generated flashcards.
 */
export const FlashcardView: React.FC<FlashcardViewProps> = ({ flashcardData }) => {
    const allFlashcards: Flashcard[] = useMemo(() => {
        return flashcardData.flashcards?.flatMap(docResult => docResult.flashcards || []) || [];
    }, [flashcardData]);

    const [shuffledCards, setShuffledCards] = useState<Flashcard[]>([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [isFlipped, setIsFlipped] = useState(false);

    useEffect(() => {
        handleShuffle();
    }, [flashcardData]);

    const goNext = () => {
        setCurrentIndex(prevIndex => {
            const nextIndex = prevIndex + 1;
            return nextIndex < shuffledCards.length ? nextIndex : 0;
        });
        setIsFlipped(false);
    }


    const goPrevious = () => {
        setCurrentIndex(prevIndex => {
            const prevIndexValue = prevIndex - 1;
            return prevIndexValue >= 0 ? prevIndexValue : shuffledCards.length - 1;
        });
        setIsFlipped(false);
    }
    const handleShuffle = () => {
        const reshuffled = [...allFlashcards].sort(() => Math.random() - 0.5);
        setShuffledCards(reshuffled);
        setCurrentIndex(0);
        setIsFlipped(false);
    };

    // ⬇️ *** ADD THIS CHECK HERE *** ⬇️
    // This guard clause prevents the component from rendering an undefined card.
    if (shuffledCards.length === 0) {
        return <p>Generating flashcards...</p>; // Or return null if you want nothing to show
    }

    const currentCard = shuffledCards[currentIndex];

    return (
        <div className="flex flex-col items-center gap-6">
            {/* The 3D container for the card flip effect */}
            <div className="w-full max-w-xl h-64 [perspective:1000px]">
                <div
                    className={`relative w-full h-full text-center transition-transform duration-700 [transform-style:preserve-3d] ${isFlipped ? '[transform:rotateY(180deg)]' : ''}`}
                    onClick={() => setIsFlipped(!isFlipped)}
                >
                    {/* Front of Card */}
                    <div className="absolute w-full h-full p-6 bg-white border border-gray-200 rounded-xl shadow-lg flex items-center justify-center cursor-pointer [backface-visibility:hidden]">
                        <p className="text-2xl font-semibold text-gray-800">{currentCard.question}</p>
                    </div>

                    {/* Back of Card */}
                    <div className="absolute w-full h-full p-6 bg-blue-50 border border-blue-200 rounded-xl shadow-lg flex items-center justify-center cursor-pointer [transform:rotateY(180deg)] [backface-visibility:hidden]">
                        <p className="text-xl text-gray-700">{currentCard.answer}</p>
                    </div>
                </div>
            </div>

            {/* Navigation Controls */}
            <div className="flex items-center gap-4">
                <button
                    onClick={goPrevious}
                    disabled={currentIndex === 0}
                    className="px-4 py-2 font-bold text-white bg-blue-600 rounded-md shadow hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
                >
                    &larr; Previous
                </button>
                <span className="text-gray-700 font-medium">{currentIndex + 1} / {shuffledCards.length}</span>
                <button
                    onClick={goNext}
                    disabled={currentIndex === shuffledCards.length - 1}
                    className="px-4 py-2 font-bold text-white bg-blue-600 rounded-md shadow hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
                >
                    Next &rarr;
                </button>
            </div>

            {/* Shuffle Button */}
            <button
                onClick={handleShuffle}
                className="px-5 py-2 font-semibold text-white bg-green-500 rounded-lg shadow hover:bg-green-600 transition-colors"
            >
                Shuffle Deck
            </button>
        </div>
    );
}
