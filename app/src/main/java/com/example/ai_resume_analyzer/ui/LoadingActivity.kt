package com.example.ai_resume_analyzer.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_resume_analyzer.model.AnalyzeResponse
import com.example.ai_resume_analyzer.R
import com.example.ai_resume_analyzer.network.RetrofitClient
import com.example.ai_resume_analyzer.utils.FileUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// Displays the AI processing screen while the resume is being analyzed.
// Handles file upload, API request and progress animation
// before navigating to the result screen.
class LoadingActivity : AppCompatActivity(){

    lateinit var progressBar: ProgressBar
    lateinit var percentText: TextView
    lateinit var taskUpload: TextView
    lateinit var taskExtract: TextView
    lateinit var taskScore: TextView
    lateinit var taskUploadIcon: TextView
    lateinit var taskExtractIcon: TextView
    lateinit var taskScoreIcon: TextView
    lateinit var taskUploadStatus: TextView
    lateinit var taskExtractStatus: TextView
    lateinit var taskScoreStatus: TextView
    lateinit var circularProgress: CircularProgressIndicator

    val handler = Handler(Looper.getMainLooper())

    var loadingProgressValue = 0



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        initViews()

        startLoadingAnimation()
        startIconRotation()

        val uriString = intent.getStringExtra("pdfUri")
        if(uriString == null){
            finish()
            return
        }

        val uri = Uri.parse(uriString)
        val jobText = intent.getStringExtra("jobDescription") ?: ""

        analyzeResume(uri,jobText)
    }

    private fun startLoadingAnimation() {

        handler.postDelayed(object : Runnable {

            override fun run() {

                loadingProgressValue += 5

                progressBar.progress = loadingProgressValue
                circularProgress.setProgress(loadingProgressValue,true)

                percentText.text = "$loadingProgressValue%"

                when(loadingProgressValue) {

                    20 -> {
                        taskUploadIcon.text = "✔"
                        taskUploadStatus.text = "Completed"
                        taskUpload.text = "Resume uploaded"
                        taskUpload.setTextColor((getColor(R.color.success)))
                        taskUploadIcon.setTextColor(getColor(R.color.success))
                        taskUploadStatus.setTextColor(getColor(R.color.success))

                    }

                    50 -> {
                        taskExtractIcon.text = "✔"
                        taskExtractStatus.text = "Completed"
                        taskExtract.text = "Extracting skills"
                        taskExtract.setTextColor(getColor(R.color.success))
                        taskExtractIcon.setTextColor(getColor(R.color.success))
                        taskExtractStatus.setTextColor(getColor(R.color.success))
                    }

                    80 -> {
                        taskScoreIcon.text = "✔"
                        taskScoreStatus.text = "Completed"
                        taskScore.text = "Calculating ATS score"
                        taskScore.setTextColor(getColor(R.color.success))
                        taskScoreIcon.setTextColor(getColor(R.color.success))
                        taskScoreStatus.setTextColor(getColor(R.color.success))
                    }
                }

                    if(loadingProgressValue < 95){

                    handler.postDelayed(this,200)

                }
            }

        },200)
    }

    fun initViews(){

        progressBar = findViewById(R.id.loadingProgress)
        percentText = findViewById(R.id.loadingPercent)
        taskUpload = findViewById(R.id.taskUpload)
        taskExtract = findViewById(R.id.taskExtract)
        taskScore = findViewById(R.id.taskScore)
        taskUploadIcon = findViewById(R.id.taskUploadIcon)
        taskExtractIcon = findViewById(R.id.taskExtractIcon)
        taskScoreIcon = findViewById(R.id.taskScoreIcon)
        taskUploadStatus = findViewById(R.id.taskUploadStatus)
        taskExtractStatus = findViewById(R.id.taskExtractStatus)
        taskScoreStatus = findViewById(R.id.taskScoreStatus)
        circularProgress = findViewById(R.id.loadingSpinner)

    }

    // Sends resume file and job description to backend for AI analysis
    fun analyzeResume(uri: Uri, jobText: String) {
        val jobDescription =
            jobText.toRequestBody("text/plain".toMediaTypeOrNull())

        Log.d("UPLOAD", "Upload started")
        val fileBytes = FileUtils.readPdf(this, uri)

        val requestFile = fileBytes?.toRequestBody(
            "application/pdf".toMediaTypeOrNull()
        )

        val body = requestFile?.let {
            MultipartBody.Part.createFormData(
                "file",
                "resume.pdf",
                it
            )
        }

        if (body!=null) {
            RetrofitClient.instance.analyzeResume(body,jobDescription)
                .enqueue(object : Callback<AnalyzeResponse> {
                    override fun onResponse(
                        call: Call<AnalyzeResponse>,
                        response: Response<AnalyzeResponse>
                    ) {
                        Log.d("API", "Response received: ${response.code()}")

                        val result = response.body()

                        if (!response.isSuccessful || result == null) {
                            Toast.makeText(
                                this@LoadingActivity,
                                "Analysis failed. Please check your file or job description and try again.",
                                Toast.LENGTH_LONG
                            ).show()

                            handler.removeCallbacksAndMessages(null)
                            finish()
                            return
                        }


                        val intent = Intent(this@LoadingActivity, ResultActivity::class.java)

                        intent.putStringArrayListExtra("skills", ArrayList(result.skills))
                        intent.putStringArrayListExtra("required_skills", ArrayList(result.required_skills))
                        intent.putStringArrayListExtra("matched_skills", ArrayList(result.matched_skills))
                        intent.putStringArrayListExtra("missing", ArrayList(result.missing_skills))
                        intent.putExtra("ats_score", result.ats_score)
                        intent.putExtra("job_match", result.job_match)
                        intent.putStringArrayListExtra("suggestions", ArrayList(result.suggestions))
                        intent.putExtra("analysis_summary", result.analysis_summary)
                        intent.putExtra("recommendation_level", result.recommendation_level)


                        progressBar.progress = 100
                        percentText.text = "100%"
                        taskScoreStatus.text = "Completed"

                        Handler(Looper.getMainLooper()).postDelayed({

                            startActivity(intent)
                            finish()

                        },500)
                    }


                    override fun onFailure(
                        call: Call<AnalyzeResponse>,
                        t: Throwable
                    ) {
                        Log.e("API", "Error: ${t.message}")
                        Toast.makeText(
                            this@LoadingActivity,
                            "Connection error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        handler.removeCallbacksAndMessages(null)
                        finish()
                    }
                })
        }
    }

    private fun startIconPulse() {
        val loadingIcon = findViewById<ImageView>(R.id.loadingIcon)

        val scaleX = ObjectAnimator.ofFloat(loadingIcon, "scaleX", 1f, 1.08f, 1f)
        val scaleY = ObjectAnimator.ofFloat(loadingIcon, "scaleY", 1f, 1.08f, 1f)
        val alpha = ObjectAnimator.ofFloat(loadingIcon, "alpha", 1f, 0.85f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 1400
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    startIconPulse()
                }
            })
            start()
        }
    }

    private fun startIconRotation() {
        val loadingIcon = findViewById<ImageView>(R.id.loadingIcon)

        ObjectAnimator.ofFloat(loadingIcon, "rotation", 0f, 360f).apply {
            duration = 1800
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }




}