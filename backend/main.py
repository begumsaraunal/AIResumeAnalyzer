import os
import tempfile

from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Request
from analyzer import (
    extract_skills,
    extract_required_skills,
    find_matched_skills,
    find_missing_skills,
    generate_suggestions_from_missing,
    calculate_ats_score,
    generate_analysis_summary,
    get_recommendation_level
)
from pdfminer.high_level import extract_text
from matcher import job_match
from schemas import AnalysisResponse


app = FastAPI(
    title="AI Resume Analyzer API",
    description=(
        "Analyze resumes against job descriptions with ATS-style scoring, "
        "matched skills, missing skills, and improvement suggestions."
    ),
    version="1.0.0"
)

@app.get("/health")
async def health_check():
    return {"status": "ok"}

@app.post("/analyze/", response_model=AnalysisResponse)
async def analyze_resume(
    request: Request,
    file: UploadFile = File(...),
    job_description: str = Form("")
    ):
    form = await request.form()
    job_description = (
        job_description
        or form.get("job_description")
        or form.get("jobDescription")
        or form.get("description")
        or ""
    ).strip()

    if not file.filename:
        raise HTTPException(status_code=400, detail="A resume file is required.")

    suffix = os.path.splitext(file.filename or "")[1] or ".pdf"
    temp_path = None
    text = ""

    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as temp_file:
            temp_file.write(await file.read())
            temp_path = temp_file.name

        text = extract_text(temp_path)
    except Exception as exc:
        raise HTTPException(
            status_code=400,
            detail=f"Resume file could not be processed: {exc}"
        ) from exc
    finally:
        if temp_path and os.path.exists(temp_path):
            os.remove(temp_path)

    if not text.strip():
        raise HTTPException(
            status_code=400,
            detail="No readable text was found in the uploaded resume."
        )

    cv_skills = extract_skills(text)
    required_skills = extract_required_skills(job_description)
    matched_skills = find_matched_skills(cv_skills, required_skills)
    missing_skills = find_missing_skills(cv_skills, required_skills)
    suggestions = generate_suggestions_from_missing(missing_skills)
    score = calculate_ats_score(cv_skills, required_skills)
    summary = generate_analysis_summary(score, matched_skills, missing_skills)
    recommendation_level = get_recommendation_level(score)
    match_score = None
        
    if job_description:
        match_score = job_match(text, job_description)

    print("CV SKILLS:", cv_skills)
    print("JOB DESCRIPTION:", job_description)
    print("JOB SKILLS:", required_skills)
    print("MATCHED SKILLS:", matched_skills)
    print("MISSING SKILLS:", missing_skills)

    return AnalysisResponse(
        skills=cv_skills,
        required_skills=required_skills,
        matched_skills=matched_skills,
        missing_skills=missing_skills,
        ats_score=score,
        job_match=match_score,
        suggestions=suggestions,
        analysis_summary=summary,
        recommendation_level=recommendation_level
    )
