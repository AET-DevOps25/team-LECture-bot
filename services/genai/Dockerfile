# Use an official Python runtime as a parent image
FROM python:3.11-slim

# Set the working directory in the container
# Set environment variables to optimize python and poetry
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV POETRY_NO_INTERACTION=1 \
    POETRY_VIRTUALENVS_CREATE=false
ENV PYTHONPATH=/app/src

WORKDIR /app

# Install system dependencies, including curl for the healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Install poetry
RUN pip install --no-cache-dir poetry

# Copy only the dependency files to leverage Docker cache
COPY poetry.lock pyproject.toml ./

# Install dependencies
RUN poetry install --no-root --no-interaction --no-ansi

# Copy the rest of the application code
COPY ./src ./src

# Command to run the application
CMD ["poetry", "run", "uvicorn", "src.genai.main:app", "--host", "0.0.0.0", "--port", "8001"]
