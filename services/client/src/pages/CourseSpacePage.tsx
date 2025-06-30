import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import QnAChat from "../components/QnAChat";

// Placeholder components for features that are not yet implemented.
const Flashcards: React.FC = () => (
  <div className="p-8 text-center text-gray-500">Flashcards feature coming soon!</div>
);
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
  const { token } = useAuth(); // Use the new AuthContext
  const [activeTab, setActiveTab] = useState('qna');

  return (
    <div className="container mx-auto">
      <h1 className="text-2xl font-bold mb-4">Course Space: {courseSpaceId}</h1>
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8" aria-label="Tabs">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`${
                activeTab === tab.key
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
        {activeTab === 'flashcards' && <Flashcards />}
        {activeTab === 'documents' && <Documents />}
        {activeTab === 'qna' && !token && (
          <div className="text-red-500 text-center mt-8">You must be logged in to use Q&A.</div>
        )}
      </div>
    </div>
  );
};

export default CourseSpacePage;