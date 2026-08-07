# Heart Connect - Project Updates

This document tracks the recent improvements, bug fixes, and performance optimizations made to the Heart Connect Android application.

## 📅 Support Group Schedule

* **Upcoming Sessions View**: Added a new "Schedule" tab within the Community section to display upcoming moderated support sessions.
* **RSVP & Reminders**: Users can RSVP to upcoming clinical support groups and peer events. The system tracks attendees and confirms RSVPs, simulating reminder notifications for hosted sessions.

## 👥 Moderated Anonymous Support Groups

* **Clinical Professional Oversight**: Integrated specialized support groups overseen by verified cardiothoracic specialists, transplant care coordinators, and clinical social workers (e.g., *Pre-Transplant Waitlist Circle*, *Post-Transplant Recovery & Wellness*, and *Caregivers & Loved Ones Support*).
* **Flexible Anonymity & Alias Customization**: Users can toggle between identified posting and anonymous participation with auto-generated or custom peer aliases (e.g., `HopefulHeart_842`) to safeguard privacy and reduce emotional vulnerability barriers.
* **Interactive Group Chat & Clinical Guidance**: Built `GroupChatScreen.kt` featuring distinct visual badges for verified medical moderators, real-time message stream, safety/rules dialogs, and simulated medical moderator responses powered by Gemini for empathetic clinical support.

* **Parallelized Data Loading**: Streamlined the dashboard initialization in `AppViewModel` by using Kotlin Coroutines (`async`/`await`) to fetch user profiles, support groups, and community posts concurrently, significantly reducing the initial loading time.
* **Database Query Optimization**: Refactored `getPosts()` in `FirestoreRepository` to sort by timestamp (`orderBy`) and limit results (`limit(50)`) directly at the database level. This prevents downloading the entire collection into memory, reducing bandwidth usage, memory overhead, and improving end-user experience for long-running lists.
* **Cost Reduction & Latency**: Switched the AI counseling model in `GeminiRepository` from `gemini-3.1-pro-preview` to `gemini-1.5-flash`. This reduces API costs and provides faster response times for users without sacrificing conversational quality.
* **Dynamic AI Prompting**: Enhanced `getCounselingResponse` to accept dynamic `customSystemInstruction` arguments. This allows the AI to better simulate specific peer profiles (matching age, location, and medical history) dynamically during chats, improving reliability and relevance.

## 🐛 Bug Fixes & Stability

* **Build Configuration Fixes**: 
  * Resolved a build failure caused by a missing `debug.keystore` by generating a local keystore for debug signing.
  * Fixed an AAPT resource linking error by ensuring the `img_hero_banner.jpg` placeholder exists in the `drawable` directory.
* **Prevented Empty Submissions**: Added client-side validation using `.trim().isEmpty()` to the `FloatingActionButton` in `AICounselingScreen` and the answer submission button in `CommunityScreen`. Users are now shown a helpful Toast message instead of sending empty data to the backend or AI.
* **Data Serialization Fix**: Fixed a data corruption issue in `backupLogsSecurely` and `restoreLogsSecurely` within `AppViewModel`. The delimiter was changed from `|` to `|||` to prevent parsing errors when user-entered notes contained pipe characters.
* **Duplicate Log Creation**: Fixed a bug in `transcribeAndAddAudioLog` where the `addDailyLog` function was being called twice per transcription, causing duplicate entries in the user's daily log history.
* **Gemini Context Duplication**: Corrected the `getCounselingResponse` payload in `GeminiRepository`. The latest user prompt is no longer appended twice to the `conversationHistory` array, preventing confusion and context-length bloat in the AI model.
