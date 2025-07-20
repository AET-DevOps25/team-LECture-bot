import { render, screen, fireEvent } from '@testing-library/react';
import QnAChat from '../src/components/QnAChat';

// Mock storage.getItem
jest.mock('../src/utils/storage', () => ({
  getItem: jest.fn(() => 'fake-jwt-token'),
}));

// Mock scrollIntoView for jsdom
window.HTMLElement.prototype.scrollIntoView = function() {};

describe('QnAChat', () => {
  const courseSpaceId = 'course-123';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders input and ask button', () => {
    render(<QnAChat courseSpaceId={courseSpaceId} />);
    expect(screen.getByPlaceholderText(/type your question/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /ask/i })).toBeInTheDocument();
  });

  it('disables ask button when input is empty', () => {
    render(<QnAChat courseSpaceId={courseSpaceId} />);
    expect(screen.getByRole('button', { name: /ask/i })).toBeDisabled();
  });

  it('shows prompt when no history', () => {
    render(<QnAChat courseSpaceId={courseSpaceId} />);
    expect(screen.getByText(/ask a question about your course materials/i)).toBeInTheDocument();
  });

  it('shows loading indicator when submitting', async () => {
    // Mock fetch to delay
    global.fetch = jest.fn(() =>
      new Promise(resolve =>
        setTimeout(() =>
          resolve({
            ok: true,
            json: async () => ({ answerText: '42', citations: [] }),
          }), 100)
      )
    ) as any;

    render(<QnAChat courseSpaceId={courseSpaceId} />);
    fireEvent.change(screen.getByPlaceholderText(/type your question/i), { target: { value: 'What is the answer?' } });
    fireEvent.click(screen.getByRole('button', { name: /ask/i }));

    expect(await screen.findByText(/generating answer/i)).toBeInTheDocument();

    // Clean up fetch
    (global.fetch as any).mockClear();
    delete (global as any).fetch;
  });

  it('shows answer and question in chat history after submit', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: async () => ({
          answerText: 'The answer is 42.',
          citations: [
            {
              document_id: 'doc1',
              chunk_id: 2,
              document_name: 'Doc Name',
              retrieved_text_preview: 'Preview text',
            },
          ],
        }),
      })
    ) as any;

    render(<QnAChat courseSpaceId={courseSpaceId} />);
    fireEvent.change(screen.getByPlaceholderText(/type your question/i), { target: { value: 'What is the answer?' } });
    fireEvent.click(screen.getByRole('button', { name: /ask/i }));

    expect(await screen.findByText(/the answer is 42/i)).toBeInTheDocument();
    expect(screen.getByText(/you:/i)).toBeInTheDocument();
    expect(screen.getByText(/answer:/i)).toBeInTheDocument();
    expect(screen.getByText(/doc1 \(chunk 2\)/i)).toBeInTheDocument();
    expect(screen.getByText(/doc name/i)).toBeInTheDocument();
    expect(screen.getByText(/“preview text”/i)).toBeInTheDocument();

    (global.fetch as any).mockClear();
    delete (global as any).fetch;
  });

  it('shows error message if fetch fails', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        json: async () => ({ error: 'Something went wrong' }),
      })
    ) as any;

    render(<QnAChat courseSpaceId={courseSpaceId} />);
    fireEvent.change(screen.getByPlaceholderText(/type your question/i), { target: { value: 'fail?' } });
    fireEvent.click(screen.getByRole('button', { name: /ask/i }));

    expect(await screen.findByText(/something went wrong/i)).toBeInTheDocument();

    (global.fetch as any).mockClear();
    delete (global as any).fetch;
  });
});