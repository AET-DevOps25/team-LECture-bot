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

interface UploadedDocument {
  id: string;
  name: string;
  size: number;
  uploadedAt: string;
}


const PdfUpload: React.FC = () => {
  const { courseSpaceId } = useParams<{ courseSpaceId: string }>();
  const [files, setFiles] = useState<FileStatus[]>([]);
  const [uploadedDocs, setUploadedDocs] = useState<UploadedDocument[]>([]);
  const [loadingDocs, setLoadingDocs] = useState(false);
  const [deleteStatus, setDeleteStatus] = useState<{ [id: string]: 'idle' | 'deleting' | 'error' }>({});

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

  // Fetch uploaded documents
  const fetchUploadedDocs = async () => {
    if (!courseSpaceId) return;
    setLoadingDocs(true);
    try {
      const response = await documentApiClient.GET("/documents/{courseSpaceId}", {
        params: { path: { courseSpaceId } },
      });
      if (response.data && Array.isArray(response.data)) {
        setUploadedDocs(response.data.map((doc: any) => ({
          id: doc.id || doc.documentId || doc._id || doc.name, // fallback for id
          name: doc.name,
          size: doc.size,
          uploadedAt: doc.uploadedAt || doc.createdAt || '',
        })));
      } else {
        setUploadedDocs([]);
      }
    } catch (e) {
      setUploadedDocs([]);
    } finally {
      setLoadingDocs(false);
    }
  };

  React.useEffect(() => {
    // Inject sample documents for frontend preview
    setUploadedDocs([
      {
        id: 'sample1',
        name: 'Lecture1-Intro.pdf',
        size: 1048576,
        uploadedAt: new Date(Date.now() - 86400000).toISOString(),
      },
      {
        id: 'sample2',
        name: 'Assignment2.pdf',
        size: 524288,
        uploadedAt: new Date(Date.now() - 3600 * 1000 * 5).toISOString(),
      },
      {
        id: 'sample3',
        name: 'Project-Report.pdf',
        size: 3145728,
        uploadedAt: new Date().toISOString(),
      },
    ]);
    // fetchUploadedDocs();
    // eslint-disable-next-line
  }, [courseSpaceId]);

  // Delete document
  const handleDelete = async (docId: string) => {
    setDeleteStatus(prev => ({ ...prev, [docId]: 'deleting' }));
    // try {
    //   const response = await documentApiClient.DELETE("/documents/{courseSpaceId}/{documentId}", {
    //     params: { path: { courseSpaceId: courseSpaceId!, documentId: docId } },
    //   });
    //   if (response.response?.ok) {
    //     setUploadedDocs(prev => prev.filter(doc => doc.id !== docId));
    //     setDeleteStatus(prev => ({ ...prev, [docId]: 'idle' }));
    //   } else {
    //     setDeleteStatus(prev => ({ ...prev, [docId]: 'error' }));
    //   }
    // } catch {
    //   setDeleteStatus(prev => ({ ...prev, [docId]: 'error' }));
    // }
  };

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

      let errorMessage = '';
      const status = response.response?.status;
      if (response.error || !response.response?.ok) {
        switch (status) {
          case 400:
            errorMessage = 'Invalid input. Please check your file(s) and try again.';
            break;
          case 409:
            errorMessage = 'This document was already uploaded in this course space.';
            break;
          case 422:
            errorMessage = 'PDF cannot be processed. It may be empty, unreadable (scanned), or use an unsupported encoding.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = `Unexpected error (HTTP ${status || 'Unknown'}). Please contact support.`;
        }
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
        // Refresh uploaded docs after successful upload
        fetchUploadedDocs();
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

      <hr style={{ margin: '32px 0 16px 0' }} />
      <h3>Uploaded Documents</h3>
      {loadingDocs ? (
        <div>Loading documents...</div>
      ) : uploadedDocs.length === 0 ? (
        <div style={{ color: '#888' }}>No documents uploaded yet.</div>
      ) : (
        <table style={{ width: '100%', maxWidth: 600, borderCollapse: 'collapse', margin: '0 auto' }}>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              <th style={{ textAlign: 'left', padding: 8 }}>Name</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Size</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Uploaded</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {uploadedDocs.map(doc => (
              <tr key={doc.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: 8 }}>{doc.name}</td>
                <td style={{ padding: 8, textAlign: 'left' }}>{(doc.size / 1024 / 1024).toFixed(2)} MB</td>
                <td style={{ padding: 8 }}>{doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleString() : '-'}</td>
                <td style={{ padding: 8 }}>
                  <button
                    onClick={() => handleDelete(doc.id)}
                    disabled={deleteStatus[doc.id] === 'deleting'}
                    style={{ color: 'red', border: 'none', background: 'none', cursor: 'pointer' }}
                  >
                    {deleteStatus[doc.id] === 'deleting' ? 'Deleting...' : 'Delete'}
                  </button>
                  {deleteStatus[doc.id] === 'error' && (
                    <span style={{ color: 'red', marginLeft: 8 }}>Delete failed</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default PdfUpload;