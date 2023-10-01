package com.example.speakgestureskotlin

import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.fragment.app.FragmentActivity
import com.example.speakgestureskotlin.databinding.ActivityMainBinding
import java.util.*


class MainActivity : FragmentActivity(), CaptionCallback, TextToSpeech.OnInitListener {

    private var tts_service: TextToSpeech? = null
    private var tts: Boolean = false

    private lateinit var binding: ActivityMainBinding

    private var flash: Boolean = false

    companion object{
        var currentCameraLens = CameraSelector.LENS_FACING_FRONT
    }

    private val cc_maxWords = 15
    private var cc_text = ""
    private var cc_current = ""
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_view, CameraFragment(currentCameraLens)).commit()

        tts_service = TextToSpeech(this, this)

        binding.cameraButton.setOnClickListener {
            binding.flashButton.isSelected = false
            flash = false
            currentCameraLens = if (currentCameraLens == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_view, CameraFragment(currentCameraLens)).commit()
        }

        binding.flashButton.setOnClickListener {
            flash = !flash
            binding.flashButton.isSelected = flash

            if (currentCameraLens == CameraSelector.LENS_FACING_FRONT) {
                currentCameraLens = CameraSelector.LENS_FACING_BACK

                binding.frontFlash.visibility = if (flash) {
                     View.VISIBLE
                } else {
                    View.GONE
                }
            } else {
                currentCameraLens = CameraSelector.LENS_FACING_FRONT

                val cameraFragment = supportFragmentManager.findFragmentById(R.id.fragment_view)
                if (cameraFragment is CameraFragment) {
                    cameraFragment.toggleFlash(flash)
                }
            }
        }

        binding.ttsButton.setOnClickListener {
            tts = !tts
            binding.ttsButton.isSelected = tts
        }

        binding.closedCaption.text = ""

        setContentView(binding.root)


    }

    private fun updateCaption(newText: String) {
        if (newText != cc_current || cc_text.isEmpty()) {
            cc_current = newText

            if (tts) {
                tts_service!!.speak(newText, TextToSpeech.QUEUE_FLUSH, null, "")
            }

            cc_text += " $newText"

            if (cc_text.split(" ").size > cc_maxWords) {
                cc_text = newText
            }

            runOnUiThread {
                binding.closedCaption.visibility = View.VISIBLE
                binding.closedCaption.text = cc_text

                // Cancel the previous timer, if any
                timer?.cancel()

                // Start a new timer to clear the caption after 3 seconds
                timer = object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Do nothing on tick
                    }

                    override fun onFinish() {
                        runOnUiThread {
                            cc_text = ""
                            binding.closedCaption.text = ""
                            binding.closedCaption.visibility = View.GONE
                        }
                    }
                }
                timer?.start()
            }
        }
    }

    //implementasi callback CaptionCallback
    override fun onNewCaptionDetected(gesture: String) {
        updateCaption(gesture)
    }

    override fun onDestroy() {
        if (tts_service != null) {
            tts_service!!.stop()
            tts_service!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        val result = tts_service!!.setLanguage(Locale("id", "ID"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Speech language is not supported!", Toast.LENGTH_SHORT).show()
            tts_service!!.language = Locale.US
        }
    }

}