import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/apiClient';
import type { components } from '../shared/api/generated/api';

type CourseSpace = components['schemas']['CourseSpace'];

const DashboardPage: React.FC = () => {
    const [courses, setCourses] = useState<CourseSpace[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

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

    if (loading) return <div>Loading courses...</div>;
    if (error) return <div className="text-red-500">Error: {error}</div>;

    return (
        <div className="container mx-auto">
            <h1 className="text-3xl font-bold mb-6">My Course Spaces</h1>
            {courses.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {courses.map((course) => (
                        <Link
                            key={course.id}
                            to={`/coursespaces/${course.id}`}
                            className="block p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100"
                        >
                            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900">{course.name}</h5>
                            <p className="font-normal text-gray-700">Click to view details.</p>
                        </Link>
                    ))}
                </div>
            ) : (
                <p>You have not created or joined any course spaces yet.</p>
            )}
        </div>
    );
};

export default DashboardPage;