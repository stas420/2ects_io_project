
# Dokumentacja Testów

## Spis Treści
1. [Testy Przechwytywania Dźwięku (AudioCaptureTests)](#testy-przechwytywania-dźwięku-audiocapturetests)
2. [Testy Wysyłania E-maili (EmailTests)](#testy-wysyłania-e-maili-emailtests)
3. [Testy Przechwytywania Ekranu (ScreenCaptureManagerTests)](#testy-przechwytywania-ekranu-screencapturemanagertests)
4. [Testy Zrzutów Ekranu (ScreenCaptureTests)](#testy-zrzutów-ekranu-screencapturetests)
5. [Testy Rozpoznawania Mowy (SpeechTests)](#testy-rozpoznawania-mowy-speechtests)
6. [Testy OCR (OCRProcessorTests)](#testy-rozpoznawania-mowy-speechtests)

---

## Testy Przechwytywania Dźwięku (AudioCaptureTests)
### Opis
Testy mają na celu weryfikację poprawności przechwytywania dźwięku oraz jego transkrypcji przy użyciu Google Speech API. Testowane są różne scenariusze, takie jak przechwytywanie dźwięku z mikrofonu, konwersja nagrań do formatu bajtowego oraz transkrypcja dźwięku na tekst. Dodatkowo uwzględniono testy pozytywne i negatywne, sprawdzające reakcję systemu na poprawne i błędne dane.

### Testowane Funkcje
- **Przechwytywanie dźwięku z mikrofonu**: weryfikacja, czy dane audio są poprawnie przechwytywane.
- **Konwersja nagrania do formatu bajtowego**: testowanie funkcji przekształcania plików audio na format binarny.
- **Transkrypcja dźwięku na tekst**: sprawdzanie skuteczności i dokładności transkrypcji.

### Przykładowe Assercje
- Sprawdzenie, czy dane audio zostały poprawnie zapisane.
- Weryfikacja obecności transkrypcji.
- Testy odporności na błędy przy próbie transkrypcji uszkodzonych plików.

---

## Testy Wysyłania E-maili (EmailTests)
### Opis
Testy weryfikują poprawność działania klasy `EmailSender`, obsługującej wysyłanie e-maili z załącznikami. Sprawdzane są takie aspekty jak poprawność działania wzorca Singleton, prawidłowe ustawienie danych uwierzytelniających oraz skuteczność wysyłania wiadomości e-mail przy użyciu mockowania klasy `Transport`.

### Testowane Funkcje
- **Wzorzec Singleton**: sprawdzenie, czy instancja `EmailSender` jest unikalna i niepowielana.
- **Ustawienie danych uwierzytelniających**: weryfikacja poprawności zapisania loginu i hasła.
- **Wysyłanie e-maila**: testowanie poprawności wysyłania wiadomości, w tym adresatów, tematu i treści.

### Przykładowe Assercje
- Upewnienie się, że metoda `Transport.send()` została wywołana.
- Weryfikacja poprawności adresu nadawcy i odbiorcy.
- Sprawdzanie, czy e-mail zawiera prawidłowe załączniki.

---

## Testy Przechwytywania Ekranu (ScreenCaptureManagerTests)
### Opis
Testy sprawdzają działanie klasy `ScreenCaptureManager`, odpowiedzialnej za przechwytywanie zrzutów ekranu. Obejmują one weryfikację poprawności wzorca Singleton, obsługi aktywacji i dezaktywacji przechwytywania, tworzenia zrzutów ekranu (z użyciem mockowania klasy `Robot`) oraz porównywania obrazów w celu wykrycia zmian.

### Testowane Funkcje
- **Wzorzec Singleton**: testowanie unikalności instancji `ScreenCaptureManager`.
- **Aktywacja i dezaktywacja** przechwytywania: weryfikacja działania metod aktywujących i dezaktywujących przechwytywanie.
- **Tworzenie zrzutów ekranu**: sprawdzenie poprawności przechwytywania obrazu z ekranu.
- **Porównywanie obrazów**: testowanie dokładności porównywania dwóch zrzutów ekranu.

### Przykładowe Assercje
- Sprawdzenie poprawności utworzonych zrzutów ekranu.
- Weryfikacja zgodności porównywanych obrazów.
- Testowanie poprawnego działania przy różnych rozdzielczościach ekranu.

---

## Testy Zrzutów Ekranu (ScreenCaptureTests)
### Opis
Proste testy jednostkowe sprawdzające konstruktor i metody klasy `ScreenCapture`. Testy koncentrują się na poprawności tworzenia obiektów, odczycie obrazu oraz zapisie znacznika czasu.

### Testowane Funkcje
- **Tworzenie obiektu `ScreenCapture`**: weryfikacja poprawności inicjalizacji obiektu.
- **Odczyt obrazu i znacznika czasu**: testowanie metod dostępowych.

### Przykładowe Assercje
- Upewnienie się, że zapisany obraz i timestamp są zgodne z oczekiwaniami.
- Testowanie poprawnego działania przy różnych formatach obrazów.

---

## Testy Rozpoznawania Mowy (SpeechTests)
### Opis
Testy weryfikujące działanie klas `GoogleSpeech`, `AudioUtil` i `SpeechToText`. Testy obejmują zarówno scenariusze pozytywne, jak i negatywne, sprawdzając poprawność konwersji plików audio do bajtów, skuteczność transkrypcji oraz dokładność rozpoznawania mowy. Dodatkowo testowane są przypadki ekstremalne, takie jak uszkodzone pliki audio.

### Testowane Funkcje
- **Konwersja pliku audio do bajtów**: testowanie funkcji przekształcania plików audio.
- **Transkrypcja plików audio**: sprawdzenie poprawności działania Google Speech API.
- **Testy dokładności rozpoznawania mowy**: ocena skuteczności w różnych warunkach.
- **Testy pozytywne i negatywne (alwaysPasses, alwaysFails)**: weryfikacja poprawności działania testów.

### Przykładowe Assercje
- Weryfikacja poprawności transkrypcji.
- Sprawdzenie dokładności rozpoznawania mowy.
- Testy obsługi błędów w przypadku błędnej transkrypcji.
- Ocena wydajności transkrypcji dla długich nagrań.

---

## Testy OCR (OCRProcessorTests)

### Opis
Testy jednostkowe mające na celu weryfikację poprawności działania klasy `OCRProcessor`, odpowiedzialnej za rozpoznawanie tekstu na obrazach przy użyciu biblioteki Tesseract. Testy obejmują przetwarzanie plików graficznych, przetwarzanie obrazów w pamięci oraz obsługę wyjątków w przypadku zamknięcia procesu OCR.

### Testowane Funkcje
- **Przetwarzanie plików graficznych**: testowanie poprawności rozpoznawania tekstu z plików obrazów. 
- **Przetwarzanie obrazów w pamięci (BufferedImage)**: weryfikacja działania OCR na obrazach utworzonych w pamięci.
- **Obsługa wyjątków po zamknięciu procesu OCR**: sprawdzanie, czy system poprawnie reaguje na próby przetwarzania po zamknięciu instancji OCR.

### Przykładowe Assercje
- Weryfikacja poprawności rozpoznanego tekstu.
- Upewnienie się, że wynik OCR jest zgodny z oczekiwaniami.
- Testowanie poprawnego rzucania wyjątków po zamknięciu instancji OCR.
