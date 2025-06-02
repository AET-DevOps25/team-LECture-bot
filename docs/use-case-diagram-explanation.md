# Overview of the Use Case Diagram: Intelligent Course Material Assistant

## Purpose

This use case diagram serves as a starting point for understanding the functional scope of the Intelligent Course Material Assistant. It outlines the key interactions between the primary user (student) and the system, with a focus on document management, question answering, and flashcard generation. As an early-stage conceptual model, this diagram is intended to guide discussions, inform design decisions, and remain open to refinement as the system evolves.

---

## Primary Actor

**Student:** The primary user of the system who interacts with all major functionalities. The student performs actions such as uploading documents, managing course spaces, asking questions, and generating flashcards.

---

## Core Functionalities

### 1. User Account Management
- **Sign up:** Allows new users to register an account.
- **Log in:** Enables returning users to access the system and their course spaces.
- **Manage Profile:** Users can update their personal and account-related settings.

### 2. Course Space Management
- **Manage Course Space:** A high-level action encompassing optional actions like:
  - **Create Course Space:** Establish a new workspace for a course.
  - **Edit Course Space:** Modify the metadata of an existing course space.
  - **Delete Course Space:** Remove a course space and its associated data.

These operations allow students to organize documents and interactions by course.

### 3. Document Handling
- **Upload Document(s):** Students can upload course-related files (e.g., PDFs, lecture notes). A high-level action encompassing the following actions:
  - **Process Document(s):** The system reads and analyzes the documents.
  - **Index Document(s):** Documents are transformed into a searchable format (e.g., via vector embedding).
  - **Progress Indicator:** Real-time visual feedback on upload and processing status.
  - **Error/Success Message:** Notifies users of the outcome of their actions.
- **Manage Documents:** Enables users to rename or delete uploaded documents.

### 4. Course Q&A
- **Ask Question about Course (Q&A):** Students may submit questions based on their uploaded content.
  - **Generate Answer with Citation:** The system generates a response using the course documents as references and provides source citations for transparency. This feature leverages the underlying GenAI Service.

### 5. Flashcard Generation
- **Flashcard Generation:** Based on uploaded documents in a course space, the system creates question-answer flashcards. This feature leverages the underlying GenAI Service. Optional additional actions based on this are:
  - **Adapt Flashcards:** Modify generated flashcards.
  - **Set Generation Options:** Configure parameters such as difficulty, number of cards, or specific topics.
  - **Export Flashcards:** Allows users to download and save flashcards for use in other applications or offline studying.

---

## GenAI Service Integration

The GenAI Service is responsible for executing generative AI tasks within the system. It supports:
- Answer generation with citation from course content.
- Automated flashcard generation from course content.

It can interact with two types of AI models:
- **Cloud-based model:** Suitable for powerful, large-scale processing (e.g., OpenAI).
- **Local model:** Used when data privacy, offline access, or resource control is prioritized.

---