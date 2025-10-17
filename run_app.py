#!/usr/bin/env python3
"""
Main entry point for the Dental Student Record Automation application.
This file serves as the entry point to run the application from the project root.
"""
import sys
import os

# Add the project root to the Python path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from src.main import main

if __name__ == "__main__":
    main()