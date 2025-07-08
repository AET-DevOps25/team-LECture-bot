
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import { apiClient, getCourses, updateCourseSpace } from '../api/apiClient';
import CourseSpaceModal from '../components/CourseSpaceModal';

const CourseListPage: React.FC = () => {

  const { token } = useAuth();
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [editingCourse, setEditingCourse] = useState<any | null>(null);
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


  // Handler for creating a new course space
  const handleCreateCourse = async ({ title, description }: { title: string; description: string }) => {
    // Backend expects { name, description }
    const body: any = { name: title, description };
    const { data, error } = await apiClient.POST('/coursespaces', { body });
    if (error || !data) throw new Error('Failed to create course space.');
    setCourses((prev) => [data, ...prev]);
    setModalOpen(false);
  };

  // Handler for editing an existing course space
  const handleEditCourse = async ({ title, description }: { title: string; description: string }) => {
    if (!editingCourse) return;
    const body = { name: title, description };
    const data = await updateCourseSpace(editingCourse.id, body);
    setCourses((prev) => prev.map((c) => (c.id === editingCourse.id ? data : c)));
    setModalOpen(false);
    setEditingCourse(null);
  };

  const openCreateModal = () => {
    setModalMode('create');
    setEditingCourse(null);
    setModalOpen(true);
  };

  const openEditModal = (course: any) => {
    setModalMode('edit');
    setEditingCourse(course);
    setModalOpen(true);
  };

  const handleModalCancel = () => {
    setModalOpen(false);
    setEditingCourse(null);
  };


  if (loading) return <div className="flex justify-center items-center h-64">Loading courses...</div>;
  if (error) return <div className="text-red-500 text-center mt-8">{error}</div>;


  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6 text-center">Your Courses</h1>
      <div className="flex justify-end mb-4">
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
          onClick={openCreateModal}
        >
          Create New Course Space
        </button>
      </div>
      <CourseSpaceModal
        isOpen={modalOpen}
        mode={modalMode}
        initialData={modalMode === 'edit' && editingCourse ? {
          title: editingCourse.name || editingCourse.title || '',
          description: editingCourse.description || ''
        } : undefined}
        onSubmit={modalMode === 'edit' ? handleEditCourse : handleCreateCourse}
        onCancel={handleModalCancel}
      />
      {courses.length === 0 ? (
        <div className="text-center text-gray-500 mt-12">No course spaces found. Click "Create New Course Space" to get started!</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {courses.map((course) => (
            <div
              key={course.id}
              className="bg-white rounded-lg shadow-md p-6 hover:bg-blue-50 border border-gray-200 transition-colors text-left flex flex-col gap-2"
            >
              <div className="flex justify-between items-start">
                <div className="flex-1 cursor-pointer" onClick={() => navigate(`/courses/${course.id}`)}>
                  <h2 className="text-xl font-semibold mb-2">{course.name || course.title}</h2>
                  <p className="text-gray-600">{course.description}</p>
                </div>
                <button
                  className="ml-2 px-2 py-1 text-sm bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200"
                  onClick={() => openEditModal(course)}
                  title="Edit Course Space"
                >
                  Edit
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CourseListPage;
