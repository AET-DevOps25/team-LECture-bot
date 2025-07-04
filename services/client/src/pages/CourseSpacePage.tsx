import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import QnAChat from "../components/QnAChat";
import { apiClient } from '@src/api/apiClient';
import type { components } from '../shared/api/generated/api';
import FlashcardViewer from '../components/flashcards/FlashcardViewer';

type CourseSpace = components['schemas']['CourseSpaceDto'];

const Documents: React.FC = () => (
    <div className="p-8 text-center text-gray-500">Document management coming soon!</div>
);

const TABS = [
    { key: 'qna', label: 'Q&A' },
    { key: 'flashcards', label: 'Flashcards' },
    { key: 'documents', label: 'Documents' },
];

const CourseSpacePage: React.FC = () => {
    const { courseSpaceId } = useParams<{ courseSpaceId: string }>();
    const [courseSpace, setCourseSpace] = useState<CourseSpace | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { token } = useAuth(); // Use the new AuthContext
    const [activeTab, setActiveTab] = useState('qna');


    useEffect(() => {
        // Make sure we have a courseId before trying to fetch
        if (!courseSpaceId) {
            setError('No course space ID provided.');
            setLoading(false);
            return;
        }

        const fetchCourseSpaceDetails = async () => {
            try {
                setLoading(true);
                // This assumes you have a GET /coursespaces/{courseSpaceId} endpoint
                const { data, error: fetchError } = await apiClient.GET('/coursespaces/{courseSpaceId}', {
                    params: {
                        path: { courseSpaceId },
                    },
                });

                if (fetchError || !data) {
                    throw new Error('Failed to fetch course space details.');
                }

                setCourseSpace(data); // Save the fetched data to state
            } catch (err) {
                setError((err as Error).message);
            } finally {
                setLoading(false);
            }
        };

        fetchCourseSpaceDetails();
    }, [courseSpaceId]); // <-- This effect re-runs if the courseId in the URL changes

    if (loading) {
        return <div>Loading course space...</div>;
    }

    if (error) {
        return <div className="text-red-500">Error: {error}</div>;
    }

    if (!courseSpace) {
        return <div>Course space not found.</div>;
    }
    return (
        <div className="container mx-auto">
            <h1 className="text-2xl font-bold mb-4">Course Space: {courseSpace.name} ({courseSpaceId})</h1>
            <div className="border-b border-gray-200">
                <nav className="-mb-px flex space-x-8" aria-label="Tabs">
                    {TABS.map((tab) => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key)}
                            className={`${activeTab === tab.key
                                ? 'border-indigo-500 text-indigo-600'
                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
                        >
                            {tab.label}
                        </button>
                    ))}
                </nav>
            </div>
            <div className="mt-6">
                {activeTab === 'qna' && token && <QnAChat courseSpaceId={courseSpaceId!} />}
                {activeTab === 'flashcards' && <FlashcardViewer courseSpaceId={courseSpaceId!} />}
                {activeTab === 'documents' && <Documents />}
                {activeTab === 'qna' && !token && (
                    <div className="text-red-500 text-center mt-8">You must be logged in to use Q&A.</div>
                )}
            </div>
        </div>
    );
};

export default CourseSpacePage;
