import React, { useState } from 'react';
import type { ChangeEvent } from 'react';
import { useParams } from 'react-router-dom';
import createClient from "openapi-fetch";
import type { paths } from '../shared/api/generated/document-api';
import storage from '../utils/storage';

interface FileStatus {
  file: File;
  progress: number;
  status: 'idle' | 'uploading' | 'success' | 'error';
  message?: string;
}

const PdfUpload: React.FC = () => {
  const { courseSpaceId } = useParams<{ courseSpaceId: string }>(); // <-- Get from URL
  const [files, setFiles] = useState<FileStatus[]>([]);

  // Create document API client
  const documentApiClient = createClient<paths>({
    baseUrl: 'http://localhost:8080/api/v1',
  });

  // Add auth interceptor
  documentApiClient.use({
    async onRequest({ request }) {
      const token = storage.getItem<string>('jwtToken');
      if (token) {
        request.headers.set("Authorization", `Bearer ${token}`);
      }
      return request;
    },
  });

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

  // Upload a single file using multipart/form-data
  const uploadFile = async (fileStatus: FileStatus, idx: number) => {
    setFiles(prev =>
      prev.map((f, i) =>
        i === idx ? { ...f, status: 'uploading', progress: 0, message: undefined } : f
      )
    );

    const formData = new FormData();
    formData.append('files', fileStatus.file);

    try {
      const response = await documentApiClient.POST("/documents/{courseSpaceId}", {
        params: {
          path: {
            courseSpaceId: courseSpaceId!
          }
        },
        body: formData as any, // FormData bypass for file upload
      });

      // Check for both error field and HTTP status
      if (response.error || !response.response?.ok) {
        const errorMessage = `HTTP ${response.response?.status || 'Unknown'}: Upload failed`;
        setFiles(prev =>
          prev.map((f, i) =>
            i === idx
              ? { ...f, status: 'error', message: errorMessage }
              : f
          )
        );
      } else {
        setFiles(prev =>
          prev.map((f, i) =>
            i === idx
              ? { ...f, status: 'success', progress: 100, message: 'Upload successful!' }
              : f
          )
        );
      }
    } catch (err: any) {
      setFiles(prev =>
        prev.map((f, i) =>
          i === idx
            ? { ...f, status: 'error', message: err.message || 'Upload failed' }
            : f
        )
      );
    }
  };

  const handleUpload = () => {
    files.forEach((fileStatus, idx) => {
      if (fileStatus.status === 'idle') uploadFile(fileStatus, idx);
    });
  };

  return (
    <div className="pdf-upload">
      <h2>Upload PDF Files</h2>
      <div style={{ color: '#555', fontSize: '0.95rem', marginBottom: 12 }}>
        <strong>Note:</strong> Only PDF files smaller than <strong>50MB</strong> each can be uploaded.<br />
        The total size of all files in one upload must not exceed <strong>50MB</strong>.
      </div>
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
            <div style={{ width: '100%', maxWidth: 500, background: '#eee', margin: '5px auto', borderRadius: 4 }}>
              <div
                style={{
                  width: `${fileStatus.progress}%`,
                  background: fileStatus.status === 'error' ? 'red' : 'green',
                  height: 8,
                  transition: 'width 0.3s',
                  borderRadius: 4,
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