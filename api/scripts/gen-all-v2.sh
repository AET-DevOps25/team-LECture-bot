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


echo "Generating Python GenAi schemas and definitions..."
echo "Ensure you have openapi-python-client installed before continuing..."

GENAI_PYTHON_OUTPUT_DIR="./services/genai/src/genai"
openapi-python-client generate --path ./${GENAI_API_SPEC} \
                  --output-path ./temp_client

cp -r ./temp_client/gen_ai_service_api_client/models ./${GENAI_PYTHON_OUTPUT_DIR}/generated/
cp ./temp_client/gen_ai_service_api_client/errors.py ./${GENAI_PYTHON_OUTPUT_DIR}/generated/
cp ./temp_client/gen_ai_service_api_client/types.py ./${GENAI_PYTHON_OUTPUT_DIR}/generated/


rm -rf ./temp_client 


echo "Python GenAi schemas and definitions generated at ${GENAI_PYTHON_OUTPUT_DIR}/generated"
echo "--- Code generation complete. ---"
