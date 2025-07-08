
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getCourses } from '../api/apiClient';
import CreateCourseSpaceModal from '../components/CourseSpaceModal';

const CourseListPage: React.FC = () => {

  const { token } = useAuth();
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();


  useEffect(() => {
    if (!token) {
      setError('You must be logged in to view courses.');
      setLoading(false);
      return;
    }
    getCourses()
      .then(setCourses)
      .catch(() => setError('Failed to load courses.'))
      .finally(() => setLoading(false));
  }, [token]);

  const handleCourseCreated = (newCourse: any) => {
    setCourses((prev) => [newCourse, ...prev]);
    setModalOpen(false);
  };


  if (loading) return <div className="flex justify-center items-center h-64">Loading courses...</div>;
  if (error) return <div className="text-red-500 text-center mt-8">{error}</div>;


  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6 text-center">Your Courses</h1>
      <div className="flex justify-end mb-4">
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
          onClick={() => setModalOpen(true)}
        >
          Create New Course Space
        </button>
      </div>
      <CreateCourseSpaceModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onCourseCreated={handleCourseCreated}
      />
      {courses.length === 0 ? (
        <div className="text-center text-gray-500 mt-12">No course spaces found. Click "Create New Course Space" to get started!</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {courses.map((course) => (
            <button
              key={course.id}
              className="bg-white rounded-lg shadow-md p-6 hover:bg-blue-50 border border-gray-200 transition-colors text-left"
              onClick={() => navigate(`/courses/${course.id}`)}
            >
              <h2 className="text-xl font-semibold mb-2">{course.name || course.title}</h2>
              <p className="text-gray-600">{course.description}</p>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default CourseListPage;
