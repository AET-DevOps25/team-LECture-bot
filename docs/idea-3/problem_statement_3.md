## Idea 3: Personalized Travel Itinerary Generator

### 1. Team Information

* **Team Name:** Team 2
* **Team Members:**
    * \[Member 1 Name] ([GitHub Username 1]) - Tentative Role: [e.g., Frontend Lead]
    * \[Member 2 Name] ([GitHub Username 2]) - Tentative Role: [e.g., Backend Lead]
    * Carlos Mejia (carloslme) - Tentative Role: [e.g., GenAI Lead]
* **Project Repository:** `https://github.com/AET-DevOps25/team-2`

### 2. Problem Statement (max. 5 sentences)

Planning a truly personalized travel itinerary is often an overwhelming and time-consuming process, requiring individuals to sift through countless blogs, travel guides, and reviews to piece together a coherent plan. This application aims to streamline and enrich travel planning by generating custom daily itineraries tailored to user-specific interests, budget, and travel style. Its core functionality allows users to input their destination, travel dates, budget constraints, key interests (such as history, adventure, cuisine, or relaxation), and preferred travel pace. The intended users are individuals, couples, and families who desire a more efficient and personalized approach to discovering and organizing their travel experiences. GenAI will be meaningfully integrated using LangChain and a vector database (populated by scraping and processing diverse travel guides, articles, and points-of-interest data) to implement Retrieval-Augmented Generation (RAG), enabling the assistant to construct detailed itineraries, provide rich summaries of attractions, and answer travel-related questions based *only* on the indexed information, with support for both cloud-based and local LLMs.

### 3. High-Level Functional Points

* User can sign up, log in, and manage a detailed travel profile including interests (e.g., art, nature, nightlife), preferred budget range (e.g., budget, mid-range, luxury), typical travel pace (e.g., relaxed, moderate, packed), and past destinations or preferences.
* User can input specific trip requirements: destination(s), travel dates, number of travelers.
* System provides an interactive interface for the GenAI to generate a day-by-day itinerary suggestion, including activities, estimated timings, and logistical notes (e.g., "allow 2 hours for museum visit").
* The application displays the itinerary with rich details for each activity/location, such as summaries, images (if available from scraped data), and map links, with information pulled via RAG.
* User can customize the generated itinerary by adding, removing, or reordering activities, or by requesting alternative suggestions for specific time slots.
* Users can save their itineraries, access them offline (basic view), and export them (e.g., to PDF or a calendar format).
* System provides clear visual feedback during itinerary generation and updates.
* (Optional) Users can share their itineraries with fellow travelers or make them public for community inspiration (with privacy controls).

### 4. Initial System Structure (Text and Diagram)

* **Server:** Spring Boot REST API handling user authentication and profiles, trip request management, itinerary data storage and retrieval, and orchestrating communication with the GenAI service. It will be architected with at least 3 microservices (e.g., User Profile & Trip Service, Itinerary Management Service, GenAI Orchestration Service).
* **Client:** A responsive web frontend (e.g., Angular, React, or Vue.js) providing an engaging and interactive user interface for inputting trip details, displaying generated itineraries (potentially with map integration), and allowing for itinerary customization.
* **GenAI Service:** A Python-based microservice utilizing LangChain. It will manage the interaction with a vector database (e.g., Weaviate) storing processed and indexed travel information (text from guides, POI details, user reviews if ethically sourced). This service will perform RAG for itinerary construction, attraction summarization, and travel Q&A.
* **Database:** A relational database (e.g., PostgreSQL or MySQL, run via Docker) for storing user accounts, travel profiles, trip details, saved itineraries, and structured metadata related to points of interest. The vector database will store embeddings and detailed text for travel content.

### 5. Pros & Cons

* **Pros:**
    * Addresses a popular and relatable problem with a clear value proposition for travelers.
    * High potential for a visually appealing and interactive user experience, especially with map integrations.
    * GenAI (RAG) can provide significant value by synthesizing information from diverse sources into a coherent plan.
    * The application domain is engaging and offers many avenues for creative feature development.
* **Cons:**
    * Data acquisition: Reliably scraping, cleaning, and structuring high-quality travel data from diverse sources (guides, blogs, POI databases) is a major challenge and requires ongoing effort.
    * Itinerary coherence: Generating itineraries that are not just lists of activities but are logistically sound, well-paced, and genuinely personalized is complex and requires sophisticated RAG prompting and potentially additional logic.
    * Scope management: The potential feature set is vast (real-time bookings, flight/hotel integration, live updates), so keeping the project within course limits will be crucial. Focus must remain on the core GenAI-driven planning.
    * Keeping information up-to-date (e.g., opening hours, prices, availability of attractions) is difficult without access to live APIs, which may be beyond the scope or budget.
    * Ethical considerations around scraping and using user-generated review content if incorporated.
