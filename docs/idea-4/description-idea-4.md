## Idea 4: Academic Paper Summarizer & Comparator

### 1. Team Information

* **Team Name:** Team 2
* **Team Members:**
    * \[Member 1 Name] ([GitHub Username 1]) - Tentative Role: [e.g., Frontend Lead]
    * \[Member 2 Name] ([GitHub Username 2]) - Tentative Role: [e.g., Backend Lead]
    * Carlos Mejia (carloslme) - Tentative Role: [e.g., GenAI Lead]
* **Project Repository:** `https://github.com/AET-DevOps25/team-2`

### 2. Problem Statement (max. 5 sentences)

Researchers and students are often inundated with academic papers and face the time-consuming challenge of quickly grasping key findings and discerning critical differences between multiple publications. This application aims to alleviate this burden by providing an AI-powered platform for summarizing individual research papers and performing comparative analysis across a set of documents. Its core functionality allows users to upload academic papers (primarily in PDF format), after which the system generates concise, structured summaries and enables users to ask comparative questions like, "What are the main differences in methodology between paper A and paper B?". The intended users are academics, researchers, and students who need to efficiently process and synthesize information from scholarly literature. GenAI will be meaningfully integrated using LangChain and a vector database (e.g., Weaviate) to index the textual content and structural elements of uploaded papers, enabling Retrieval-Augmented Generation (RAG) to produce summaries and answer comparative queries based *only* on the provided documents, with support for both cloud-based and local LLMs.

### 3. High-Level Functional Points

* User can sign up, log in, and manage their profile.
* User can create projects or collections to group related papers.
* User can upload one or more academic papers (PDFs primarily; potentially .txt or .md for abstracts/notes).
* System provides feedback during upload and processing (parsing, indexing).
* For individual papers, the system generates a structured summary (e.g., background, methods, key findings, conclusion).
* User can select multiple uploaded papers for comparison.
* User can ask natural language comparative questions (e.g., "Compare the datasets used in these papers," "Which paper has a larger sample size?").
* The GenAI backend processes PDFs, extracts text and metadata, indexes content into a vector DB, and uses RAG for summarization and comparative Q&A.
* The application displays summaries and comparison results clearly, highlighting key differences or similarities and citing sources within the uploaded documents.

### 4. Initial System Structure (Text and Diagram)

* **Server:** Spring Boot REST API handling user authentication, project/collection management, document uploads and metadata storage, and orchestrating requests to the GenAI service. It will be architected with at least 3 microservices (e.g., User & Project Service, Document Processing & Storage Service, Analysis Orchestration Service).
* **Client:** A responsive web frontend (e.g., React, Angular, or Vue.js) providing the user interface for login, document upload, managing paper collections, viewing summaries, and initiating/viewing comparative analyses.
* **GenAI Service:** A Python-based microservice utilizing LangChain. It will incorporate advanced PDF parsing libraries (to handle complex layouts, tables, and figures if possible), manage document chunking, embedding generation, and interaction with a vector database (e.g., Weaviate) for storing and querying paper content. This service will perform RAG to generate summaries and comparative insights.
* **Database:** A relational database (e.g., PostgreSQL or MySQL, run via Docker) for storing user data, project/collection information, metadata about uploaded papers (authors, publication year, source, processing status), and potentially structured data extracted from papers. The vector database will store the embeddings and text chunks of the papers.

### 5. Pros & Cons

* **Pros:**
    * Addresses a significant and well-defined need within the academic and research communities.
    * Strong and clear use case for advanced RAG, text analysis, and comparative reasoning using GenAI.
    * Leverages a common document format (PDF) for academic literature.
    * Potential for sophisticated features like identifying research gaps or tracing citation impacts (though advanced).
    * High utility for users needing to quickly get up to speed on new research areas or prepare literature reviews.
* **Cons:**
    * PDF parsing of academic papers with diverse and complex layouts (multi-column, tables, figures, formulas) is a major technical challenge and can be error-prone.
    * Generating accurate, nuanced, and truly insightful summaries and comparisons requires robust NLP and careful RAG implementation; avoiding superficial results is key.
    * Handling potentially large volumes of text and managing the vector database efficiently will be important.
    * Ensuring the system correctly interprets and compares methodologies, results, and conclusions across different papers is highly complex.
    * Potential copyright considerations if users upload papers they don't have distribution rights for (though the system is for personal analysis).
