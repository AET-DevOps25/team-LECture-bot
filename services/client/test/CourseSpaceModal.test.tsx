import { render, screen, fireEvent, act } from '@testing-library/react';
import CreateCourseSpaceModal from '../src/components/CourseSpaceModal';

describe('CreateCourseSpaceModal', () => {
  const onClose = jest.fn();
  const onCourseCreated = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('does not render when isOpen is false', () => {
    render(
      <CreateCourseSpaceModal isOpen={false} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    expect(screen.queryByText(/create new course space/i)).not.toBeInTheDocument();
  });

  it('renders when isOpen is true', () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    expect(screen.getByText(/create new course space/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/title/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
  });

  it('shows error if title is empty and submit is clicked', async () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: /^create$/i }));
    });
    expect(onCourseCreated).not.toHaveBeenCalled();
  });

  it('calls onCourseCreated with title and description', () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    fireEvent.change(screen.getByLabelText(/title/i), { target: { value: 'My Course' } });
    fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'A description' } });
    fireEvent.click(screen.getByRole('button', { name: /^create$/i }));
    expect(onCourseCreated).toHaveBeenCalledWith('My Course', 'A description');
  });

  it('calls onClose when cancel is clicked', () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    fireEvent.click(screen.getByText(/cancel/i));
    expect(onClose).toHaveBeenCalled();
  });

  it('disables the Create button if title is empty', () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    const createButton = screen.getByRole('button', { name: /^create$/i });
    expect(createButton).toBeDisabled();
  });

  it('enables the Create button if title is not empty', () => {
    render(
      <CreateCourseSpaceModal isOpen={true} onClose={onClose} onCourseCreated={onCourseCreated} />
    );
    fireEvent.change(screen.getByLabelText(/title/i), { target: { value: 'Some Title' } });
    const createButton = screen.getByRole('button', { name: /^create$/i });
    expect(createButton).not.toBeDisabled();
  });
});