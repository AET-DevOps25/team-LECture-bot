import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import PdfUpload from '../src/components/PdfUpload';

// --- Mock setup ---
const mockGET = jest.fn().mockResolvedValue({ data: [] });
const mockPOST = jest.fn().mockResolvedValue({ response: { ok: true } });
const mockDELETE = jest.fn().mockResolvedValue({ response: { ok: true, status: 204 } });

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ courseSpaceId: 'abc123' }),
}));

jest.mock('openapi-fetch', () => () => ({
  use: jest.fn(),
  GET: mockGET,
  POST: mockPOST,
  DELETE: mockDELETE,
}));

jest.mock('../src/utils/storage', () => ({
  getItem: jest.fn(() => 'fake-jwt-token'),
}));

describe('PdfUpload', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders upload input and upload button', async () => {
    await act(async () => {
      render(<PdfUpload />);
    });
    expect(screen.getByText(/upload pdf files/i)).toBeInTheDocument();
    expect(screen.getByTestId('pdf-input')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /upload/i })).toBeDisabled();
  });

  it('enables upload button when a PDF is selected', async () => {
    await act(async () => {
      render(<PdfUpload />);
    });
    const file = new File(['dummy content'], 'test.pdf', { type: 'application/pdf' });
    const input = screen.getByTestId('pdf-input');
    await act(async () => {
      fireEvent.change(input, { target: { files: [file] } });
    });
    expect(screen.getByRole('button', { name: /upload/i })).not.toBeDisabled();
    expect(screen.getByText('test.pdf')).toBeInTheDocument();
  });

  it('ignores non-PDF files and shows alert', async () => {
    window.alert = jest.fn();
    await act(async () => {
      render(<PdfUpload />);
    });
    const file = new File(['dummy content'], 'test.txt', { type: 'text/plain' });
    const input = screen.getByTestId('pdf-input');
    await act(async () => {
      fireEvent.change(input, { target: { files: [file] } });
    });
    expect(window.alert).toHaveBeenCalledWith('Only PDF files are allowed. Some files were ignored.');
    expect(screen.getByRole('button', { name: /upload/i })).toBeDisabled();
  });

  it('shows "No documents uploaded yet." if no docs', async () => {
    await act(async () => {
      render(<PdfUpload />);
    });
    await waitFor(() => {
      expect(screen.getByText(/no documents uploaded yet/i)).toBeInTheDocument();
    });
  });

  it('shows error message if upload fails', async () => {
    mockPOST.mockResolvedValueOnce({ response: { ok: false }, error: { message: 'Upload failed' } });

    await act(async () => {
      render(<PdfUpload />);
    });
    const file = new File(['dummy content'], 'fail.pdf', { type: 'application/pdf' });
    const input = screen.getByTestId('pdf-input');
    await act(async () => {
      fireEvent.change(input, { target: { files: [file] } });
    });

    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: /upload/i }));
    });

    await waitFor(() => {
      expect(screen.getByText(/unexpected error/i)).toBeInTheDocument();
    });
  });

  it('shows uploaded documents if present', async () => {
    mockGET.mockResolvedValueOnce({
      data: [
        { id: '1', filename: 'doc1.pdf', uploadedAt: '2024-01-01' },
        { id: '2', filename: 'doc2.pdf', uploadedAt: '2024-01-02' },
      ],
    });

    await act(async () => {
      render(<PdfUpload />);
    });
    await waitFor(() => {
      expect(screen.getByText('doc1.pdf')).toBeInTheDocument();
      expect(screen.getByText('doc2.pdf')).toBeInTheDocument();
    });
  });

  it('calls delete API and removes document from list', async () => {
    mockGET.mockResolvedValueOnce({
      data: [
        { id: '1', filename: 'doc1.pdf', uploadedAt: '2024-01-01' },
      ],
    });

    await act(async () => {
      render(<PdfUpload />);
    });
    await waitFor(() => {
      expect(screen.getByText('doc1.pdf')).toBeInTheDocument();
    });

    // Simulate delete button click
    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: /delete/i }));
    });

    // Wait for removal
    await waitFor(() => {
      expect(screen.queryByText('doc1.pdf')).not.toBeInTheDocument();
    });
  });
});