import React from 'react';

export type ToastType = 'success' | 'error';

interface ToastProps {
  message: string;
  type?: ToastType;
  onClose: () => void;
}

const Toast: React.FC<ToastProps> = ({ message, type = 'success', onClose }) => {
  return (
    <div
      className={`fixed top-6 right-6 z-50 px-6 py-4 rounded shadow-lg text-white transition-all duration-300
        ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`}
      role="alert"
    >
      <div className="flex items-center justify-between gap-4">
        <span>{message}</span>
        <button
          onClick={onClose}
          className="ml-4 text-white hover:text-gray-200 focus:outline-none"
          aria-label="Close notification"
        >
          &times;
        </button>
      </div>
    </div>
  );
};

export default Toast;
