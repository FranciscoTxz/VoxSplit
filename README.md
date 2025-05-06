# VoxSplit
Turn your into text with precision and automatically separate each speaker’s voice. Whether for interviews, meetings, or personal notes, **VoxSplit** helps you capture every word without missing a detail.

## Features ⚒️
- Audio Selection & Upload
   - Choose an audio file from your device and upload it for processing.
- Smart Transcription with Diarization
   - Audio is transcribed and segmented by speaker, enabling clear separation of who said what.
- Audio Player with Linked Transcription
   - Interact with the transcription: tap any part of the text to jump to the exact second in the audio.
- Processing Summary Screen
   - After processing, view a detailed summary of the transcription and diarization results.
- History Screen
   - Access past transcriptions. Tapping a history item brings up the corresponding summary and linked transcription.

## Tech Stack 📲
- Kotlin
- Dagger Hilt – for dependency injection
- Retrofit – for networking and API integration
- Room – for local database and offline access

## Usage 📍
- Upload Your Audio File
   - On the main screen, tap the "Upload File" button and select an audio file from your device.
- Pre-Summary Screen
   - After selecting the file, you'll be taken to the pre-summary screen. Here, you'll find an audio player, and you can select the number of speakers and the language of the audio. From this screen, you can either proceed with processing or choose a different audio file.
- Processing Screen
   - Once processing starts, you’ll be taken to the processing screen where the transcription and diarization results from the API are displayed. The audio player is available, and tapping on any transcript segment takes you to the exact second where that speaker starts talking.
- View from History
   - You can revisit any previous processing session from the history screen. Selecting an item will open the corresponding result, including transcription, diarization, and audio playback.

## Screenshots 
- 🏠 Welcome Dialog
<p align="center">
   <img src="images/img_3.png" alt="Welcome screen" width="250" />
</p>

- 🔊 Audio File Upload
<p align="center">
   <img src="images/img_2.png" alt="Upload Audio File" width="250" />
</p>

- 📄 Audio File Summary
<p align="center">
   <img src="images/img_6.png" alt="Audio file Summary" width="250" />
</p>

- ✅ Result of Audio File Processing
<p align="center">
   <img src="images/img.png" alt="Audio file Summary" width="250" />
</p>

- 🔄 History of Processed Audio Files
<p align="center">
   <img src="images/img_1.png" alt="History of Processed Audio Files" width="250" />
</p>

- 🌒 Dark Mode
<p align="center">
   <img src="images/img_4.png" alt="Audio File Upload Dark Mode" width="200" />
  <img src="images/img_7.png" alt="Audio file Summary Dark Mode" width="200" />
  <img src="images/img_5.png" alt="Result of Audio File Processing Dark Mode" width="200" />
</p>

## 🎧 Sample Audio & API Integration

The app includes a sample transcription based on a demo audio file to showcase its capabilities.

<audio controls>
  <source src="audio/example.mp3" type="audio/mpeg">
  --Your browser does not support the audio element--
</audio>


This processing is powered by an external transcription and diarization API available at:

API Repository: [https://github.com/FranciscoTxz/VoxSplitApi](https://github.com/FranciscoTxz/VoxSplitAPI)

To make VoxSplit fully functional with your own API instance, you must update the Retrofit service call in the app:

- Locate the Retrofit interface used for API communication (e.g., TranscriptionService.kt).
- Modify the baseUrl to match your deployed API endpoint.
- Adjust any necessary headers, request bodies, or parameters based on your server configuration.
