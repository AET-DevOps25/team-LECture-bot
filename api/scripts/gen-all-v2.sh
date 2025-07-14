#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Configuration ---
# --- User & Course Microservice ---
USER_COURSE_API_SPEC="api/user-course-openapi.yaml"
USER_COURSE_SERVER_OUTPUT_DIR="services/backend/user-course-microservice"
# --- GenAI Microservice ---
GENAI_API_SPEC="api/genai-backend-openapi.yaml"
GENAI_SERVER_OUTPUT_DIR="services/backend/genai-backend-microservice"
# --- Client ---
CLIENT_OUTPUT_DIR="services/client/src/shared/api/generated"


echo "--- Generating code for all services ---"

# --- Generate user-course-microservice Server Code (Spring Boot) ---
echo "Generating Spring Boot server code for user-course-microservice..."
TEMP_SERVER_OUTPUT_DIR="${USER_COURSE_SERVER_OUTPUT_DIR}/generated-src-temp"
FINAL_SERVER_JAVA_DIR="${USER_COURSE_SERVER_OUTPUT_DIR}/src/main/java"
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"
mkdir -p "${TEMP_SERVER_OUTPUT_DIR}"

npx @openapitools/openapi-generator-cli generate \
    -i "${USER_COURSE_API_SPEC}" \
    -g spring \
    -o "${TEMP_SERVER_OUTPUT_DIR}" \
    --additional-properties=interfaceOnly=true,useSpringBoot3=true,useTags=true,apiPackage=com.lecturebot.generated.api,modelPackage=com.lecturebot.generated.model,openApiNullable=false

echo "Moving generated Java code to final destination..."
rm -rf "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated"
mkdir -p "${FINAL_SERVER_JAVA_DIR}/com/lecturebot"
mv "${TEMP_SERVER_OUTPUT_DIR}/src/main/java/com/lecturebot/generated" "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/"
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"
echo "Server code for user-course-microservice generated in ${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated"

# --- Generate genai-backend-microservice Server Code (Spring Boot) ---
echo "Generating Spring Boot server code for genai-backend-microservice..."
TEMP_SERVER_OUTPUT_DIR="${GENAI_SERVER_OUTPUT_DIR}/generated-src-temp"
FINAL_SERVER_JAVA_DIR="${GENAI_SERVER_OUTPUT_DIR}/src/main/java"
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"
mkdir -p "${TEMP_SERVER_OUTPUT_DIR}"

npx @openapitools/openapi-generator-cli generate \
    -i "${GENAI_API_SPEC}" \
    -g spring \
    -o "${TEMP_SERVER_OUTPUT_DIR}" \
    --additional-properties=interfaceOnly=true,useSpringBoot3=true,useTags=true,apiPackage=com.lecturebot.generated.api,modelPackage=com.lecturebot.generated.model,openApiNullable=false

echo "Moving generated Java code to final destination..."
rm -rf "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated"
mkdir -p "${FINAL_SERVER_JAVA_DIR}/com/lecturebot"
mv "${TEMP_SERVER_OUTPUT_DIR}/src/main/java/com/lecturebot/generated" "${FINAL_SERVER_JAVA_DIR}/com/lecturebot/"
rm -rf "${TEMP_SERVER_OUTPUT_DIR}"
echo "Server code for genai-backend-microservice generated in ${FINAL_SERVER_JAVA_DIR}/com/lecturebot/generated"


# --- Generate Client Code (TypeScript Fetch) ---
echo "Generating TypeScript client code..."
mkdir -p "${CLIENT_OUTPUT_DIR}"

# Generate client for user-course-microservice
npx openapi-typescript "${USER_COURSE_API_SPEC}" --output "${CLIENT_OUTPUT_DIR}/user-course-api.ts"
echo "TypeScript client for user-course-microservice generated at ${CLIENT_OUTPUT_DIR}/user-course-api.ts"

# Generate client for genai-backend-microservice
npx openapi-typescript "${GENAI_API_SPEC}" --output "${CLIENT_OUTPUT_DIR}/genai-api.ts"
echo "TypeScript client for genai-backend-microservice generated at ${CLIENT_OUTPUT_DIR}/genai-api.ts"

# Generate client for document-microservice
npx openapi-typescript "api/document-api.yaml" --output "${CLIENT_OUTPUT_DIR}/document-api.ts"
echo "TypeScript client for document-microservice generated at ${CLIENT_OUTPUT_DIR}/document-api.ts"


echo "--- Code generation complete. ---"

# --- Generate Document Microservice Code (Spring Boot) ---
DOC_API_SPEC_FILE="api/document-api.yaml"
TEMP_DOC_OUTPUT_DIR="services/backend/document-microservice/generated-src-temp"
FINAL_DOC_JAVA_DIR="services/backend/document-microservice/src/main/java"

echo "--- Generating code for document microservice from ${DOC_API_SPEC_FILE} ---"

rm -rf "${TEMP_DOC_OUTPUT_DIR}"
mkdir -p "${TEMP_DOC_OUTPUT_DIR}"

openapi-generator-cli generate \
    -i "${DOC_API_SPEC_FILE}" \
    -g spring \
    -o "${TEMP_DOC_OUTPUT_DIR}" \
    --additional-properties=interfaceOnly=true,useSpringBoot3=true,useTags=true,apiPackage=com.lecturebot.generated.api,modelPackage=com.lecturebot.generated.model,openApiNullable=false

echo "Moving generated Java code to final destination..."
rm -rf "${FINAL_DOC_JAVA_DIR}/com/lecturebot/generated"
mkdir -p "${FINAL_DOC_JAVA_DIR}/com/lecturebot"
mv "${TEMP_DOC_OUTPUT_DIR}/src/main/java/com/lecturebot/generated" "${FINAL_DOC_JAVA_DIR}/com/lecturebot/"
rm -rf "${TEMP_DOC_OUTPUT_DIR}"

echo "Document microservice code generated in ${FINAL_DOC_JAVA_DIR}/com/lecturebot/generated"