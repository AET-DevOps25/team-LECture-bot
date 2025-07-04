import { useState } from 'react';
import type { components } from '@src/shared/api/generated/api';
import { apiClient } from '@src/api/apiClient';
import { FlashcardView } from './FlashcardView';

type FlashcardResponse = components["schemas"]["FlashcardResponse"]
type FlashcardRequest = components["schemas"]["FlashcardRequest"]

interface Props {
    courseSpaceId: string;

}
const FlashcardViewer: React.FC<Props> = ({ courseSpaceId }) => {
    const [groupedFlashcards, setGroupedFlashcards] = useState<FlashcardResponse | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const fetchFlashcards = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const request: FlashcardRequest = {
                course_space_id: courseSpaceId,
            };

            const { data, error } = await apiClient.POST('/genai/generate-flashcards',
                {
                    body: request
                });

            if (error) {
                console.error('Failed to generate flashcards:', error);
                return null;
            }

            if (data && data.flashcards) {
                console.log('Successfully received flashcards:', data);
                setGroupedFlashcards(data)

                return data as FlashcardResponse;
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
        <div className="mt-8">
            <div className="flex flex-col items-center">
                <button
                    onClick={fetchFlashcards}
                    disabled={isLoading}
                    className="px-6 py-3 font-semibold text-white bg-blue-600 rounded-lg shadow-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors duration-300"
                >
                    {isLoading ? 'Generating...' : '✨ Generate Flashcards'}
                </button>

                {error && <p className="mt-4 text-red-500">{error}</p>}
            </div>


            <div className="mt-8">
                {groupedFlashcards ? (
                    <FlashcardView flashcardData={groupedFlashcards} />
                ) : (
                    !isLoading && !error && (
                        <div className="text-center py-10 bg-gray-50 rounded-lg">
                            <p className="text-lg text-gray-500">No flashcards generated yet.</p>
                            <p className="mt-1 text-gray-400">Click the button to start.</p>
                        </div>
                    )
                )}
            </div>
        </div>
    );
}


export default FlashcardViewer
