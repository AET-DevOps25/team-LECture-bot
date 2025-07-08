import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import type { components } from '../shared/api/generated/api';
import CreateCourseSpaceModal from '../components/CourseSpaceModal';

type CourseSpace = components['schemas']['CourseSpaceDto'];


import { updateCourseSpace } from '../api/apiClient';

const DashboardPage: React.FC = () => {
    const [courses, setCourses] = useState<CourseSpace[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
    const [editingCourse, setEditingCourse] = useState<CourseSpace | null>(null);


    useEffect(() => {
        const fetchCourses = async () => {
            try {
                const { data, error } = await apiClient.GET('/coursespaces');
                if (error) {
                    throw new Error('Failed to fetch course spaces.');
                }
                setCourses(data || []);
            } catch (err) {
                setError((err as Error).message);
            } finally {
                setLoading(false);
            }
        };

        fetchCourses();
    }, []);





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

            // If successful, remove the course space from the UI
            setCourses(prevCourses => prevCourses.filter(course => course.id !== courseSpaceId));

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
                    onClick={() => {
                        setModalMode('create');
                        setEditingCourse(null);
                        setIsModalOpen(true);
                    }}
                    className='px-5 py-3 font-bold text-white bg-green-500 rounded-lg shadow hover:bg-green-600 transition-colors'
                >
                    ✨ Create New Space
                </button>
            </div>
            {courses.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {courses.map((course) => (
                        <div key={course.id} className="group relative block p-6 bg-white border border-gray-200 rounded-xl shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300">
                            <Link
                                to={`/coursespaces/${course.id}`}
                                className="block"
                            >
                                <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900">{course.title}</h5>
                                <p className="font-normal text-gray-600">Click to enter this course space and start learning.</p>
                                <div className="mt-4 flex justify-end">
                                    <span className="text-blue-600 hover:text-blue-800 font-semibold text-lg">Enter &rarr;</span>
                                </div>
                            </Link>
                            <button
                                onClick={(e) => handleDelete(e, course.id!)}
                                className="absolute top-4 right-4 bg-red-100 text-red-600 rounded-full h-8 w-8 flex items-center justify-center opacity-0 group-hover:opacity-100 hover:bg-red-200 transition-all duration-200 z-10"
                                aria-label="Delete course space"
                            >
                                &times;
                            </button>
                            <button
                                onClick={() => {
                                    setModalMode('edit');
                                    setEditingCourse(course);
                                    setIsModalOpen(true);
                                }}
                                className="absolute top-4 left-4 bg-yellow-100 text-yellow-800 rounded-full h-8 w-8 flex items-center justify-center opacity-0 group-hover:opacity-100 hover:bg-yellow-200 transition-all duration-200 z-10"
                                aria-label="Edit course space"
                                title="Edit Course Space"
                            >
                                ✎
                            </button>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="text-center py-16">
                    <p className="text-xl text-gray-500">You haven't created any course spaces yet.</p>
                    <p className="mt-2 text-gray-400">Get started by creating a new one!</p>
                </div>
            )}

            {/* Render the modal for creating or editing a course space */}
            <CreateCourseSpaceModal
                isOpen={isModalOpen}
                mode={modalMode}
                initialData={modalMode === 'edit' && editingCourse ? {
                    title: editingCourse.title ?? '',
                    description: editingCourse.description || ''
                } : undefined}
                onCancel={() => {
                    setIsModalOpen(false);
                    setEditingCourse(null);
                }}
                onSubmit={async ({ title, description }) => {
                    if (modalMode === 'edit' && editingCourse) {
                        const updated = await updateCourseSpace(editingCourse.id!, { title, description });
                        setCourses((prevCourses) => prevCourses.map((c) => c.id === editingCourse.id ? updated : c));
                        setIsModalOpen(false);
                        setEditingCourse(null);
                    } else {
                        // OpenAPI expects { title, description }
                        const body: any = { title, description };
                        const { data, error } = await apiClient.POST('/coursespaces', { body });
                        if (error || !data) throw new Error('Failed to create course space.');
                        setCourses((prevCourses) => [...prevCourses, data]);
                        setIsModalOpen(false);
                    }
                }}
            />

        </div>
    );
};

export default DashboardPage;
