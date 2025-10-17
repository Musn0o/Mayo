import speech_recognition as sr

def transcribe_arabic_name():
    """
    Captures audio from the microphone and uses the Google Speech Recognition API
    (online, requires internet) to transcribe the Arabic text.
    """
    r = sr.Recognizer()

    with sr.Microphone() as source:
        r.adjust_for_ambient_noise(source)

        print("\a")  # Play a beep sound
        print(
            "\nBEEP! Say the student's name now (will listen for max 5 seconds)..."
        )
        try:
            audio = r.listen(source, phrase_time_limit=5, timeout=3)
        except sr.WaitTimeoutError:
            print("❌ No speech detected within the time limit.")
            return ""

        print("Processing audio... (This requires internet access)")

    try:
        recognized_text = r.recognize_google(audio, language="ar-SA")  # type: ignore

        print(f"✅ Recognized: {recognized_text}")
        return recognized_text.strip()

    except sr.UnknownValueError:
        print("❌ Could not understand audio (too quiet, unclear, or service error).")
        return ""
    except sr.RequestError as e:
        print(
            f"❌ Could not request results from Google Speech Recognition service; check internet connection. Error: {e}"
        )
        return ""
    except Exception as e:
        print(f"An unexpected error occurred during transcription: {e}")
        return ""