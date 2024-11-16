# Client requirements

The end product is to deliver meeting notes, which shall be automatically created from meeting recording, or live meeting's screen display. It should provide:
- **speech-to-text** transcription, and put said lines in output note (*as a nice to have: distinguish the speakers*);
- **optical character recognition** for parsing text from shared screen (f.e. presentation slides) and also put them in output notes;
- **screen capturing**, for putting changing presentation slides into the note (or, at least, a link/reference to a saved picture);
- **generating the notes into a few formats**, such as .pdf, .txt, .html and/or .md;
- **support for the most popular meeting applications**, such a Zoom, MS Teams or Google Meet;
- **sending generated notes to provided meeting participants**.

The form of application was *not* specified by the client. Time to deliver is estimated to be around **10 weeks**.

# Expected inputs - application usage

The application is to be used by user to generate notes from the meeting, which shall be set up as follows:
1. User opens the meeting screen (f.e. browser or application window)
2. User runs our application with pointing which screen is to be recorded (the sounds output will be collected from master mix of the device)
	- **Note:** The user needs to have this window opened, to properly record the meeting contents. The user open other sound sources, which may disturb speech-to-text transcription.
3. The meeting proceeds and the data is parsed to temporary files...
4. After the meeting ends the data is finalized, and the emails are sent to provided emails (which may be changed during the meeting).

Above being said, the application needs device's **sounds source**, **indicated screen** and **email addresses of participants** to sends the reports to.

# Expected output note

![[Note design.png]]

*Fig.1: Draft of an expected output note*

The note is to contain:
- **meeting title, duration, and some other metadata**;
- meeting contents (from top to bottom), repeated as many times as needed:
	- **made slide screenshots** from screen-capturing;
	- **screen transcription** underneath, as a description, from OCR[^1];
	- **speech transcription**.

Notes should be available as .txt, .md, .html and .pdf formats. The formats are to be generated as follows:
1. Generation of *.txt*: *Pure text transcription, with OCR[^1] and images references;*
2. Generation of *.md*: *Made of objects provided by external libraries, which shall be the same as the contents of .txt files, but with additional formatting, embedded image references and ready to read with Markdown readers;*
3. Generation of *.html*: *Made of ready .md file, which may be directly converted to HTML format;*
4. Generation of *.pdf*: *Made of ready .html file, which only requires a few additional meta-data, such as font family, font size, etc.*

The notes are to be **always** be generated in all of formats specified above.

# Team skills matrix

| Skill | Aleksander Brzykcy | Paweł Młodkowski | Stanisław Niemczewski |
| :---: | :---: | :---: | :---: |
| Documentation design | Basics | Basics | Basics | 
| C++ programming | Lower-intermediate | Lower-intermediate | Intermediate | 
| Java programming | Lower-intermediate | Basics | Lower-intermediate | 
| Python programming | Basics | Basics | Basics | 
| Software testing/QA | Basics | N/A | N/A | 
| Web applications development | N/A | N/A | N/A | 
| Agile/Scrum | N/A | Basics | Basics | 

# Technology choice

Based on above skills matrix, and meeting discussion, the team decided to work with specified technologies, such as **Qt** environment with **C++** language (see: file ```Work Organisation```). The application will be provided for Windows desktop devices in the first place.

[^1]: OCR = Optical Character Recognition