import React, { useState, useEffect } from 'react';

interface Props {
    isOpen: boolean;
    onClose: () => void;
    initialName: string;
    initialDescription?: string;
    onSave: (name: string, description: string) => void;
}

const EditCourseSpaceModal: React.FC<Props> = ({
    isOpen,
    onClose,
    initialName,
    initialDescription = '',
    onSave
}) => {
    const [name, setName] = useState(initialName);
    const [description, setDescription] = useState(initialDescription);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState<string | null>(null);

    useEffect(() => {
        setName(initialName);
        setDescription(initialDescription);
    }, [initialName, initialDescription, isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!name.trim()) {
            setError('Title cannot be empty.');
            return;
        }
        setLoading(true);
        setError(null);
        setSuccess(null);
        try {
            await onSave(name, description);
            setSuccess('Course space updated successfully!');
            setTimeout(() => {
                setSuccess(null);
                onClose();
            }, 1000);
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : 'Failed to update course space.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
            <div className="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md">
                <h2 className="text-2xl font-bold mb-6">Edit Course Space</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="courseName" className="block text-sm font-medium text-gray-700 mb-1">
                            Title
                        </label>
                        <input
                            id="courseName"
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="e.g., Introduction to AI"
                            disabled={loading}
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="courseDescription" className="block text-sm font-medium text-gray-700 mb-1">
                            Description
                        </label>
                        <textarea
                            id="courseDescription"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Course description (optional)"
                            disabled={loading}
                        />
                    </div>
                    {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                    {success && <p className="text-green-600 text-sm mb-4">{success}</p>}
                    <div className="flex justify-end gap-4">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                            className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading || !name.trim()}
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {loading ? 'Saving...' : 'Save'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditCourseSpaceModal;