# Dental Student Record Automation

This application helps dental professionals manage student records, with features for Arabic speech-to-text input, data management, and PDF generation.

## Features

- Arabic speech-to-text for student name input
- Student record management (CSV storage)
- PDF generation for dental notification cards
- View and edit records in a table format
- Support for Arabic language interface

## Project Structure

```
├── run_app.py              # Main entry point
├── requirements.txt        # Project dependencies
├── .gitignore            # Git ignore rules
├── README.md             # This file
├── src/                  # Source code
│   ├── __init__.py
│   ├── main.py           # Application entry point
│   ├── ui/               # User interface components
│   │   ├── __init__.py
│   │   ├── gui.py
│   │   └── view_records_window.py
│   ├── data/             # Data management
│   │   ├── __init__.py
│   │   └── data_manager.py
│   ├── pdf/              # PDF generation
│   │   ├── __init__.py
│   │   └── pdf_generator.py
│   ├── audio/            # Audio processing
│   │   ├── __init__.py
│   │   └── speech_recognizer.py
│   └── utils/            # Utilities
│       ├── __init__.py
│       └── config.py
├── assets/               # Project assets
│   └── logo.png
├── docs/                 # Documentation
└── tests/                # Tests
```

## Prerequisites

- Python 3.8 or higher
- A working microphone for speech recognition
- On Linux, you may need additional packages for audio:
  ```bash
  # Ubuntu/Debian
  sudo apt-get install portaudio19-dev python3-pyaudio
  
  # For Arabic font support
  sudo apt-get install fonts-noto-color-emoji
  ```

## Installation

1. Clone this repository:
   ```bash
   git clone <repository-url>
   cd dental-student-record
   ```

2. Create a virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Usage

Run the application:

```bash
python run_app.py
```

## Building for Windows (EXE)

To create a Windows executable, you can use PyInstaller:

```bash
pip install pyinstaller
pyinstaller main.spec
```

The executable will be created in the `dist/` folder.

## Configuration

The application settings are managed in `src/utils/config.py`:
- CSV filename for data storage
- PDF output filename
- Available diagnoses options

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

[Specify your license here]