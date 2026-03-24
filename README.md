# AI Resume Analyzer

An end-to-end mobile and backend portfolio project that analyzes resumes against job descriptions and provides ATS-style scoring, matched skills, missing skills, and improvement suggestions.

## Overview

AI Resume Analyzer helps users compare a resume against a target job description through an Android mobile app powered by a FastAPI backend. The system extracts resume text from PDF files, detects relevant skills, calculates an ATS-style score, and highlights gaps the user can improve.

## Features

- Upload a resume PDF from the Android app
- Paste a job description and analyze role alignment
- Detect matched and missing skills
- Calculate an ATS-style score
- Show job match similarity and improvement suggestions
- Provide a backend health check endpoint
- Include automated backend tests for core analysis logic and API behavior

## Tech Stack

### Backend
- FastAPI
- pdfminer.six
- sentence-transformers
- scikit-learn
- pytest

### Mobile
- Kotlin
- Android SDK
- Retrofit
- Material Components

## How It Works

1. The user enters a job description in the Android app.
2. The user uploads a resume PDF.
3. The backend extracts text from the uploaded file.
4. Skills are detected from both the resume and the job description.
5. The backend calculates matched skills, missing skills, ATS score, and job match similarity.
6. The mobile app presents the analysis results in a user-friendly interface.

## Project Structure

- `backend/` - FastAPI backend for resume analysis
- `backend/tests/` - automated backend tests
- `app/` - Android mobile application


## Screenshots

| Job Description | Upload |
|---|---|
| ![Job Description Screen](screenshots/job-description.png) | ![Upload Screen](screenshots/upload-screen.png) |

| Loading | Results |
|---|---|
| ![Loading Screen](screenshots/loading-screen.png) | ![Results Screen](screenshots/results-screen.png) |


## Demo

![Demo](screenshots/demo.gif)


## API

### Health Check

**Endpoint**  
`GET /health`

**Example Response**
```json
{
  "status": "ok"
}
```

### Analyze Resume

**Endpoint**  
`POST /analyze/`

**Form Data**
- `file`: resume PDF
- `job_description`: job description text

**Example Response**
```json
{
  "skills": ["interviewing", "psychology", "research", "spss", "statistics"],
  "required_skills": ["interviewing", "mentoring", "psychology", "research", "spss", "statistics"],
  "matched_skills": ["interviewing", "psychology", "research", "spss", "statistics"],
  "missing_skills": ["mentoring"],
  "ats_score": 83,
  "job_match": 0.3,
  "suggestions": [
    "Add experience with mentoring or mention relevant coursework/projects involving mentoring."
  ],
  "analysis_summary": "Matched 5 required skills and missed 1. ATS score is 83/100.",
  "recommendation_level": "good"
}
```

## Setup

### Backend

1. Open the `backend/` folder.
2. Create and activate a virtual environment.
3. Install dependencies.
4. Run the FastAPI server.

**Example**
```bash
pip install -r requirements.txt
uvicorn main:app --reload
```

### Android App

1. Open the Android project in Android Studio.
2. Make sure the backend is running locally.
3. Use `10.0.2.2:8000` as the backend base URL for the Android emulator.
4. Build and run the app.

## Testing

Run backend tests with:

```bash
.\backend\venv\Scripts\python.exe -m pytest
```

Current test coverage includes:
- skill extraction logic
- ATS score calculation
- matched and missing skill detection
- API response validation
- health endpoint validation

## Technical Highlights

- Resume text extraction with `pdfminer.six`
- Rule-based skill detection for ATS-style scoring
- Semantic similarity scoring with sentence embeddings
- FastAPI backend integrated with Android via Retrofit
- Mobile UI designed for clear resume feedback and improvement guidance
- Automated backend tests for core logic and API routes

## Challenges Solved

- Passing job description text correctly across Android activities
- Sending multipart PDF uploads from mobile to backend
- Matching resume skills against job requirements
- Separating ATS score from semantic job match in the UI
- Improving backend reliability with typed responses and tests

## Future Improvements

- Add a more comprehensive skill dictionary
- Improve NLP-based skill extraction
- Support DOCX resumes
- Add richer analytics and explanation cards
- Improve recommendation quality with more context-aware suggestions
- Add cloud deployment and public demo hosting
