# Dental Record Automation

A desktop application designed to help dental professionals in schools quickly capture and manage student dental examination records. The app features an Arabic-language UI, speech-to-text for data entry, and PDF generation for parent notifications.

## Features

-   **Arabic-First UI:** The interface is designed Right-to-Left and fully translated into Arabic.
-   **Speech Recognition:** Uses speech-to-text for quick and hands-free entry of student names in Arabic.
-   **Data Management:** Records are saved locally to a CSV file for easy access and management.
-   **PDF Generation:** Generates notification cards for individual students or a summary sheet for all records.
-   **Built-in Font:** Includes an Arabic font, so no system-level font installation is required.
-   **Cross-Platform:** Built with Python and PySide6, allowing it to run on Windows and other operating systems.

## Downloads

The easiest way to use this application is to download the latest pre-built executable (`.exe`) for Windows from the **[Releases](https://github.com/Musn0o/Mayo.git/releases)** page.

## Getting Started (for Developers)

These instructions are for developers who want to run the application from the source code.

### Prerequisites

-   Python 3.9 or higher.
-   A working microphone (for the speech recognition feature).
-   On Linux, you may need to install `portaudio`:
    ```bash
    # For Debian/Ubuntu
    sudo apt-get install portaudio19-dev python3-pyaudio
    ```

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Musn0o/Mayo.git
    cd your-repo
    ```

2.  **Create and activate a virtual environment:**
    ```bash
    python -m venv .venv
    # On Windows:
    # .venv\Scripts\activate
    # On macOS/Linux:
    source .venv/bin/activate
    ```

3.  **Install the required dependencies:**
    ```bash
    pip install -r requirements.txt
    ```

## Usage

Once the setup is complete, run the application with:

```bash
python run_app.py
```

## Building from Source

The project is configured to be built into a standalone Windows executable using PyInstaller.

```bash
# Build the executable
pyinstaller run_app.spec
```

The final application will be located in the `dist/` directory. For official builds, refer to the GitHub Actions workflow defined in `.github/workflows/build-windows.yml`.

## Configuration

Application settings, such as CSV/PDF filenames and the list of diagnoses, can be modified in `src/utils/config.py`.
