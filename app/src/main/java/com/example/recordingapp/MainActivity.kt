package com.example.recordingapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recordingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: File? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 権限チェック
        if (checkPermission()) {
            setupRecording()
        } else {
            requestPermission()
        }

        binding.recordButton.setOnClickListener {
            if (checkPermission()) {
                toggleRecording()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRecording()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupRecording() {
        // 録音ボタンの初期状態を設定
        updateUI(false)
    }

    private fun toggleRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        try {
            // 出力ファイルのパスを生成
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val audioDir = File(getExternalFilesDir(null), "Recordings")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            outputFile = File(audioDir, "recording_$timestamp.3gp")

            // MediaRecorderの設定
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(outputFile!!.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                
                try {
                    prepare()
                    start()
                    isRecording = true
                    updateUI(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_recording),
                        Toast.LENGTH_SHORT
                    ).show()
                    releaseRecorder()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.error_recording),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            updateUI(false)

            // 録音終了後、ファイルを保存
            outputFile?.let { file ->
                if (file.exists()) {
                    saveRecording(file)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.error_recording),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            releaseRecorder()
        }
    }

    private fun releaseRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
    }

    private fun updateUI(recording: Boolean) {
        if (recording) {
            binding.statusText.text = getString(R.string.status_recording)
            binding.recordButton.text = getString(R.string.record_stop)
            binding.recordButton.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
            )
        } else {
            binding.statusText.text = getString(R.string.status_ready)
            binding.recordButton.text = getString(R.string.record_start)
            binding.recordButton.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            )
        }
    }

    private fun saveRecording(audioFile: File) {
        binding.statusText.text = getString(R.string.status_processing)
        
        // データファイルとして保存（音声ファイルのコピー）
        saveDataFile(audioFile)
        
        // 文字起こしを実行（非同期処理）
        transcribeAudio(audioFile)
    }

    private fun saveDataFile(audioFile: File) {
        try {
            val dataDir = File(getExternalFilesDir(null), "Data")
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val dataFile = File(dataDir, "data_$timestamp.3gp")
            
            audioFile.copyTo(dataFile, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "データ保存エラー: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun transcribeAudio(audioFile: File) {
        coroutineScope.launch {
            try {
                // ML Kit Speech-to-Textで文字起こし
                // 注意: ML Kit Speech-to-Textは主にリアルタイム認識用です
                // 録音ファイルを文字起こしするには、Google Cloud Speech-to-Text APIの使用を推奨します
                // ここでは、基本的な実装として、録音情報と文字起こしの準備をします
                
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val transcriptionDir = File(getExternalFilesDir(null), "Transcriptions")
                if (!transcriptionDir.exists()) {
                    transcriptionDir.mkdirs()
                }
                val transcriptionFile = File(transcriptionDir, "transcription_$timestamp.txt")
                
                // 文字起こし結果（実際の実装では、Google Cloud Speech-to-Text APIを使用）
                // ここでは、録音情報と文字起こしの準備状態を保存
                val transcriptionContent = buildString {
                    appendLine("録音ファイル: ${audioFile.name}")
                    appendLine("録音日時: $timestamp")
                    appendLine("ファイルサイズ: ${audioFile.length()} bytes")
                    appendLine("")
                    appendLine("【文字起こし結果】")
                    appendLine("")
                    appendLine("※ 実際の文字起こし機能を使用するには、")
                    appendLine("Google Cloud Speech-to-Text APIの実装が必要です。")
                    appendLine("")
                    appendLine("録音データは正常に保存されました。")
                    appendLine("文字起こし機能を有効にするには、")
                    appendLine("Google Cloud Speech-to-Text APIの設定を行ってください。")
                }
                
                withContext(Dispatchers.IO) {
                    FileOutputStream(transcriptionFile).use { fos ->
                        fos.write(transcriptionContent.toByteArray())
                    }
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "${getString(R.string.recording_saved)}\n${getString(R.string.transcription_saved)}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.statusText.text = getString(R.string.status_ready)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "文字起こしエラー: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.statusText.text = getString(R.string.status_ready)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseRecorder()
    }
}
