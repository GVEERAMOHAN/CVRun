# CVRun - AI-Powered Resume Tailoring App (MVP)

CVRun is a privacy-first web application that helps users upload their resume, paste a target job description, review AI-assisted relevance suggestions, edit/reorder items, and generate a tailored ATS-friendly resume.

---

## Technical Stack
- **Backend**: Java 21, Spring Boot 3.3.4, Maven, Apache PDFBox (PDF parsing), Apache POI (DOCX parsing).
- **Frontend**: React 19, Vite, TypeScript, Axios, Vanilla CSS (harmonious indigo dark/light system).
- **Caching**: Ephemeral in-memory `ConcurrentHashMap` with configurable 30-minute slide-eviction.
- **AI Integration**: Provider-agnostic abstraction supporting fallback stub rule-based scoring and Google Gemini API REST client.

---

## Project Structure
```
workspace/
├── backend/
│   ├── src/main/java/com/example/airesume/
│   │   ├── ai/          # Provider interfaces & active adapters (Stub, Gemini)
│   │   ├── cache/       # In-memory storage & eviction scheduling
│   │   ├── config/      # CORS and bean definitions
│   │   ├── controller/  # REST endpoints (WorkflowController)
│   │   ├── dto/         # Request and Response schemas
│   │   ├── exception/   # Global Exception handler Mapping
│   │   ├── model/       # Workflow domain structures (ResumeData, Suggestions)
│   │   └── service/     # Parsers, merging, ranking, and generation services
│   └── pom.xml          # Maven dependencies (Spring Web, PDFBox, POI)
└── frontend/
    ├── src/
    │   ├── api/         # Rest endpoint client wrappers (apiClient)
    │   ├── components/  # Common widgets
    │   ├── features/    # Context API states (WorkflowContext)
    │   ├── pages/       # Wizard panels (Upload, Profile, JD, Suggestions, Preview, Export)
    │   ├── App.tsx      # Main sidebar layout coordinator
    │   └── index.css    # Premium CSS design styling variables
    └── package.json     # Vite package scripts
```

---

## Configuration & Run Instructions

### Prerequisites
- **Java Development Kit (JDK 21 or higher)**
- **Apache Maven 3.9+**
- **Node.js (v18 or higher) & npm**

### Run the Backend (Port `8080`)
1. Open a terminal and navigate to the backend folder:
   ```bash
   cd backend
   ```
2. (Optional) Set up your Gemini API Key in your environment:
   - On Windows (PowerShell):
     ```powershell
     $env:GEMINI_API_KEY="your-gemini-key-here"
     ```
   - On Linux/macOS:
     ```bash
     export GEMINI_API_KEY="your-gemini-key-here"
     ```
   *Note: If no API key is provided, the application automatically runs in **Mock/Stub fallback mode**, which uses smart local keyword tokenization and rule-based scoring.*
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Run the Frontend (Port `5173`)
1. Open a separate terminal and navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the developer environment:
   ```bash
   npm run dev
   ```
4. Access the web app in your browser at: `http://localhost:5173`

---

## API Documentation & Sample Requests

All endpoints are mapped under the `/api/workflow` prefix.

### 1. Upload Resume
- **Endpoint**: `POST /api/workflow/upload-resume`
- **Content-Type**: `multipart/form-data`
- **Request**: Multipart file (`file`)
- **Response Shape**:
  ```json
  {
    "workflowId": "wf_b23d5a7e",
    "status": "RESUME_UPLOADED",
    "resumeData": {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+91 9876543210",
      "skills": ["Java", "Spring Boot", "Git"],
      "projects": [
        {
          "id": "proj_1",
          "title": "E-Commerce Platform",
          "description": "Microservices platform...",
          "bullets": ["Implemented shopping cart APIs."],
          "techStack": ["Java", "Spring Boot"],
          "source": "resume"
        }
      ]
    }
  }
  ```

### 2. Paste Job Description
- **Endpoint**: `POST /api/workflow/analyze-jd`
- **Request Body**:
  ```json
  {
    "workflowId": "wf_b23d5a7e",
    "jdText": "Backend developer role requiring Java, Spring Boot and microservices experience. Help build scalable REST APIs."
  }
  ```
- **Response**: The updated workflow object including ranked suggestions and scores.

### 3. Save Approved Content
- **Endpoint**: `POST /api/workflow/approve-content`
- **Request Body**:
  ```json
  {
    "workflowId": "wf_b23d5a7e",
    "approvedContent": {
      "skills": ["Java", "Spring Boot"],
      "projects": [
        {
          "id": "proj_1",
          "title": "E-Commerce Platform",
          "bullets": ["Implemented shopping cart APIs using Spring Boot."],
          "techStack": ["Java", "Spring Boot"]
        }
      ],
      "experience": [],
      "education": []
    }
  }
  ```

### 4. Download Plain Text
- **Endpoint**: `GET /api/workflow/export-text?workflowId=wf_b23d5a7e`
- **Response**: Downloads a tailored plain text file (`tailored_resume.txt`) structured as a clean single-column template.
