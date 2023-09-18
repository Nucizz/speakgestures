package com.example.speakgestureskotlin

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.speakgestureskotlin.databinding.ActivityMainBinding
import com.example.speakgestureskotlin.ml.BisindoModelV9
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var flash: Boolean = false
    private var tts: Boolean = false

    private lateinit var modelRecognition: BisindoModelV9

    private lateinit var camera_manager: CameraManager
    private var camera_id: String? = null

    private val cc_maxWords = 10
    private var cc_text = ""
    private var cc_timeout = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initCamera()

        modelRecognition = BisindoModelV9.newInstance(applicationContext)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 30, 1662), DataType.FLOAT32)

        val outputs = modelRecognition.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

//        modelRecognition.setRecognitionListener { recognizedText ->
//            updateCaption(recognizedText)
//        }



        binding.flashButton.setOnClickListener{
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

        binding.ttsButton.setOnClickListener{
            tts = !tts
            binding.ttsButton.isSelected = tts
        }

        binding.closedCaption.text = ""

        setContentView(binding.root)
    }

    private fun updateCaption(newText: String) {
        cc_text += " $newText"

        if (cc_text.split(" ").size > cc_maxWords) {
            cc_text = newText
        }

        binding.closedCaption.text = cc_text

        val handler = Handler(Looper.getMainLooper())
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            cc_text = ""
            binding.closedCaption.text = ""
        }, cc_timeout)
    }

    private fun initCamera() {
        camera_manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            val cameraIdList = camera_manager.cameraIdList
            if (cameraIdList.isNotEmpty()) {
                camera_id = cameraIdList[0]
            }
        } catch (e: CameraAccessException) {
            Toast.makeText(this, "Couldn't access camera", Toast.LENGTH_SHORT).show()
        }
    }
}