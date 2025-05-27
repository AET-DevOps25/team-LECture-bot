#  React + Vite + TypeScript Project

This is a Vite-based frontend project using TypeScript and React. You can run it either locally or using Docker.

##  🚀 Running Locally
### 1. Navigate to the Client Directory
```bash
cd client
```

### 2. Install Dependencies
```bash
npm install   
```
### 3. Start the Development Server
```bash
npm run dev 
```

The app will be available at http://localhost:3000

## 🐳 Running with Docker

### 1. Navigate to the Client Directory
```bash
cd client
```

### 2. Build the Docker Image
```bash
docker build -t client-app .
```
### 3. Run the Docker Container
```bash
docker run --name client -p 3000:3000 client-app
```

The app will be available at http://localhost:3000
