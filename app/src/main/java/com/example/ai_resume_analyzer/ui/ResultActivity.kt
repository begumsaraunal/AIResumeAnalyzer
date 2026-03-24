package com.example.ai_resume_analyzer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_resume_analyzer.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.ProgressBar
import android.widget.TextView
// Final screen displaying the AI analysis results.
// Shows ATS score, job match percentage, detected skills,
// missing skills and suggestions to improve the resume.
class ResultActivity : AppCompatActivity() {

    lateinit var resultText: TextView
    lateinit var scoreText: TextView
    lateinit var scoreProgress: ProgressBar
    lateinit var scoreGauge: CircularProgressIndicator
    lateinit var missingContainer: ChipGroup
    lateinit var atsScoreText: TextView
    lateinit var matchedChipGroup: ChipGroup
    lateinit var summaryText: TextView
    lateinit var feedbackTitleText: TextView

    lateinit var analyzeAgainButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)

        initViews()
        showResults()
        setupButtons()
    }

    fun setupButtons(){

        analyzeAgainButton.setOnClickListener {

            val intent = Intent(this, JobDescriptionActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish()
        }

    }

    // Initializes all UI components from the layout
    fun initViews(){

        resultText = findViewById(R.id.resultText)
        scoreText = findViewById(R.id.scoreText)
        scoreProgress = findViewById(R.id.scoreProgress)
        scoreGauge = findViewById(R.id.scoreGauge)
        missingContainer = findViewById(R.id.missingSkillsContainer)
        analyzeAgainButton = findViewById(R.id.analyzeAgainButton)
        atsScoreText = findViewById(R.id.atsScoreText)
        matchedChipGroup = findViewById(R.id.matchedSkillsChipGroup)
        summaryText = findViewById(R.id.summaryText)
        feedbackTitleText = findViewById(R.id.feedbackTitleText)



        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

    }

    // Retrieves analysis results from intent extras
    // and updates the UI components accordingly
    @SuppressLint("SetTextI18n")
    fun showResults(){

        val score = intent.getIntExtra("ats_score", 0)
        val jobMatch = intent.getFloatExtra("job_match", 0f)
        val matchedSkills = intent.getStringArrayListExtra("matched_skills") ?: arrayListOf()
        val missing = intent.getStringArrayListExtra("missing") ?: arrayListOf()
        val suggestions = intent.getStringArrayListExtra("suggestions") ?: arrayListOf()
        val summary = intent.getStringExtra("analysis_summary").orEmpty()

        summaryText.text = summary.ifBlank {
            "This analysis highlights how well your resume matches the selected job requirements."
        }

        atsScoreText.text = "$score / 100"

        scoreProgress.animate()
        scoreProgress.progress = score
        scoreGauge.setProgressCompat(score,true)

        scoreText.text = "${(jobMatch * 100).toInt()}%"

       showSkills(matchedChipGroup, matchedSkills, R.color.detected_skill_bg, R.color.detected_skill_text)
        showSkills(missingContainer, missing, R.color.missing_skill_bg, R.color.missing_skill_text)


        resultText.text = when {
            suggestions.isNotEmpty() -> suggestions.joinToString("\n• ", prefix = "• ")
            missing.isEmpty() -> "Your resume already covers the detected required skills."
            else -> "No suggestions are available yet, but improving the missing skills can increase your score."
        }



        val feedbackText = findViewById<TextView>(R.id.feedbackText)

        feedbackText.text = when {
            score >= 90 -> " \uD83D\uDC4D Excellent alignment for this role."
            score >= 70 -> "\uD83D\uDCAA Strong match with a few gaps to improve."
            score >= 50 -> "\uD83D\uDC4C Partial match. Your resume could be tailored further."
            else -> "\uD83D\uDC4E Low alignment. Consider revising your resume for this role."
        }

        feedbackTitleText.text = when {
            score >= 90 -> "Excellent Match"
            score >= 70 -> "Good Match"
            score >= 50 -> "Fair Match"
            else -> "Low Match"
        }


    }

    // Dynamically creates chips to display detected skills from the analysis
    fun showSkills(
        chipGroup: ChipGroup,
        skills: List<String>,
        backgroundColorRes: Int,
        textColorRes: Int
    ) {
        chipGroup.removeAllViews()

        if (skills.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No items to display."
            emptyText.setTextColor(getColor(R.color.text_secondary))
            emptyText.textSize = 13f
            chipGroup.addView(emptyText)
            return
        }

        skills.forEach { skill ->
            val chip = Chip(this)
            chip.text = skill
            chip.isClickable = false
            chip.textSize = 13f
            chip.setChipBackgroundColorResource(backgroundColorRes)
            chip.setTextColor(getColor(textColorRes))
            chipGroup.addView(chip)
        }
    }

}



