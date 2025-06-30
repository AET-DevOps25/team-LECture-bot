import React, { useState, useRef, useEffect } from "react";
import { apiClient } from "../api/apiClient";
import type { components } from "../shared/api/generated/api";

type Citation = components['schemas']['Citation'];
type QueryRequest = components['schemas']['QueryRequest'];

interface QAEntry {
  question: string;
  answer: string;
  citations: Citation[] | undefined;
}

interface QnAChatProps {
  courseSpaceId: string;
}

const QnAChat: React.FC<QnAChatProps> = ({ courseSpaceId }) => {
  const [question, setQuestion] = useState("");
  const [history, setHistory] = useState<QAEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const chatEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [history, loading]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!question.trim()) return;

    setLoading(true);
    setError(null);

    const requestBody: QueryRequest = {
      query_text: question,
      course_space_id: courseSpaceId,
    };

    try {
      // Use the new apiClient which handles auth automatically
      const { data, error: apiError } = await apiClient.POST("/genai/query", {
        body: requestBody,
      });

      if (apiError) {
        const errorMessage = (apiError as any).detail || 'An error occurred. Please try again.';
        throw new Error(errorMessage);
      }

      if (data) {
        setHistory((prev) => [
          ...prev,
          {
            question,
            answer: data.answer || "No answer provided.",
            citations: data.citations,
          },
        ]);
        setQuestion("");
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto p-4 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Course Q&amp;A</h2>
      <div className="h-80 overflow-y-auto mb-4 border rounded p-2 bg-gray-50">
        {history.length === 0 && !loading && (
          <div className="text-gray-400 text-center mt-16">
            Ask a question about your course materials!
          </div>
        )}
        {history.map((entry, idx) => (
          <div key={idx} className="mb-6">
            <div className="flex justify-end">
              <div className="bg-blue-100 text-blue-900 px-4 py-2 rounded-lg max-w-xs break-words">
                <span className="font-semibold">You:</span> {entry.question}
              </div>
            </div>
            <div className="flex justify-start mt-2">
              <div className="bg-green-100 text-green-900 px-4 py-2 rounded-lg max-w-xs break-words">
                <span className="font-semibold">Answer:</span> {entry.answer}
                {entry.citations && entry.citations.length > 0 && (
                  <div className="mt-2 text-xs text-gray-700">
                    <span className="font-semibold">Citations:</span>
                    <ul className="list-disc ml-4">
                      {entry.citations.map((c, i) => (
                        <li key={i}>
                          <span className="font-mono">
                            {c.document_id}
                            {c.chunk_id ? ` (chunk ${c.chunk_id})` : ""}
                          </span>
                          {c.document_name && <> — {c.document_name}</>}
                          {c.retrieved_text_preview && (
                            <div className="italic text-gray-500 mt-1">
                              “{c.retrieved_text_preview}”
                            </div>
                          )}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex justify-center items-center mt-4">
            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500"></div>
            <span className="ml-2 text-blue-500">Generating answer...</span>
          </div>
        )}
        <div ref={chatEndRef} />
      </div>
      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          className="flex-1 border rounded px-3 py-2 focus:outline-none focus:ring"
          placeholder="Type your question..."
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          disabled={loading}
        />
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
          disabled={loading || !question.trim()}
        >
          Ask
        </button>
      </form>
      {error && (
        <div className="mt-2 text-red-600 text-sm text-center">{error}</div>
      )}
    </div>
  );
};

export default QnAChat;