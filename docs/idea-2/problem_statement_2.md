## Idea 2: Smart Recipe & Meal Planner

### 1. Team Information

* **Team Name:** Team 2
* **Team Members:**
    * \[Member 1 Name] ([GitHub Username 1]) - Tentative Role: [e.g., Frontend Lead]
    * \[Member 2 Name] ([GitHub Username 2]) - Tentative Role: [e.g., Backend Lead]
    * Carlos Mejia (carloslme) - Tentative Role: [e.g., GenAI Lead]
* **Project Repository:** `https://github.com/AET-DevOps25/team-2`

### 2. Problem Statement (max. 5 sentences)

Planning meals and finding recipes based on available ingredients, dietary restrictions, and personal preferences can be a time-consuming and often uninspiring daily task. This application aims to simplify and enhance this process by intelligently suggesting recipes, generating personalized meal plans, and helping create shopping lists. Its main functionality allows users to input their available ingredients, specify dietary needs (e.g., vegan, gluten-free, allergies), and indicate cuisine preferences to receive tailored recipe suggestions and weekly meal plans. The intended users are individuals and families looking for an efficient, inspiring, and personalized assistant for their everyday cooking and meal planning. GenAI will be meaningfully integrated using LangChain and a vector database (populated with diverse recipe data) to implement Retrieval-Augmented Generation (RAG), enabling the assistant to personalize suggestions, generate creative recipe variations (e.g., "make this recipe low-carb" or "suggest a substitute for an ingredient"), and answer cooking-related questions based on the indexed recipe data, with support for both cloud-based and local LLMs.

### 3. High-Level Functional Points

* User can sign up, log in, and manage a detailed profile including dietary restrictions (e.g., vegan, vegetarian, gluten-free), allergies, disliked ingredients, and cuisine preferences.
* User can maintain a list of currently available ingredients in their pantry/fridge.
* System provides an interface for browsing and searching a comprehensive recipe database with filters (e.g., cuisine, preparation time, difficulty, dietary tags).
* GenAI-powered recipe recommendation engine suggests recipes based on the user's profile, available ingredients, and explicitly stated preferences or mood.
* User can generate personalized weekly or daily meal plans (breakfast, lunch, dinner, snacks).
* System can automatically generate a shopping list based on a selected meal plan, taking into account ingredients already marked as available by the user.
* Users can save favorite recipes and meal plans for quick access.
* System provides clear feedback and loading indicators during searches, plan generation, and other operations.
* (Optional) Users can rate recipes and view community ratings/comments.

### 4. Initial System Structure (Text and Diagram)

* **Server:** Spring Boot REST API handling user authentication and profiles, ingredient management, recipe data, meal plan generation logic, shopping list creation, and orchestrating communication with the GenAI service. It will be architected with at least 3 microservices (e.g., User Profile & Pantry Service, Recipe & Meal Plan Service, GenAI Orchestration Service).
* **Client:** A responsive web frontend (e.g., Vue.js, React, or Angular) providing an intuitive and visually appealing user interface for managing profiles, inputting ingredients, searching/viewing recipes, creating meal plans, and managing shopping lists.
* **GenAI Service:** A Python-based microservice utilizing LangChain. It will handle the interaction with a vector database (e.g., Weaviate) storing rich recipe data (ingredients, instructions, nutritional information, cuisine type, user tags). This service will perform RAG for recipe suggestions, modifications, and answering cooking-related Q&A.
* **Database:** A relational database (e.g., PostgreSQL or MySQL, run via Docker) for storing user accounts, profiles, pantry inventories, saved recipes, meal plans, and structured recipe metadata. The vector database will store embeddings and detailed text for recipes.

### 5. Pros & Cons

* **Pros:**
    * Addresses a common daily need, offering high potential for regular user engagement.
    * Offers a clear value proposition by saving time, reducing food waste, and inspiring culinary variety.
    * Provides ample opportunity for a visually rich and interactive user experience.
    * GenAI can significantly enhance personalization and offer creative culinary assistance beyond simple database lookups.
    * Recipe data, while needing curation, is widely available from various sources (public APIs, scraping - with ethical considerations).
* **Cons:**
    * Data acquisition, cleaning, parsing, and structuring from diverse recipe sources into a consistent format for the vector database will be a significant undertaking.
    * Ensuring the quality and accuracy of recipe data (especially nutritional information or allergy compatibility) is crucial and challenging.
    * The user interface for managing preferences, ingredients, and meal plans can become complex if not carefully designed for simplicity.
    * Generating truly "intelligent" and contextually relevant meal plans requires sophisticated logic and effective use of user data beyond basic RAG.
    * Balancing feature richness with ease of use will be key.
