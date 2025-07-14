import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import CreateCourseSpaceModal from '../components/CourseSpaceModal';
import EditCourseSpaceModal from '../components/EditCourseSpaceModal';
import { useCourseSpaces } from '../context/CourseSpaceContext';
import type { components } from '../shared/api/generated/user-course-api';



const DashboardPage: React.FC = () => {
    const { courseSpaces, loading, error, createCourseSpace, fetchCourseSpaces, updateCourseSpace } = useCourseSpaces();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [editingCourse, setEditingCourse] = useState<components['schemas']['CourseSpaceDto'] | null>(null);
    // Edit logic
    const handleEditClick = (e: React.MouseEvent, course: components['schemas']['CourseSpaceDto']) => {
        e.preventDefault();
        e.stopPropagation();
        setEditingCourse(course);
        setEditModalOpen(true);
    };

    const handleEditSave = async (title: string, description: string) => {
        if (!editingCourse?.id) return;
        try {
            const updated = await updateCourseSpace(editingCourse.id, title, description);
            if (!updated) throw new Error('Failed to update course space.');
            await fetchCourseSpaces();
        } catch (err) {
            throw err;
        }
    };


    // Called by the modal on success
    const handleCourseCreated = async (name: string, description: string) => {
        const newCourseSpace = await createCourseSpace(name, description);
        if (newCourseSpace && newCourseSpace.id) {
            setIsModalOpen(false);
        } else {
            // Handle creation error if needed
            alert('Could not create the course space.');
        }
    };


    const handleDelete = async (e: React.MouseEvent, courseSpaceId: string) => {
        // Stop the click from navigating to the course space page
        e.preventDefault();
        e.stopPropagation();

        if (!window.confirm('Are you sure you want to delete this course space? This action cannot be undone.')) {
            return;
        }

        try {
            const { error: deleteError } = await apiClient.DELETE('/coursespaces/{courseSpaceId}', {
                params: {
                    path: { courseSpaceId }
                }
            });

            if (deleteError) {
                throw new Error('Failed to delete course space.');
            }

        } catch (err) {
            alert((err as Error).message);
        }
    };

    if (loading) return <div>Loading courses...</div>;
    if (error) return <div className="text-red-500">Error: {error}</div>;

    return (
        <div className="container mx-auto">
            <div className='flex justify-between items-center mb-8'>
                <h1 className="text-3xl font-bold mb-6">My Course Spaces</h1>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className='px-5 py-3 font-bold text-white bg-green-500 rounded-lg shadow hover:bg-green-600 transition-colors'
                >
                    ✨ Create New Space
                </button>
            </div>
            {courseSpaces.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {courseSpaces.map((course) => (
                        <Link
                            key={course.id}
                            to={`/coursespaces/${course.id}`}
                            className="group relative block p-6 bg-white border border-gray-200 rounded-xl shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
                        >
                            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900">{course.name}</h5>
                            <button
                                onClick={(e) => handleDelete(e, course.id!)}
                                className="absolute top-4 right-4 bg-red-100 text-red-600 rounded-full h-8 w-8 flex items-center justify-center opacity-0 group-hover:opacity-100 hover:bg-red-200 transition-all duration-200 z-10" // <-- Added z-10
                                aria-label="Delete course space"
                            >
                                &times; {/* Using a proper 'x' icon */}
                            </button>
                            <div className="mt-4 flex justify-between items-center">
                                <button
                                    onClick={(e) => handleEditClick(e, course)}
                                    className="bg-yellow-100 text-yellow-700 rounded px-3 py-1 text-sm font-semibold hover:bg-yellow-200 transition-all duration-200 mr-2"
                                >
                                    Edit
                                </button>
                                <span className="text-blue-600 hover:text-blue-800 font-semibold text-lg">Enter &rarr;</span>
                            </div>
                        </Link>
                    ))}
                </div>
            ) : (
                <div className="text-center py-16">
                    <p className="text-xl text-gray-500">You haven't created any course spaces yet.</p>
                    <p className="mt-2 text-gray-400">Get started by creating a new one!</p>
                </div>
            )}

            {/* Render the modal for creating a new course space */}
            <CreateCourseSpaceModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onCourseCreated={handleCourseCreated} />

            {/* Edit modal for editing a course space (must be outside the map and grid) */}
            <EditCourseSpaceModal
                isOpen={editModalOpen}
                onClose={() => { setEditModalOpen(false); setEditingCourse(null); }}
                initialName={editingCourse?.name || ''}
                initialDescription={editingCourse?.description || ''}
                onSave={handleEditSave}
            />
        </div>
    );
};

export default DashboardPage;
