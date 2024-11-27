### Project Name: To Efficient Communication and Transcript Summarization (2ECTS) ©™

### Tools:
- OS: **Windows 10 and 11**
- Development environment: **[Qt](https://www.qt.io/download-qt-installer-oss)** (C++)
	- under [*open-source* license](https://www.qt.io/download-open-source)
	- probably with some CMake/bash scripts help for building purposes
	- **Microsoft C++ VS** compiler (version to be specified)
	- in addition to standard library, the project will make use of external libraries:
		- [json parsing library](https://github.com/nlohmann/json)
			- release from 28th Nov 2023, version 3.11.3
		- [voice to speech library - whisper dock](https://github.com/ErcinDedeoglu/WhisperDock)
		- [curlpp for HTTP requests](https://github.com/jpbarrette/curlpp)
			- release from 27th Feb 2017, version 0.8.1
			- as the above is only a wrapper, we also need the original [curl already installed](https://curl.se/windows/) - version 8.11 from 6th Nov 2024
		- [screen capture library](https://github.com/JKnightGURU/ScreenCaptureLib)
- additional needed software: **Docker**

### Organisation framework

**Agile**: **weekly reports** with **regular meetings**, including live workshops and 
ad-hoc meetings.

[Clickup](https://clickup.com) is the tool of choice to provide the team with Kanban boards and other organisation-helping tools.

[Slack](https://slack.com) is the communication tool, provided by the AGH, to make our communication easier and transparent for others.

[Github](https://github.com/stas420/2ects_io_project) is our software version control tool, where we share the source code, instructions of usage, documentation, and every other project-related thing. The contributors are visible in the repo details.