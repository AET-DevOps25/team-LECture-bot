## Idea 1: Intelligent Course Material Assistant

### 1. Team Information

* **Team Name:** Team 2
* **Team Members:**
    * \[Member 1 Name] ([GitHub Username 1]) - Tentative Role: [e.g., Frontend Lead]
    * \[Member 2 Name] ([GitHub Username 2]) - Tentative Role: [e.g., Backend Lead]
    * Carlos Mejia (carloslme) - Tentative Role: [e.g., GenAI Lead]
* **Project Repository:** `https://github.com/AET-DevOps25/team-2`

### 2. Problem Statement (max. 5 sentences)

Students across various university courses often struggle to efficiently locate specific information within a multitude of course materials like lecture slides, research papers, and textbooks. This application aims to solve this by providing a centralized, intelligent query interface for any uploaded course resources. Its main functionality is to allow users to upload diverse course documents (e.g., PDFs, Markdown files, text files) and then ask natural language questions about the content. The intended users are students enrolled in any university or online course seeking a faster way to find information and understand complex topics, ultimately saving significant study time and improving comprehension of their study materials. GenAI will be meaningfully integrated using LangChain and a vector database to implement Retrieval-Augmented Generation (RAG), enabling the assistant to answer user queries based *only* on the provided course-specific documents, with support for both cloud-based (e.g., OpenAI API) and local Large Language Models (LLMs).

### 3. High-Level Functional Points

* User can sign up, log in, and manage their profile (e.g., associate with specific courses).
* User can create "course spaces" or collections to organize materials.
* User can upload various document types (PDF, MD, TXT) into a selected course space.
* System efficiently processes and indexes uploaded documents, making them immediately available for querying.
* System provides clear feedback to the user during document upload, processing, and indexing stages (e.g., progress indicators, success/error messages).
* User can ask natural language questions within the context of a specific course space.
* The GenAI backend retrieves relevant information from the indexed documents (RAG) and generates a concise answer.
* The application displays the answer along with citations or references to the source document(s) and page numbers/sections.
* (Optional) Admin dashboard to view system usage statistics or manage global settings.

### 4. Initial System Structure (Text and Diagram)

* **Server:** Spring Boot REST API handling user authentication, document management (uploads, storage metadata), course space management, and orchestrating communication with the GenAI service. It will be architected with at least 3 microservices (e.g., User & Course Service, Document Processing Service, Query Orchestration Service).
* **Client:** A responsive web frontend (React/Angular/Vue.js) providing the user interface for login, document upload, course space management, and the interactive chat/Q&A interface.
* **GenAI Service:** A Python-based microservice utilizing LangChain. It will handle document chunking, embedding generation, interaction with a vector database (e.g., Weaviate or similar) for storing and querying document embeddings, and communication with LLMs (OpenAI API and a local model like Ollama) to generate answers via RAG.
* **Database:** A relational database (e.g., PostgreSQL or MySQL, run via Docker) for storing user data, course space information, document metadata, and other application-specific data. The vector database will store the document embeddings.


### 5. Pros & Cons

* **Pros:**
    * Addresses a common and significant pain point for students across many disciplines.
    * Provides a clear and valuable application of GenAI (RAG) for information retrieval.
    * The domain is flexible, allowing the team to use their own course materials for testing.
    * The technical requirements align well with the specified project stack.
    * Can lead to a highly useful tool for personal study or broader student use.
    * A key differentiator will be its highly intuitive, student-centric user experience and robust citation features specifically tailored for academic materials, going beyond generic document Q&A.
* **Cons:**
    * Success heavily depends on the quality of document parsing, chunking, and the RAG implementation.
    * Handling diverse document formats and layouts effectively can be challenging.
    * Ensuring accurate and relevant answers from the GenAI requires careful prompt engineering and potentially fine-tuning.
    * Managing storage and processing for a large number of documents/users could present scalability challenges (though likely within course project limits).
    * The "novelty" might be in the specific implementation and focus, as general Q&A on documents is a known GenAI application.
