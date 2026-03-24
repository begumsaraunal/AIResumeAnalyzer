import re

skills_list = [
    "python", "java", "kotlin", "c++", "c#",
    "sql", "postgresql", "mysql",
    "machine learning", "deep learning",
    "tensorflow", "pytorch", "scikit-learn",
    "android", "firebase", "flutter",
    "react", "angular", "node.js",
    "docker", "kubernetes", "aws",
    "rest api", "git", "linux",
    "pandas", "numpy", "matplotlib",
    "access", "powerpoint",
    "psychology", "mental health",
    "research", "statistics",
    "spss", "data analysis",
    "interviewing", "mentoring",
    "community work"
]

def _normalize_text(text):
    return re.sub(r"\s+", " ", (text or "").lower()).strip()

def _build_skill_pattern(skill):
    escaped_skill = re.escape(skill)
    return rf"(?<!\w){escaped_skill}(?!\w)"

def _extract_matching_skills(text):
    normalized_text = _normalize_text(text)
    detected = []

    for skill in skills_list:
        pattern = _build_skill_pattern(skill)
        if re.search(pattern, normalized_text, flags=re.IGNORECASE):
            detected.append(skill)

    return sorted(set(detected))

def extract_skills(text):
    return _extract_matching_skills(text)

def extract_required_skills(job_description):
    return _extract_matching_skills(job_description)

def find_missing_skills(cv_skills, job_skills):
    missing = []

    for skill in job_skills:
        if skill not in cv_skills:
            missing.append(skill)
    missing = sorted(set(missing))        

    return missing


def find_matched_skills(cv_skills, job_skills):
    matched = []

    for skill in job_skills:
        if skill in cv_skills:
            matched.append(skill)

    return sorted(set(matched))

def generate_suggestions_from_missing(missing_skills):

    suggestions = []

    for skill in missing_skills:

        suggestions.append(
            f"Add experience with {skill} or mention relevant coursework/projects involving {skill}."
        )

    return suggestions

def calculate_ats_score(cv_skills, required_skills):

    if len(required_skills) == 0:
        return 0

    matched = 0

    for skill in required_skills:
        if skill in cv_skills:
            matched += 1

    score = (matched / len(required_skills)) * 100

    return round(score)


def get_recommendation_level(score):
    if score >= 90:
        return "excellent"
    if score >= 70:
        return "good"
    if score >= 50:
        return "fair"
    return "low"


def generate_analysis_summary(score, matched_skills, missing_skills):
    if not matched_skills and not missing_skills:
        return "No job-specific skills were detected from the description."

    if missing_skills:
        return (
            f"Matched {len(matched_skills)} required skills and missed "
            f"{len(missing_skills)}."
        )

    return (
        f"All detected required skills are covered by the resume. "
    )
