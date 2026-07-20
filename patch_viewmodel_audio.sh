sed -i '/fun addDailyLog/i \
    private val _isTranscribing = MutableStateFlow(false)\
    val isTranscribing = _isTranscribing.asStateFlow()\
\
    fun transcribeAndAddAudioLog(base64Audio: String, mood: Int, symptoms: String) {\
        viewModelScope.launch {\
            _isTranscribing.value = true\
            val transcription = geminiRepository.transcribeAudio(base64Audio)\
            addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")\
            _isTranscribing.value = false\
            _notificationEvent.emit("Audio transcribed and log added.")\
        }\
    }\
' app/src/main/java/com/example/viewmodel/AppViewModel.kt
