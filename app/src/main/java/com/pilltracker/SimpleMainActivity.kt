package com.pilltracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pilltracker.app.R
import java.text.SimpleDateFormat
import java.util.*

class SimpleMainActivity : AppCompatActivity() {

    private lateinit var fabAddMedication: FloatingActionButton
    private lateinit var btnTake: Button
    private lateinit var btnEdit: ImageButton
    private lateinit var btnDelete: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        updateCurrentTime()
    }

    private fun initializeViews() {
        fabAddMedication = findViewById(R.id.fabAddMedication)
        btnTake = findViewById(R.id.btnTake)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setupClickListeners() {
        fabAddMedication.setOnClickListener {
            showToast("Add Medication clicked")
            // TODO: Implement add medication functionality
        }

        btnTake.setOnClickListener {
            showToast("Take medication clicked")
            // TODO: Implement take medication functionality
        }

        btnEdit.setOnClickListener {
            showToast("Edit medication clicked")
            // TODO: Implement edit medication functionality
        }

        btnDelete.setOnClickListener {
            showToast("Delete medication clicked")
            // TODO: Implement delete medication functionality
        }
    }

    private fun updateCurrentTime() {
        // This would typically be updated with a timer or when the activity resumes
        // For now, we'll just show the current time as in the design
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val currentDate = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date())
        
        // In a real implementation, you would update the TextViews with these values
        // For now, the layout shows static values matching the design
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}