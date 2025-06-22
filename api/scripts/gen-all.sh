#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Define paths relative to the project root
API_SPEC_FILE="api/openapi.yaml" # Your OpenAPI specification file
TEMP_SERVER_OUTPUT_DIR="services/server/generated-src-temp" # Temporary output directory for the generator
FINAL_SERVER_JAVA_DIR="services/server/src/main/java" # The final destination for generated Java source code
CLIENT_OUTPUT_DIR="services/client/src/shared/api/generated"

echo "--- Generating code for all services from ${API_SPEC_FILE} ---"

# --- Generate Server Code (Spring Boot) ---
echo "Generating Spring Boot server code..."
# Ensure the temporary output directory exists and is clean
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"
mkdir -p "${TEMP_SERVER_OUTPUT_DIR}"

# Using openapi-generator-cli.
openapi-generator-cli generate \
    -i "${API_SPEC_FILE}" \
    -g spring \
    -o "${TEMP_SERVER_OUTPUT_DIR}" \
    --additional-properties=interfaceOnly=true,useSpringBoot3=true,useTags=true,apiPackage=com.lecturebot.generated.api,modelPackage=com.lecturebot.generated.model,openApiNullable=false

# Move the generated Java files to their final location
echo "Moving generated Java code to final destination..."
rm -rf "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated" # Clean previous generated code
mkdir -p "${FINAL_SERVER_JAVA_DIR}/com/lecturebot" # Ensure parent directory exists
mv "${TEMP_SERVER_OUTPUT_DIR}/src/main/java/com/lecturebot/generated" "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/"

# Clean up the temporary directory
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"

echo "Server code generated in ${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated"


# --- Generate Client Code (TypeScript Fetch) ---
echo "Generating TypeScript client code..."
# Ensure the output directory exists
mkdir -p "$(dirname "${CLIENT_OUTPUT_DIR}")"

# Using openapi-typescript.
openapi-typescript "${API_SPEC_FILE}" --output "${CLIENT_OUTPUT_DIR}/api.ts"

echo "TypeScript client generated at ${CLIENT_OUTPUT_DIR}/api.ts"

echo "--- Code generation complete. ---"