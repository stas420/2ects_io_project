# first meeting summary

### received email:

Szanowni Państwo,

*Nasza firma, MeetSmart Sp. z o.o., poszukuje rozwiązania do automatycznego generowania notatek zdalnych spotkań. Aplikacja powinna umożliwiać automatyczną transkrypcję mówionego tekstu, zapisywanie udostępnionego ekranu oraz wykonywanie OCR na wypadek, gdyby notatki były sporządzane na wirtualnej tablicy.*

*Dodatkowo, oprogramowanie powinno identyfikować użytkowników uczestniczących w spotkaniu i przypisywać im odpowiednie wypowiedzi w transkrypcji, aby późniejszy przegląd notatek był bardziej czytelny i zrozumiały. Aplikacja powinna działać niezależnie od używanego narzędzia do telekonferencji i być możliwa do uruchomienia przez dowolnego uczestnika spotkania.*

*Zapraszam na spotkanie 14. listopada w celu omówienia szczegółów.*

Z poważaniem,
Jan Nowak
MeetSmart Sp. z o.o.

___ 

### meeting 

#### required functionalities:
1. ***speech to text transcription***
2. saving shared slides/presentations screenshots
    - *optical character recognition (OCR)* of the text
3. meeting notes creation $\Rightarrow$ **.pdf, .html, .txt, .md** files
4. **making report after the meeting**, which shall be sent to the participants (by **email**)
5. UI *(???)*:
    - emails of participants, sent through f.e. sendinblue, mailgun, etc.
    - calendar apps integration, for automatic meeting recording
6. every participant should be able to record their screen (with the meeting on, no chess games etc.)
7. supporting: **Zoom, MS Teams, GoogleMeet**
8. notes summary
9. searching in notes *(well, Ctrl+F???)*
10. *\[nice to have]* speakers/participants identification by speech
11. *\[nice to have]* meeting statistics: words per participant, speech speed *(no comment.)*

#### other:
- form unspecified, **does not need to be a webapp**
- approx. **10 weeks of work**
- medium of communication: **AGH IO Slack**

___

### after the meeting
- 3 people team;
- discussed options: 
    - *definitely not a web application*;
    - C++ with Qt?; 
    - Java with JavaFX or something else?;
    - strict first priority is Windows distro for the demo, porting for other systems *as a part of the future*;
- automatic recording, but what if the meeting is not turned on anywhere?
- does this need to work even if the user is not powered on?

#### \> to be discussed and decided at the nearest meeting