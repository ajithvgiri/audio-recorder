package com.ajithvgiri.audiorecorder

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.ajithvgiri.audiorecorder.utils.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import recorder.main.AudioChunk
import recorder.main.AudioRecorder
import recorder.main.PullTransport
import recorder.main.Recorder
import recorder.model.AudioChannel
import recorder.model.AudioSampleRate
import recorder.model.AudioSource
import recorder.utils.RecorderUtils
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), PullTransport.OnAudioChunkPulledListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //setup recorder
        setUpRecorder()

        buttonStart.setOnClickListener {
            if (!isRecordingStarted) {
                mRecorder.startRecording()
                buttonPause.visibility = View.VISIBLE
                buttonStart.text = getString(R.string.stop_recording)
                startTimer()
            } else {
                try {
                    if (mRecorder != null) {
                        mRecorder.stopRecording()
                    } else {
                        setUpRecorder()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                stopTimer()
                recorderSecondsElapsed = 0
                buttonPause.visibility = View.GONE
                buttonStart.text = getString(R.string.start_recording)
                textViewTimer.text = getString(R.string._00_00_00)
            }
            isRecordingStarted = !isRecordingStarted
        }

        buttonPause.setOnClickListener {
            if (!isRecordingPaused) {
                buttonPause.text = getString(R.string.resume)
                mRecorder.pauseRecording()
                stopTimer()
            } else {
                buttonPause.text = getString(R.string.pause)
                mRecorder.resumeRecording()
                startTimer()
            }
            isRecordingPaused = !isRecordingPaused
        }
    }

    private fun setUpRecorder() {
        mRecorder = AudioRecorder.wav(
            PullTransport.Default(
                RecorderUtils.getMic(AudioSource.MIC, AudioChannel.STEREO, AudioSampleRate.HZ_48000),
                this@MainActivity
            ), file()
        )
    }

    override fun onAudioChunkPulled(audioChunk: AudioChunk) {
        val amplitude = if (isRecordingStarted) audioChunk.maxAmplitude().toFloat() else 0f
    }


    private fun animateVoice(maxPeak: Float) {
        //recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start()
    }

    private fun file(): File {
        //Create Folder
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/AudioRecorder")
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e(Companion.TAG, "Unable to create AudioRecorder folder...please check storage permission")
            }
        }
        Log.d(Companion.TAG, "recorder filename ${Utilities.recordingFilename()}")
        return File(folder, Utilities.recordingFilename())
    }


    private fun startTimer() {
        stopTimer()
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateTimer()
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        }
    }

    private fun updateTimer() {
        runOnUiThread {
            if (isRecordingStarted) {
                recorderSecondsElapsed++
                textViewTimer.text = Utilities.formatSeconds(recorderSecondsElapsed)
            }
        }
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private lateinit var mRecorder: Recorder
        private var isRecordingStarted = false
        private var isRecordingPaused = false
        private var timer: Timer? = null
        private var recorderSecondsElapsed: Int = 0
    }
}
