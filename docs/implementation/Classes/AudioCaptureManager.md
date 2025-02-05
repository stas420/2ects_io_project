#### Package: *audio_capturing*

This class is a global singleton class, which controls audio capturing. It requires the user to *set up an audio loopback on their devices*, which is then utilized to record all the device's audio output. Once it is activated, it periodically records audio, sends to Google API's ***SpeechToText***, and stores the answered text with timestamp in a simple *AudioCapture* class.

![[audiocapturemanager_diag.png]]

*Fig. 1 - Diagram picturing general principle of AudioCaptureManager working schema*

#### Methods:
- **getInstance()** $\rightarrow$ *void* - retrieves the Singleton instance;
- **Activate()** $\rightarrow$ *void*- activates the recording session, i.e. initializes all needed classes and runs the process, along with saving the data;
- **Deactivate()** $\rightarrow$ *void* - deactivates the process and ends recording;
- **PopTheOldestAudioCapture()** $\rightarrow$ *Optional\<AudioCapture>* - filters the private list, which contains done and timestamped audio captures, and returns the oldest one to process;

Exemplary usage:
```java
AudioCaptureManager.getInstance().Active();

Optional<AudioCapture> ac = AudioCaptureManager.getInstance().PopTheOldestAudioCapture();

if (ac.isPresent()) {
	System.println(ac.get().getContent());
}

if (AudioCaptureManager.getInstance().isActive()) {
	AudioCaptureManager.getInstance().Deactivate();
}
```
