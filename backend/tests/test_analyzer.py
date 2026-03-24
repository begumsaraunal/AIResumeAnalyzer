import sys
from pathlib import Path

sys.path.append(str(Path(__file__).resolve().parent.parent))

from analyzer import (
    extract_skills,
    extract_required_skills,
    find_matched_skills,
    find_missing_skills,
    calculate_ats_score,
    generate_analysis_summary,
    get_recommendation_level,
)


def test_extract_skills_detects_resume_skills():
    text = "Experienced in psychology, research, SPSS, statistics and interviewing."
    skills = extract_skills(text)

    assert "psychology" in skills
    assert "research" in skills
    assert "spss" in skills
    assert "statistics" in skills
    assert "interviewing" in skills


def test_extract_required_skills_detects_job_skills():
    job_description = (
        "We are looking for someone with psychology, mentoring, research, "
        "SPSS, statistics and interviewing experience."
    )
    required = extract_required_skills(job_description)

    assert "psychology" in required
    assert "mentoring" in required
    assert "research" in required
    assert "spss" in required
    assert "statistics" in required
    assert "interviewing" in required


def test_matched_and_missing_skills_are_calculated_correctly():
    cv_skills = ["psychology", "research", "spss", "statistics", "interviewing"]
    job_skills = ["psychology", "research", "spss", "statistics", "interviewing", "mentoring"]

    matched = find_matched_skills(cv_skills, job_skills)
    missing = find_missing_skills(cv_skills, job_skills)

    assert matched == ["interviewing", "psychology", "research", "spss", "statistics"]
    assert missing == ["mentoring"]


def test_calculate_ats_score_returns_expected_value():
    cv_skills = ["psychology", "research", "spss", "statistics", "interviewing"]
    job_skills = ["psychology", "research", "spss", "statistics", "interviewing", "mentoring"]

    score = calculate_ats_score(cv_skills, job_skills)

    assert score == 83


def test_generate_analysis_summary_with_missing_skills():
    summary = generate_analysis_summary(
        83,
        ["interviewing", "psychology", "research", "spss", "statistics"],
        ["mentoring"]
    )

    assert summary == "Matched 5 required skills and missed 1. ATS score is 83/100."


def test_recommendation_level_mapping():
    assert get_recommendation_level(95) == "excellent"
    assert get_recommendation_level(75) == "good"
    assert get_recommendation_level(55) == "fair"
    assert get_recommendation_level(20) == "low"
