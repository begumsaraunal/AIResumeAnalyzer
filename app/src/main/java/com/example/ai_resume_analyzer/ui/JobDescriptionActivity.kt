package com.example.ai_resume_analyzer.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.ai_resume_analyzer.R
import com.google.android.material.textfield.TextInputEditText

// First screen of the application.
// Allows the user to paste a job description which will later
// be compared against the uploaded resume for ATS analysis.
class JobDescriptionActivity : AppCompatActivity() {

    lateinit var jobInput: TextInputEditText
    lateinit var continueButton: Button
    lateinit var wordCounter: TextView

    companion object {
        const val EXTRA_JOB_DESCRIPTION = "JOB_DESCRIPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_job_description)

        jobInput = findViewById(R.id.jobInput)
        continueButton = findViewById(R.id.continueButton)
        wordCounter = findViewById(R.id.wordCounter)

        jobInput.addTextChangedListener {
            val jobText = jobInput.text?.toString() ?:""

            val words = jobText.
            trim().
            split("\\s+".toRegex()).
            filter { it.isNotBlank() }

            val count = words.size

            wordCounter.text = "$count / 20 words"

            if (count >=20){
                wordCounter.text = "✔ Ready to analyze"
                wordCounter.setTextColor(getColor(R.color.success))

                continueButton.isEnabled = true
                continueButton.alpha = 1f
            }else {
                wordCounter.setTextColor(getColor(R.color.text_secondary))

                continueButton.isEnabled = false
                continueButton.alpha = 0.5f
            }

        }

        continueButton.setOnClickListener {
            val jobText = jobInput.text?.toString() ?:""


                currentFocus?.let {

                    val imm = getSystemService(INPUT_METHOD_SERVICE)
                            as InputMethodManager

                    imm.hideSoftInputFromWindow(it.windowToken,0)

                }

                val intent = Intent(this, UploadActivity::class.java)

                intent.putExtra(EXTRA_JOB_DESCRIPTION,jobText)

                startActivity(intent)
            }
        }


    }
