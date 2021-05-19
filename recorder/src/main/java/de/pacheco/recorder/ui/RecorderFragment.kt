package de.pacheco.recorder.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import de.pacheco.recorder.databinding.RecorderFragmentBinding
import de.pacheco.recorder.waverecorder.RecorderState
import de.pacheco.recorder.waverecorder.Sounds
import de.pacheco.recorder.waverecorder.WaveRecorder

private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 21

class RecorderFragment : Fragment() {
    private val TAG: String = "RecorderFragment"
    private var filePath: String = ""
    private var isRecording = false
    private lateinit var waveRecorder: WaveRecorder
    private lateinit var noiseSuppressorSwitch: SwitchMaterial


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: RecorderFragmentBinding = RecorderFragmentBinding.inflate(inflater)
        filePath = context?.externalCacheDir?.absolutePath + "/audioFile.wav"
        Sounds.load(filePath)
        waveRecorder = WaveRecorder(filePath)

        waveRecorder.onStateChangeListener = recordStateChangeListener()
        binding.record.setOnTouchListener(recordWhileTouchingListener())
        noiseSuppressorSwitch = binding.noiseSuppressor
        noiseSuppressorSwitch.setOnCheckedChangeListener(switchNoiseSuppression())
        binding.play.setOnClickListener(playListener())
        return binding.root
    }

    override fun onDestroy() {
        Sounds.close()
        super.onDestroy()
    }

    private fun playListener() = { _: View ->
        Sounds.play(filePath)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    waveRecorder.startRecording()
                }
            }
            else -> {
            }
        }
    }

    private fun recordStateChangeListener(): (RecorderState) -> Unit =
        {
            when (it) {
                RecorderState.RECORDING -> startRecording()
                RecorderState.STOP -> {
                    stopRecording()
                    Sounds.load(filePath)
                }
            }
        }

    private fun switchNoiseSuppression() =
        { _: Button, isChecked: Boolean ->
            waveRecorder.noiseSuppressorActive = isChecked
            if (isChecked)
                Toast.makeText(context, "Noise Suppressor Activated", Toast.LENGTH_SHORT).show()
        }

    //TODO show that it is recording
    private fun recordWhileTouchingListener() =
        { _: View, event: MotionEvent ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (checkRecordPermission()) waveRecorder.startRecording()
                    else ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSIONS_REQUEST_RECORD_AUDIO
                    )
                    Log.i("Recorder", "Record button pressed")
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (checkRecordPermission()) waveRecorder.stopRecording()

                    Log.i("Recorder", "Record button released")
                    true
                }
                else -> {
                    false
                }
            }
        }


    private fun checkRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording() {
        Log.d(TAG, waveRecorder.audioSessionId.toString())
        isRecording = true
        noiseSuppressorSwitch.isEnabled = false
    }

    private fun stopRecording() {
        isRecording = false
        Toast.makeText(context, "File saved at : $filePath", Toast.LENGTH_LONG).show()
        noiseSuppressorSwitch.isEnabled = true
    }
}



