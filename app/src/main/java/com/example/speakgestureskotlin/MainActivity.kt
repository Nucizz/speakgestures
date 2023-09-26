package com.example.speakgestureskotlin

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import com.example.speakgestureskotlin.databinding.ActivityMainBinding


class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private var flash: Boolean = false
    private var tts: Boolean = false

    private lateinit var camera_manager: CameraManager
    private var camera_id: String? = null

    private val cc_maxWords = 10
    private var cc_text = ""
    private var cc_timeout = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, CameraFragment()).commit()

        camera_manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            val cameraIdList = camera_manager.cameraIdList
            if (cameraIdList.isNotEmpty()) {
                camera_id = cameraIdList[0]
            }
        } catch (e: CameraAccessException) {
            Toast.makeText(this, "Couldn't access camera", Toast.LENGTH_SHORT).show()
        }

        binding.flashButton.setOnClickListener {
            flash = !flash
            binding.flashButton.isSelected = flash
            if (camera_id != null) {
                try {
                    camera_manager.setTorchMode(camera_id!!, flash)
                } catch (e: CameraAccessException) {
                    Toast.makeText(this, "Couldn't turn on flash", Toast.LENGTH_SHORT).show()
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

    fun updateCaption(newText: String) {
        cc_text += " $newText"

        if (cc_text.split(" ").size > 3) {
            cc_text = newText
        }

        binding.closedCaption.text = cc_text

        val handler = Handler(Looper.getMainLooper())
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            cc_text = ""
            binding.closedCaption.text = ""
        }, 3000)
    }


}