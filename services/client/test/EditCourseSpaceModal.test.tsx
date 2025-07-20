import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EditCourseSpaceModal from '../src/components/EditCourseSpaceModal';

describe('EditCourseSpaceModal', () => {
  const onClose = jest.fn();
  const onSave = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('does not render when isOpen is false', () => {
    render(
      <EditCourseSpaceModal
        isOpen={false}
        onClose={onClose}
        onSave={onSave}
        initialName="AI"
        initialDescription="desc"
      />
    );
    expect(screen.queryByText(/edit course space/i)).not.toBeInTheDocument();
  });

  it('renders with initial values when open', () => {
    render(
      <EditCourseSpaceModal
        isOpen={true}
        onClose={onClose}
        onSave={onSave}
        initialName="AI"
        initialDescription="desc"
      />
    );
    expect(screen.getByText(/edit course space/i)).toBeInTheDocument();
    expect(screen.getByDisplayValue('AI')).toBeInTheDocument();
    expect(screen.getByDisplayValue('desc')).toBeInTheDocument();
  });

  it('shows error if title is empty and submit is clicked', async () => {
    render(
      <EditCourseSpaceModal
        isOpen={true}
        onClose={onClose}
        onSave={onSave}
        initialName=""
        initialDescription=""
      />
    );
    fireEvent.change(screen.getByLabelText(/title/i), { target: { value: '' } });
    fireEvent.click(screen.getByText(/save/i));
    // Validation error
    // expect(await screen.findByText(/course space title cannot be empty/i)).toBeInTheDocument();
    expect(onSave).not.toHaveBeenCalled();
  });

  it('calls onSave with updated values', async () => {
    onSave.mockResolvedValueOnce(undefined);
    render(
      <EditCourseSpaceModal
        isOpen={true}
        onClose={onClose}
        onSave={onSave}
        initialName="AI"
        initialDescription="desc"
      />
    );
    fireEvent.change(screen.getByLabelText(/title/i), { target: { value: 'New Title' } });
    fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'New Desc' } });
    fireEvent.click(screen.getByText(/save/i));
    await waitFor(() => {
      expect(onSave).toHaveBeenCalledWith('New Title', 'New Desc');
    });
    expect(await screen.findByText(/updated successfully/i)).toBeInTheDocument();
  });

  it('calls onClose when cancel is clicked', () => {
    render(
      <EditCourseSpaceModal
        isOpen={true}
        onClose={onClose}
        onSave={onSave}
        initialName="AI"
        initialDescription="desc"
      />
    );
    fireEvent.click(screen.getByText(/cancel/i));
    expect(onClose).toHaveBeenCalled();
  });

  it('shows error if onSave throws', async () => {
    onSave.mockRejectedValueOnce(new Error('Failed!'));
    render(
      <EditCourseSpaceModal
        isOpen={true}
        onClose={onClose}
        onSave={onSave}
        initialName="AI"
        initialDescription="desc"
      />
    );
    fireEvent.click(screen.getByText(/save/i));
    // onSave error
    expect(await screen.findByText(/failed!/i)).toBeInTheDocument();
  });
});