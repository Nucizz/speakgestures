package com.example.speakgestureskotlin

interface CaptionCallback {
    fun onNewCaptionDetected(gesture: String)
}