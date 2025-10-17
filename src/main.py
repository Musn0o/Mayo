import sys
import os
# Add the project root to the Python path to allow imports
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from PySide6.QtWidgets import QApplication
from src.ui.gui import DentistApp
from src.data.data_manager import initialize_csv

def main():
    # 1. Initialize the CSV header if it doesn't exist
    initialize_csv()

    # 2. Create and run the PySide6 main window
    app = QApplication(sys.argv)
    window = DentistApp()
    window.show()
    sys.exit(app.exec())

if __name__ == "__main__":
    main()
