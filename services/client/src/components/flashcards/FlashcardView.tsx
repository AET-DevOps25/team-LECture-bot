import React, { useEffect, useState, useMemo } from "react";
import type { components } from "@src/shared/api/generated/api";
import styles from "./FlashcardView.module.css";

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
        <div className={styles.flashcardContainer}>
            <div className={styles.cardScene}>
                <div
                    className={`${styles.flashcard} ${isFlipped ? styles.isFlipped : ''}`}
                    onClick={() => setIsFlipped(!isFlipped)}
                >
                    <div className={`${styles.cardFace} ${styles.cardFront}`}>
                        <p className={styles.cardText}>{currentCard.question}</p>
                    </div>
                    <div className={`${styles.cardFace} ${styles.cardBack}`}>
                        <p className={styles.cardText}>{currentCard.answer}</p>
                    </div>
                </div>
            </div>

            <div className={styles.navigation}>
                <button onClick={goPrevious} disabled={currentIndex === 0}> &larr; Previous</button>
                <span>{currentIndex + 1} / {shuffledCards.length}</span>
                <button onClick={goNext} disabled={currentIndex === shuffledCards.length - 1}>Next &rarr;</button>
            </div>

            <button onClick={handleShuffle} className={styles.shuffleButton}>
                Shuffle Deck
            </button>
        </div>
    );
}
