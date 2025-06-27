import { useState } from 'react';
import type { FlashcardRequestBody, FlashcardResponseBody } from '@src/api/apiClient';
import { generateFlashcards } from '@src/api/apiClient';
import type { components } from '@src/shared/api/generated/api';

type FlashcardForDocument = components['schemas']['FlashcardsForDocument'];

function FlashcardViewer() {
    const [groupedFlashcards, setGroupedFlashcards] = useState<FlashcardForDocument[]>([]);
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
                setGroupedFlashcards(response.flashcards);
            } else {
                setGroupedFlashcards([]);
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

            {groupedFlashcards.length > 0 ? (
                groupedFlashcards.map((csResult) => (
                    <div key={csResult.document_id} style={{ marginTop: '20px' }}>
                        <h3>Document: {csResult.document_id}</h3>
                        <ul>
                            {csResult.flashcards && csResult.flashcards.map((flashcard, index) => (
                                <li key={index}>
                                    <p><strong>Question:</strong>{flashcard.question}</p>
                                    <p><strong>Answer:</strong>{flashcard.answer}</p>
                                </li>
                            ))}
                        </ul>
                    </div>
                ))
            ) : (
                !isLoading && <p>No flashcards generated. Click the button to start.</p>
            )}
        </>
    )
}


export default FlashcardViewer
