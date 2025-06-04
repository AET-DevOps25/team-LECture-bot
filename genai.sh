#!/bin/bash

# Ensure you are in the LECture-bot project root before running this script.
# For example: cd /path/to/your/LECture-bot

# --- Safety Check & Cleanup (Optional) ---
# If a 'genai' directory exists from a previous attempt, you might want to remove it.
# CAUTION: 'rm -rf genai' will PERMANENTLY DELETE the 'genai' directory and all its contents.
# Back up any important files before uncommenting and running the removal command.
if [ -d "genai" ]; then
  echo "WARNING: An existing 'genai' directory was found."
  read -p "Do you want to remove it and start fresh? (yes/No): " confirm_remove
  if [[ "$confirm_remove" == "yes" ]]; then
    echo "Removing existing 'genai' directory..."
    rm -rf genai
    echo "'genai' directory removed."
  else
    echo "Proceeding with existing 'genai' directory. This might cause issues if not structured for Poetry."
    echo "If 'poetry new . --src' fails, please remove the 'genai' directory manually and rerun this script."
  fi
fi
# --- End Safety Check ---

echo "Creating 'genai' directory (if it doesn't exist) and navigating into it..."
mkdir -p genai # -p ensures it doesn't error if it exists (though the check above handles clean removal)
cd genai

echo "Initializing Poetry project within './genai/' using 'src' layout..."
# This command initializes the current directory (.) as a Poetry project.
# It will create pyproject.toml and the standard src/genai structure.
poetry new . 

echo "Verifying Poetry setup..."
if [ ! -f "pyproject.toml" ] || [ ! -d "src/genai" ]; then
    echo "ERROR: Poetry project initialization failed or did not create the expected structure."
    echo "Please ensure Poetry is installed correctly and the 'genai' directory was empty or properly handled."
    cd .. # Go back to project root
    exit 1
fi
echo "Poetry project initialized."

echo "Creating additional subdirectories for the 'genai' package..."
mkdir -p src/genai/api/routers
mkdir -p src/genai/core
mkdir -p src/genai/pipelines
mkdir -p src/genai/services
mkdir -p src/genai/utils

# 'tests' directory and its __init__.py should be created by `poetry new`
# If not, or for older poetry versions:
mkdir -p tests/integration
mkdir -p tests/unit
touch tests/__init__.py # Ensure tests is a package

echo "Creating __init__.py files for Python packaging..."
touch src/genai/api/__init__.py
touch src/genai/api/routers/__init__.py
touch src/genai/core/__init__.py
touch src/genai/pipelines/__init__.py
touch src/genai/services/__init__.py
touch src/genai/utils/__init__.py
touch tests/integration/__init__.py
touch tests/unit/__init__.py

echo "Directory structure and Poetry setup for 'genai' module completed."
echo "Next, add dependencies using 'poetry add ...' while inside the 'genai' directory."

# Navigate back to the project root
cd ..
echo "Returned to project root: $(pwd)"
