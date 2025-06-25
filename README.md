# RAG Question-Answering System

## Task Description
Build a question-answering system for the museum documentation using RAG (Retrieval-Augmented Generation) architecture.

## Technical Details
### Technologies, libraries and frameworks used
* **Java** - programming language
* **Spring Boot** (Apache Tomcat embedded) - main WEB framework
* **Spring AI** - official framework for building AI-powered applications in Java. Used for chat with OpenAPI
* **LangChain4J** - equivalent of Python LangChain library, building applications powered by LLMs. Used for embeddings and RAG
* **ChromaDB** - vector database
* **Caffeine** - caching system
* **SpringDoc** - Swagger / OpenAPI documentation
* **Maven** - packet manager
* **Lombok** - library to generate boilerplate code
* **Google Gemini** - LLM model, OpenAPI
* Configuration via application.yml

### Data Preparation and Vectorization
#### 1. Document Loading and Processing
   * Source data to process: https://github.com/artsmia/collection. The data is expected to be hosted locally and loaded on a customizable path.
   * Document splitting: configurable number of chunks (default=1000) 
   * Overlapping: configurable number of tokens (default=100)

#### 2. Embedding Creation
   * Embedding model: local integrated model "sentence-transformers/all-MiniLM-L6-v2"
   * Vector database: ChromaDB, collection json_docs
   * Accelerated parallel processing in batches, 200 documents per batch by default

### LLM Integration
#### 1. LLM Setup
   * LLM: Google Gemini via OpenAPI (it's needed to have an api-key)
   * Prompt template: a static resource /resources/prompts/main-template.txt with placeholders

#### 2. RAG Pipeline
   * Vector data retrieval: configurable max results (default=5) and similarity (default=0.75)  

### API and Interface
#### 1. REST API
   * API is hosted on a root path: http://127.0.0.1:8080/
   * Swagger/OpenAPI documentation: http://127.0.0.1:8080/swagger-ui/index.html

#### 2. Simple Interface
   * Simple WEB interface: http://127.0.0.1:8080/index.html (embedded page /resources/static/index.html)

## Launching
### Deploying locally
#### Manually
   * Pre-conditions: installed Java 21 and Maven.
   * Generate an API-key of Gemini AI (or use another OpenAPI LLM).
   * Specify AI OpenAPI connection parameters in the application.yml file: `spring.ai.openai.*`.
   * Download the data source with [json documents](https://github.com/artsmia/collection) and store it locally.
   * Point to the data source path in the application.yml (/objects subdirectory expected): `app.rag.doc-storage-path`. 
   * In case there is no ready ChromaDB server, download and install it locally (e.g. via Docker image chromadb/chroma).
     * **Warning**: looks like LangChain4j is able to only work with /v1/ ChromaDB API and cannot use /v2/ API from the latest versions. The verified version of Chroma DB: 0.4.24.   
   * Run ChromaDB server.
   * Specify ChromaDB connection parameters in the application.yml file: `vectorstore.chroma.*`.
   * Build the application from the root directory: `mvn clean install`.
   * Run the application from the root directory: `mvn spring-boot:run`.
### Docker
   * Pre-conditions: installed docker and docker-compose. docker-compose.yml is used to run ChromaDB and the application together.
   * Generate an API-key of Gemini AI (or use another OpenAPI LLM).
   * Specify AI OpenAPI connection parameters in the docker-compose.yml file: `services.ai-service.environment.OPEN_API_*`.
   * Download the data source with [json documents](https://github.com/artsmia/collection) and store it locally.
   * Point to the data source path as a volume in the docker-compose.yml (/objects subdirectory expected): `services.ai-service.volumes`.
   * Specify the location for storing Chroma data files as a volume in the docker-compose.yml: `services.chroma.volumes`.
     * If there is a need to clear data in ChromaDB  - just remove all ChromaDB data files and restart the application.
   * Run the application from the root directory: `docker-compose up`.

### Create embeddings
   * Call the API http://127.0.0.1:8080/rag/embeddings/batch
     * Can be called using Swagger (http://127.0.0.1:8080/swagger-ui/index.html), Postman or curl
   * This process can take about 30-60 minutes

### Chat with generated embeddings
   * Call the API http://127.0.0.1:8080/chat/prompt the json request model `{"question": "An interesting question"}`
   * Can be used built-in WEB interface http://127.0.0.1:8080/index.html