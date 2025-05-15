# LECture-bot: System Architecture Overview

This document describes the top-level architecture of the LECture-bot system, an Intelligent Course Material Assistant. The architecture is designed as a distributed system following a client-server model with a microservice-oriented backend.

## 1. Core Components

The system is composed of several key components that interact to deliver its functionality:

### 1.1. Student User

The primary actor interacting with the system. Users access the application to upload course materials, ask questions, and generate study aids like flashcards.

### 1.2. Client Frontend

* **Technology:** React.
* **Responsibility:** Provides the user interface (UI) for all user interactions. This includes user registration/login, creating course spaces, uploading documents, submitting questions for the Q&A feature, initiating flashcard generation, and viewing results.
* **Interaction:** Communicates with the Server Backend via REST API calls over HTTP.

### 1.3. Server Backend

* **Technology:** Spring Boot (Java).
* **Responsibility:** Acts as the central hub for business logic, data management, and orchestration of services. It is designed with a microservice architecture. The image highlights the following internal services:
  * **User & Course Service:** Manages user authentication, profiles, and the creation/management of course spaces.
  * **Document Processing Service:** Handles the reception of uploaded documents, initial parsing/text extraction, and triggers the indexing process by interacting with the GenAI Service.
  * **Query Orchestration Service:** Receives user queries (for Q&A or flashcard generation) from the client, forwards them to the GenAI Service, and processes the results before sending them back to the client.
  * **API Gateway / Load Balancer:** While shown as implicit, this layer would typically manage incoming requests, route them to the appropriate microservice, and handle concerns like load balancing and potentially SSL termination.
* **Interaction:**
  * Receives requests from the Client Frontend.
  * Interacts with the Relational Database for persistent storage of user data, course information, document metadata, and generated flashcards.
  * Communicates with the GenAI Service for tasks requiring AI processing (document indexing, Q&A, flashcard generation).

### 1.4. GenAI Service

* **Technology:** Python with LangChain.
* **Responsibility:** Encapsulates all Artificial Intelligence and Natural Language Processing functionalities. This includes:
  * Receiving document content from the Document Processing Service for indexing.
  * Performing text chunking and generating vector embeddings.
  * Storing and retrieving embeddings from the Vector Database.
  * Handling RAG (Retrieval-Augmented Generation) requests:
    * Embedding user queries.
    * Querying the Vector DB for relevant document chunks.
    * Constructing prompts with retrieved context.
    * Interacting with the LLM API to generate answers or flashcards.
  * Formatting responses and extracting citations.
* **Interaction:**
  * Receives tasks from the Server Backend (Document Processing Service and Query Orchestration Service).
  * Interacts with the Vector Database to store and query document embeddings.
  * Makes API calls to the external LLM API.

### 1.5. Databases

* **Relational Database (PostgreSQL):**
  * **Responsibility:** Stores structured application data such as user accounts, course space details, document metadata (filename, type, references), saved flashcard decks, and other relational information.
  * **Interaction:** Accessed by the Server Backend microservices.
* **Vector Database (Weaviate):**
  * **Responsibility:** Stores vector embeddings of the processed document chunks, enabling efficient similarity searches for the RAG process.
  * **Interaction:** Accessed by the GenAI Service for storing new embeddings and retrieving relevant chunks based on query embeddings.

### 1.6. LLM API

* **Technology:** Cloud-based (e.g., OpenAI API) or a locally hosted Large Language Model.
* **Responsibility:** Provides the core generative capabilities. It takes prompts (constructed by the GenAI Service with context from user documents) and generates natural language responses, summaries, or Q&A pairs for flashcards.
* **Interaction:** Accessed via API calls from the GenAI Service.

## 2. Key Interaction Flows

1. **User Interaction:** The Student User interacts with the Client Frontend via HTTP requests.
2. **Client-Server Communication:** The Client Frontend makes REST API calls (HTTP) to the Server Backend (implicitly via an API Gateway/Load Balancer).
3. **Server-Database Communication:** The Server Backend microservices perform DB Query/Store operations with the Relational Database (PostgreSQL).
4. **Server-GenAI Communication:**
    * The Document Processing Service (within Server Backend) sends document content/tasks to the GenAI Service.
    * The Query Orchestration Service (within Server Backend) sends RAG requests (user queries, context for flashcards) to the GenAI Service.
5. **GenAI-VectorDB Communication:** The GenAI Service performs Embed/Query operations with the Vector Database (Weaviate).
6. **GenAI-LLM Communication:** The GenAI Service makes LLM API Calls to the external LLM API.

This architecture provides a modular and scalable system for delivering the intelligent course material assistance features of LECture-bot.
