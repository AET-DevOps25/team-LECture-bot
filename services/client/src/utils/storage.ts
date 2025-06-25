/**
 * A utility for interacting with localStorage in a safe and structured way.
 */

const storage = {
  /**
   * Retrieves an item from localStorage and parses it as JSON.
   * Includes type-safety with generics.
   *
   * @param key The key of the item to retrieve from localStorage.
   * @returns The retrieved item, parsed as type T, or null if not found or in a non-browser environment.
   */
  getItem: <T>(key: string): T | null => {
    // Check if we are in a browser environment
    if (typeof window === 'undefined') {
      return null;
    }
    try {
      const item = window.localStorage.getItem(key);
      // Parse stored json or return null if it doesn't exist
      return item ? (JSON.parse(item) as T) : null;
    } catch (error) {
      console.warn(`Error reading localStorage key “${key}”:`, error);
      return null;
    }
  },

  /**
   * Adds an item to localStorage. The item will be stringified as JSON.
   *
   * @param key The key under which to store the item.
   * @param value The value to store (will be stringified).
   */
  setItem: <T>(key: string, value: T): void => {
    // Check if we are in a browser environment
    if (typeof window === 'undefined') {
      console.warn(`Tried setting localStorage key “${key}” in a non-browser environment.`);
      return;
    }
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.warn(`Error setting localStorage key “${key}”:`, error);
    }
  },

  /**
   * Removes an item from localStorage.
   *
   * @param key The key of the item to remove.
   */
  removeItem: (key: string): void => {
    // Check if we are in a browser environment
    if (typeof window === 'undefined') {
      console.warn(`Tried removing localStorage key “${key}” in a non-browser environment.`);
      return;
    }
    try {
      window.localStorage.removeItem(key);
    } catch (error) {
      console.warn(`Error removing localStorage key “${key}”:`, error);
    }
  },
};

export default storage;