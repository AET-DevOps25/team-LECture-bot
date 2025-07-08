import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import { apiClient, getCourses } from '../api/apiClient';
import CourseSpaceModal from '../components/CourseSpaceModal';
import Toast from '../components/Toast';

const CourseListPage: React.FC = () => {

  const { token } = useAuth();
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [editingCourse, setEditingCourse] = useState<any | null>(null);
  const [deletingCourse, setDeletingCourse] = useState<any | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  // Handler for deleting a course space
  const handleDeleteCourse = async () => {
    if (!deletingCourse) return;
    setDeleteLoading(true);
    setDeleteError(null);
    try {
      const res = await apiClient.DELETE('/coursespaces/{courseSpaceId}', { params: { path: { courseSpaceId: deletingCourse.id } } });
      if (res.error) throw new Error('Failed to delete course space.');
      setCourses((prev) => prev.filter((c) => c.id !== deletingCourse.id));
      setDeletingCourse(null);
      setToast({ message: 'Course space deleted successfully.', type: 'success' });
    } catch (err: any) {
      setDeleteError(err.message || 'An error occurred.');
      setToast({ message: err.message || 'An error occurred.', type: 'error' });
    } finally {
      setDeleteLoading(false);
    }
  };
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
    // OpenAPI expects { title, description }
    const body: any = { title, description };
    const { data, error } = await apiClient.POST('/coursespaces', { body });
    if (error || !data) {
      setToast({ message: 'Failed to create course space.', type: 'error' });
      throw new Error('Failed to create course space.');
    }
    setCourses((prev) => [data, ...prev]);
    setModalOpen(false);
    setToast({ message: 'Course space created successfully.', type: 'success' });
  };

  // Handler for editing an existing course space
  // Use the OpenAPI client for PUT (edit) operation
  const handleEditCourse = async ({ title, description }: { title: string; description: string }) => {
    if (!editingCourse) return;
    const body = { title, description };
    const res = await apiClient.PUT('/coursespaces/{courseSpaceId}' as any, {
      params: { path: { courseSpaceId: editingCourse.id } },
      body
    });
    if (res.error || !res.data) {
      setToast({ message: 'Failed to update course space.', type: 'error' });
      throw new Error('Failed to update course space.');
    }
    setCourses((prev) => prev.map((c) => (c.id === editingCourse.id ? res.data : c)));
    setModalOpen(false);
    setEditingCourse(null);
    setToast({ message: 'Course space updated successfully.', type: 'success' });
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
      {/* Delete Confirmation Modal */}
      {deletingCourse && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center">
          <div className="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Delete Course Space</h2>
            <p className="mb-4">Are you sure you want to delete <span className="font-semibold">{deletingCourse.title}</span>? This action cannot be undone.</p>
            {deleteError && <p className="text-red-500 text-sm mb-2">{deleteError}</p>}
            <div className="flex justify-end gap-4">
              <button
                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
                onClick={() => setDeletingCourse(null)}
                disabled={deleteLoading}
              >
                Cancel
              </button>
              <button
                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50"
                onClick={handleDeleteCourse}
                disabled={deleteLoading}
              >
                {deleteLoading ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
      <CourseSpaceModal
        isOpen={modalOpen}
        mode={modalMode}
        initialData={modalMode === 'edit' && editingCourse ? {
          title: editingCourse.title ?? '',
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
                  <h2 className="text-xl font-semibold mb-2">{course.title}</h2>
                  <p className="text-gray-600">{course.description}</p>
                </div>
                <div className="flex gap-2">
                  <button
                    className="px-2 py-1 text-sm bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200"
                    onClick={() => openEditModal(course)}
                    title="Edit Course Space"
                  >
                    Edit
                  </button>
                  <button
                    className="px-2 py-1 text-sm bg-red-100 text-red-800 rounded hover:bg-red-200"
                    onClick={() => setDeletingCourse(course)}
                    title="Delete Course Space"
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
};

export default CourseListPage;
