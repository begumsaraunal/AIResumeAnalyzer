package com.example.ai_resume_analyzer.model

// Data model representing the response returned by the AI analysis API.
// Contains detected skills, missing skills, ATS score and improvement suggestions.
data class AnalyzeResponse(
    val skills: List<String> = emptyList(),
    val required_skills: List<String> = emptyList(),
    val matched_skills: List<String> = emptyList(),
    val missing_skills: List<String> = emptyList(),
    val ats_score: Int = 0,
    val job_match: Float = 0f,
    val suggestions: List<String> = emptyList(),
    val analysis_summary: String = "",
    val recommendation_level: String = "low"
)


