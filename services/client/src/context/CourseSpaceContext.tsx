import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { apiClient } from '../api/apiClient';
import type { components } from '../shared/api/generated/api';
import { useAuth } from "./AuthContext";

// Define the type for a single course space
type CourseSpace = components['schemas']['CourseSpaceDto'];

// Define the shape of your context data
interface CourseSpaceContextType {
    courseSpaces: CourseSpace[];
    loading: boolean;
    error: string | null;
    fetchCourseSpaces: () => Promise<void>;
    createCourseSpace: (name: string, description: string) => Promise<CourseSpace | null>;
}

// Create the context with a default undefined value
const CourseSpaceContext = createContext<CourseSpaceContextType | undefined>(undefined);

// Create the Provider component
export const CourseSpaceProvider = ({ children }: { children: ReactNode }) => {
    const { isAuthenticated } = useAuth();
    const [courseSpaces, setCourseSpaces] = useState<CourseSpace[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Function to fetch all course spaces
    const fetchCourseSpaces = async () => {
        try {
            setLoading(true);
            const { data, error: fetchError } = await apiClient.GET('/coursespaces');
            if (fetchError) throw new Error('Failed to fetch course spaces.');
            setCourseSpaces(data || []);
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setLoading(false);
        }
    };

    // Function to create a new course space
    const createCourseSpace = async (name: string, description: string): Promise<CourseSpace | null> => {
        try {
            const { data, error: createError } = await apiClient.POST('/coursespaces', {
                body: { name, description },
            });

            if (createError || !data) {
                throw new Error('Failed to create course space.');
            }

            // Add the new course space to our local state
            setCourseSpaces((prev) => [...prev, data]);
            return data; // Return the newly created course space

        } catch (err) {
            setError((err as Error).message);
            return null;
        }
    };

    // Fetch courses on initial load
    useEffect(() => {
        if (isAuthenticated) {
            fetchCourseSpaces();
        } else {
            setCourseSpaces([]);
        }
    }, [isAuthenticated]);

    const value = { courseSpaces, loading, error, fetchCourseSpaces, createCourseSpace };

    return (
        <CourseSpaceContext.Provider value={value}>
            {children}
        </CourseSpaceContext.Provider>
    );
};

// Create a custom hook for easy access to the context
export const useCourseSpaces = () => {
    const context = useContext(CourseSpaceContext);
    if (context === undefined) {
        throw new Error('useCourseSpaces must be used within a CourseSpaceProvider');
    }
    return context;
};
