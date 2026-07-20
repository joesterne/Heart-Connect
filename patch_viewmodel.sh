sed -i '/val transcription = geminiRepository.transcribeAudio(base64Audio)/c \
            val transcription = geminiRepository.transcribeAudio(base64Audio)\
            val parts = transcription.split("|||")\
            if (parts.size >= 3) {\
                val extractedMood = parts[0].trim().toIntOrNull() ?: mood\
                val extractedSymptoms = parts[1].trim()\
                val extractedNotes = parts[2].trim()\
                addDailyLog(extractedMood, extractedSymptoms, extractedNotes)\
            } else {\
                addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")\
            }' app/src/main/java/com/example/viewmodel/AppViewModel.kt
