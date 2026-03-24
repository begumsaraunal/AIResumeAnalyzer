package com.example.ai_resume_analyzer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_resume_analyzer.R
import com.google.android.material.card.MaterialCardView

// Screen responsible for selecting and uploading the user's resume file.
// Opens a file picker to select a PDF resume and passes the file URI
// to the LoadingActivity for AI processing.
class UploadActivity: AppCompatActivity() {

    lateinit var uploadButton: Button
    lateinit var browseButton: Button

    private val pdfPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                uploadPDF(uri)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upload)

        uploadButton = findViewById(R.id.uploadButton)
        browseButton = findViewById(R.id.browseButton)
        val uploadCard = findViewById<MaterialCardView>(R.id.uploadCard)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        val openPicker = {
            pdfPicker.launch("application/pdf")
        }
        uploadButton.setOnClickListener {
            openPicker()
        }

        browseButton.setOnClickListener {
                openPicker()
            }

        uploadCard.setOnClickListener {
            openPicker()
        }


    }

    fun uploadPDF(uri: Uri) {

        val jobDescription =
            intent.getStringExtra(JobDescriptionActivity.EXTRA_JOB_DESCRIPTION)


        val intent = Intent(this, LoadingActivity::class.java)

        intent.putExtra("pdfUri", uri.toString())
        intent.putExtra("jobDescription", jobDescription)


        startActivity(intent)

    }
}