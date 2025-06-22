## LECture-bot React + Vite + TypeScript Project

This is a Vite-based frontend project using TypeScript and React for the LECture-bot application. You can run it either locally or using Docker.

### API-Driven Design Workflow

This project follows an API-first approach. The API client used for communicating with the backend is auto-generated from a central OpenAPI specification.

- **API Contract**: The single source of truth for the API is `/api/openapi.yaml` in the project root.
- **Generated Client**: The TypeScript types and interfaces for the API are generated into `src/shared/api/generated/api.ts`.

**Important**: If you pull changes that modify the `openapi.yaml` file, or if you modify it yourself, you must regenerate the client code by running the following command from the project root directory:

```bash
./api/scripts/gen-all.sh
```

This ensures the frontend's API client is always in sync with the backend contract.

### Prerequisites

- Node.js and npm (or yarn) installed. (Check `client/package.json` for specific version compatibilities if any are noted).
- The backend server (LECture-bot server) should be running and accessible, typically at `http://localhost:8080`, for API features to function correctly.

### 🚀 Running Locally

1. **Navigate to Client Directory**

    If you are not already there:

    ```bash
    cd services/client
    ```

2. **Install Dependencies**

    ```bash
    npm install
    ```

    (or `yarn install` if you prefer yarn)

3. **Start the Development Server**

    ```bash
    npm run dev
    ```

    (or `yarn dev`)

    The app will typically be available at `http://localhost:5173` (as configured in `client/vite.config.ts`).

### 🧪 Testing UI Functionality (e.g., Sign Up)

Ensure both the backend server (e.g., on `http://localhost:8080`) and the frontend development server (e.g., on `http://localhost:5173`) are running. Open your browser and navigate to the Sign Up page. The route for this page is `/signup`.

**URL**: `http://localhost:5173/signup`

Fill in the registration form fields on the `SignUpPage`:

- Name
- Email
- Password (min. 8 characters)
- Confirm Password (must match password)

Upon submitting a valid form:

- A `POST` request is sent to `http://localhost:8080/api/v1/auth/register`.
- **Successful Registration**: Expect a success message and redirection to the login page (e.g., `/login`).
- **Failed Registration**: Backend error messages (e.g., "Email already exists") should be displayed on the form.

**CORS Note**: If CORS errors occur, verify the backend's `SecurityConfig.java` (located at `services/server/src/main/java/com/lecturebot/config/SecurityConfig.java`) allows requests from `http://localhost:5173`.

### 🐳 Running with Docker

1. **Build the Docker Image**

    Ensure you are in the client directory first.

    ```bash
    docker build -t client-app .
    ```

2. **Run the Docker Container**

    ```bash
    docker run --name client -p 3000:3000 client-app
    ```

    The app will be available at `http://localhost:3000` when run via Docker with these commands.

### 🛠️ Building for Production

To create an optimized static build of the frontend:

```bash
npm run build
```

(or `yarn build`)

This typically outputs to a `dist` directory.

### ✨ Linting

To lint the codebase:

```bash
npm run lint
```

(or `yarn lint`)
