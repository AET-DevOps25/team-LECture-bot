import { useState } from 'react';
import type { FlashcardRequestBody, FlashcardResponseBody } from '@src/api/apiClient';
import { generateFlashcards } from '@src/api/apiClient';
import { FlashcardView, type FlashcardResponse } from './FlashcardView';


function FlashcardViewer() {
    const [groupedFlashcards, setGroupedFlashcards] = useState<FlashcardResponse | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const couseSpaceId = 'CS101'; // Example course space ID

    const fetchFlashcards = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const requestBody: FlashcardRequestBody = {
                course_space_id: couseSpaceId,
            };

            const response: FlashcardResponseBody = await generateFlashcards(requestBody);

            if (response && response.flashcards) {
                setGroupedFlashcards(response);
            } else {
                setGroupedFlashcards(null);
            }
        } catch (err) {
            console.log("Failed to fetch flashcards:", err);
            setError("An error occurred while generating flashcards.")
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            <h2>Flashcards for CourseSpace: {couseSpaceId}</h2>
            <button onClick={fetchFlashcards} disabled={isLoading}>
                {isLoading ? 'Generating...' : 'Generate Flashcards'}
            </button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {groupedFlashcards ? (
                <FlashcardView flashcardData={groupedFlashcards} />
            ) : (
                !isLoading && <p>No flashcards generated. Click the button to start.</p>
            )}
        </>
    )
}


export default FlashcardViewer
