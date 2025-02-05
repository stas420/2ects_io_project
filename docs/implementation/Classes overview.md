Utilizing base principle of the Java language, the implementation shall be organized into classes. A general decided structure of the application will look like in the figure 1.

![[simplified_class_structure.png]]

*Fig. 1 - Simplified class structure*

The GUI will be a crucial component of the program, as the user should decide when to record the meeting, what emails to include in sending, what note type they deserve, etc.

Below is a list of more in-depth class implementation details:
- *docs/Classes/[[GUIManager]]*
- *docs/Classes/[[NoteCreator]]*
- *docs/Classes/[[AudioCaptureManager]]* (with *SpeechToText* description)
- *docs/Classes/[[ScreenshotCaptureManager]]* (with *OCRProcessor* description)
- *docs/Classes/[[TimestampManager]]*
- *docs/Classes/[[FileGenerator]]*
- *docs/Classes/[[EmailSender]]*