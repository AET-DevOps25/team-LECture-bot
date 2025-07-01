import React, { useState, ChangeEvent } from 'react';

interface FileStatus {
  file: File;
  progress: number;
  status: 'idle' | 'uploading' | 'success' | 'error';
  message?: string;
}

const PdfUpload: React.FC = () => {
  const [files, setFiles] = useState<FileStatus[]>([]);

  // Handle file selection and filter for PDFs
  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || []);
    const pdfFiles = selectedFiles.filter(file => file.type === 'application/pdf');
    const rejected = selectedFiles.filter(file => file.type !== 'application/pdf');
    if (rejected.length > 0) {
      alert('Only PDF files are allowed. Some files were ignored.');
    }
    setFiles(pdfFiles.map(file => ({
      file,
      progress: 0,
      status: 'idle',
    })));
  };

  // Simulate upload (replace with real API call)
  const uploadFile = (fileStatus: FileStatus, idx: number) => {
    setFiles(prev =>
      prev.map((f, i) =>
        i === idx ? { ...f, status: 'uploading', progress: 0 } : f
      )
    );
    // Simulate progress
    const interval = setInterval(() => {
      setFiles(prev =>
        prev.map((f, i) =>
          i === idx
            ? {
                ...f,
                progress: Math.min(f.progress + 20, 100),
                status: f.progress + 20 >= 100 ? 'success' : 'uploading',
                message: f.progress + 20 >= 100 ? 'Upload successful!' : undefined,
              }
            : f
        )
      );
      if (fileStatus.progress + 20 >= 100) clearInterval(interval);
    }, 400);
    // In real use, replace with actual upload logic and update progress/status accordingly
  };

  const handleUpload = () => {
    files.forEach((fileStatus, idx) => {
      if (fileStatus.status === 'idle') uploadFile(fileStatus, idx);
    });
  };

  return (
    <div className="pdf-upload">
      <h2>Upload PDF Files</h2>
      <input
        type="file"
        accept="application/pdf"
        multiple
        onChange={handleFileChange}
        data-testid="pdf-input"
      />
      <button onClick={handleUpload} disabled={files.length === 0}>
        Upload
      </button>
      <div>
        {files.map((fileStatus, idx) => (
          <div key={fileStatus.file.name + idx} style={{ margin: '10px 0' }}>
            <strong>{fileStatus.file.name}</strong>
            <div style={{ width: 200, background: '#eee', margin: '5px 0' }}>
              <div
                style={{
                  width: `${fileStatus.progress}%`,
                  background: fileStatus.status === 'error' ? 'red' : 'green',
                  height: 8,
                  transition: 'width 0.3s',
                }}
              />
            </div>
            <span>
              {fileStatus.status === 'uploading' && 'Uploading...'}
              {fileStatus.status === 'success' && (
                <span style={{ color: 'green' }}>{fileStatus.message}</span>
              )}
              {fileStatus.status === 'error' && (
                <span style={{ color: 'red' }}>{fileStatus.message}</span>
              )}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PdfUpload;