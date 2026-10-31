package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import android.util.Base64

class AudioRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording() {
        outputFile = File(context.cacheDir, "recording.mp4")
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile!!.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): String? {
        // The recorder is released in finally: if stop() throws (e.g. Stop
        // tapped too quickly), the old catch returned null WITHOUT releasing
        // it — leaking the recorder and potentially holding the microphone,
        // and the next startRecording() overwrote the reference for good.
        try {
            mediaRecorder?.stop()
            return outputFile?.let { file ->
                val bytes = file.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            android.util.Log.e("AudioRecorderHelper", "stopRecording failed", e)
            return null
        } finally {
            try { mediaRecorder?.release() } catch (_: Exception) {}
            mediaRecorder = null
        }
    }
}
