# Use an official Python runtime as a parent image
FROM python:3.11-slim AS builder

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Install Poetry
RUN pip install poetry==1.7.1 # Or your preferred/latest compatible version

# Set the working directory in the container
WORKDIR /app

# Copy only necessary files for installing dependencies
COPY poetry.lock pyproject.toml ./

# Export dependencies to requirements.txt
# --without-hashes: Simplifies installation in Docker, though hashes add security.
# --no-dev: Excludes development dependencies.
# RUN poetry export -f requirements.txt --output requirements.txt --without-hashes --extras "standard"
RUN poetry export -f requirements.txt --output requirements.txt --without-hashes



RUN echo "--- requirements.txt content ---" && cat requirements.txt && echo "--- end requirements.txt ---"

# --- Second Stage: Final Image ---
FROM python:3.11-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
WORKDIR /app

# Copy requirements.txt from the builder stage
COPY --from=builder /app/requirements.txt .

# Install dependencies using pip
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application source code
COPY ./src/genai ./genai

# Expose the port the app runs on
EXPOSE 8001

# Define the command to run the application
# Note: We use 0.0.0.0 to bind to all available network interfaces in the container
CMD ["python", "-m", "uvicorn", "genai.main:app", "--host", "0.0.0.0", "--port", "8001"]
