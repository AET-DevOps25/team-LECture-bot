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
          id: doc.id || doc.documentId || doc._id || doc.filename,
          name: doc.filename,
          uploadedAt: doc.uploadDate || doc.createdAt || '',
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
    fetchUploadedDocs();
    // eslint-disable-next-line
  }, [courseSpaceId]);

  // Delete document
  const handleDelete = async (docId: string) => {
    setDeleteStatus(prev => ({ ...prev, [docId]: 'deleting' }));
    try {
      const response = await documentApiClient.DELETE("/documents/{courseSpaceId}/{id}", {
        params: { path: { courseSpaceId: courseSpaceId!, id: docId } },
      });
      if (response.response?.ok || response.response?.status === 204) {
        setUploadedDocs(prev => prev.filter(doc => doc.id !== docId));
        setDeleteStatus(prev => ({ ...prev, [docId]: 'idle' }));
      } else {
        setDeleteStatus(prev => ({ ...prev, [docId]: 'error' }));
      }
    } catch {
      setDeleteStatus(prev => ({ ...prev, [docId]: 'error' }));
    }
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
        // Remove the file from the upload list after a longer delay (5 seconds)
        setTimeout(() => {
          setFiles(prev => prev.filter((_, i) => i !== idx));
        }, 5000);
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
      <button
        onClick={handleUpload}
        disabled={files.length === 0}
        style={{
          padding: '8px 20px',
          background: files.length === 0 ? '#ccc' : '#2563eb',
          color: '#fff',
          border: 'none',
          borderRadius: 6,
          fontWeight: 500,
          fontSize: '1rem',
          cursor: files.length === 0 ? 'not-allowed' : 'pointer',
          margin: '10px 0 20px 0',
          transition: 'background 0.2s',
          boxShadow: files.length === 0 ? 'none' : '0 1px 4px rgba(37,99,235,0.08)'
        }}
        onMouseOver={e => {
          if (files.length !== 0) (e.currentTarget as HTMLButtonElement).style.background = '#1d4ed8';
        }}
        onMouseOut={e => {
          if (files.length !== 0) (e.currentTarget as HTMLButtonElement).style.background = '#2563eb';
        }}
      >
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
                <span style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: 0,
                  minWidth: 0,
                  width: '100%',
                  maxWidth: 500
                }}>
                  <span style={{ color: 'red', flex: 1, minWidth: 0, overflowWrap: 'anywhere' }}>{fileStatus.message}</span>
                  <button
                    aria-label="Dismiss error"
                    title="Dismiss"
                    style={{
                      marginLeft: 16,
                      padding: '4px 16px',
                      background: '#ef4444',
                      color: '#fff',
                      border: 'none',
                      borderRadius: 6,
                      fontWeight: 500,
                      fontSize: '1rem',
                      cursor: 'pointer',
                      lineHeight: 1.2,
                      transition: 'background 0.2s',
                      boxShadow: '0 1px 4px rgba(239,68,68,0.10)'
                    }}
                    onClick={() => setFiles(prev => prev.filter((_, i) => i !== idx))}
                    onMouseOver={e => (e.currentTarget as HTMLButtonElement).style.background = '#b91c1c'}
                    onMouseOut={e => (e.currentTarget as HTMLButtonElement).style.background = '#ef4444'}
                  >
                    ×
                  </button>
                </span>
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
          <colgroup>
            <col style={{ maxWidth: 220, width: '50%' }} />
            <col style={{ maxWidth: 180, width: '35%' }} />
            <col style={{ maxWidth: 80, width: '15%' }} />
          </colgroup>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              <th style={{ textAlign: 'center', padding: 8, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>Name</th>
              <th style={{ textAlign: 'center', padding: 8, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>Uploaded</th>
              <th style={{ textAlign: 'center', padding: 8, whiteSpace: 'nowrap' }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {uploadedDocs.map(doc => (
              <tr key={doc.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: 8, maxWidth: 220, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={doc.name}>{doc.name}</td>
                <td style={{ padding: 8, maxWidth: 180, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleString() : '-'}</td>
                <td style={{ padding: 8, maxWidth: 80 }}>
                  <button
                    onClick={() => handleDelete(doc.id)}
                    disabled={deleteStatus[doc.id] === 'deleting'}
                    style={{
                      padding: '6px 16px',
                      background: deleteStatus[doc.id] === 'deleting' ? '#fca5a5' : '#ef4444',
                      color: '#fff',
                      border: 'none',
                      borderRadius: 6,
                      fontWeight: 500,
                      fontSize: '0.97rem',
                      cursor: deleteStatus[doc.id] === 'deleting' ? 'not-allowed' : 'pointer',
                      transition: 'background 0.2s',
                      boxShadow: deleteStatus[doc.id] === 'deleting' ? 'none' : '0 1px 4px rgba(239,68,68,0.08)'
                    }}
                    onMouseOver={e => {
                      if (deleteStatus[doc.id] !== 'deleting') (e.currentTarget as HTMLButtonElement).style.background = '#b91c1c';
                    }}
                    onMouseOut={e => {
                      if (deleteStatus[doc.id] !== 'deleting') (e.currentTarget as HTMLButtonElement).style.background = '#ef4444';
                    }}
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