from pydantic import BaseModel, Field


class AnalysisResponse(BaseModel):
    skills: list[str] = Field(default_factory=list)
    required_skills: list[str] = Field(default_factory=list)
    matched_skills: list[str] = Field(default_factory=list)
    missing_skills: list[str] = Field(default_factory=list)
    ats_score: int = 0
    job_match: float | None = None
    suggestions: list[str] = Field(default_factory=list)
    analysis_summary: str = ""
    recommendation_level: str = "low"
