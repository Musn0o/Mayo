import os

# Define output directories
DB_DIRECTORY_AR = "قاعدة البيانات"
CARDS_DIRECTORY_AR = "بطاقات الطلاب"

# 1. Define the file path
CSV_FILENAME = os.path.join(DB_DIRECTORY_AR, "student_records.csv")
PDF_FILENAME = "dental_records_summary.pdf"  # New output file name

# 2. Define the header row (the columns for the spreadsheet)
FIELD_NAMES = [
    "Record_ID",
    "Date",
    "School_Name",
    "Student_Name",
    "Class_No",
    "Department",
    "Diagnosis",
]

# The 7 or 8 standardized diagnoses
DIAGNOSES = [
    "قلع سن",
    "حشوات أسنان",
    "تطبيق فلورايد",
    "تنظيف أسنان",
    "أخرى",
]
