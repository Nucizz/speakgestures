package com.example.speakgestureskotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission

class PermitActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_CODE = 123 // Change to your desired code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if permissions are granted
        val cameraPermissionGranted = checkPermission(Manifest.permission.CAMERA)

        if (cameraPermissionGranted) {
            // All required permissions are granted, start the main activity
            startMainActivity()
        } else {
            // Request permissions
            requestPermissions()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return checkSelfPermission(this, permission) == PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA
            ),
            REQUEST_PERMISSIONS_CODE
        )
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<out String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            // Check if all requested permissions are granted
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions are granted, start the main activity
                startMainActivity()
            } else {
                // Handle the case where permissions are denied
                // You can show a message to the user or take other appropriate action
                Toast.makeText(this, "Camera is needed to translate!", Toast.LENGTH_LONG).show()

                finish()
            }
        }
    }
}
