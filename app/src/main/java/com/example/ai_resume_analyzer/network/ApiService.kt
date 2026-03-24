package com.example.ai_resume_analyzer.network

import com.example.ai_resume_analyzer.model.AnalyzeResponse
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Retrofit API interface used for communication with the backend.
// Sends the resume file and job description to the FastAPI server
// and receives the AI analysis results.
interface ApiService {

    @Multipart
    @POST("analyze/")
    fun analyzeResume(
        @Part file: MultipartBody.Part,
        @Part("job_description") jobDescription: RequestBody
    ): Call<AnalyzeResponse>

}