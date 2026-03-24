from pathlib import Path
import sys

from fastapi.testclient import TestClient

sys.path.append(str(Path(__file__).resolve().parent.parent))

from main import app

client = TestClient(app)
BASE_DIR = Path(__file__).resolve().parent.parent
RESUME_PATH = BASE_DIR / "resume.pdf"


def test_health_endpoint_returns_ok():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_analyze_endpoint_returns_expected_fields():
    job_description = (
        "We are looking for someone with psychology, mentoring, research, "
        "SPSS, statistics and interviewing experience."
    )

    with open(RESUME_PATH, "rb") as pdf_file:
        response = client.post(
            "/analyze/",
            files={"file": ("resume.pdf", pdf_file, "application/pdf")},
            data={"job_description": job_description},
        )

    assert response.status_code == 200

    data = response.json()

    assert "skills" in data
    assert "required_skills" in data
    assert "matched_skills" in data
    assert "missing_skills" in data
    assert "ats_score" in data
    assert "job_match" in data
    assert "suggestions" in data
    assert "analysis_summary" in data
    assert "recommendation_level" in data


def test_analyze_endpoint_detects_missing_skill():
    job_description = (
        "We are looking for someone with psychology, mentoring, research, "
        "SPSS, statistics and interviewing experience."
    )

    with open(RESUME_PATH, "rb") as pdf_file:
        response = client.post(
            "/analyze/",
            files={"file": ("resume.pdf", pdf_file, "application/pdf")},
            data={"job_description": job_description},
        )

    assert response.status_code == 200

    data = response.json()

    assert "mentoring" in data["missing_skills"]
    assert data["ats_score"] > 0
