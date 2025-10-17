import csv
import os
from src.utils.config import CSV_FILENAME, FIELD_NAMES

def get_next_record_id():
    """Reads the CSV file and returns the next available Record_ID."""
    try:
        with open(CSV_FILENAME, mode="r", newline="", encoding="utf-8") as file:
            reader = csv.DictReader(file, fieldnames=FIELD_NAMES)
            next(reader)  # Skip header
            last_id = 0
            for row in reader:
                try:
                    # Assuming Record_ID is the first field and always present
                    last_id = max(last_id, int(row[FIELD_NAMES[0]]))
                except:
                    pass
            return last_id + 1
    except FileNotFoundError:
        return 1
    except Exception as e:
        print(f"Error calculating next ID, starting at 1: {e}")
        return 1


def initialize_csv():
    """Checks if the CSV file exists and writes the header if it doesn't."""
    if not os.path.exists(CSV_FILENAME):
        with open(CSV_FILENAME, mode="w", newline="", encoding="utf-8") as file:
            writer = csv.DictWriter(file, fieldnames=FIELD_NAMES)
            writer.writeheader()
        print(f"Created new CSV file: {CSV_FILENAME} with header.")


def save_new_record(data):
    """Appends a new record to the CSV file."""
    try:
        file_exists = os.path.exists(CSV_FILENAME)
        if not file_exists:
            initialize_csv()

        with open(CSV_FILENAME, mode="a", newline="", encoding="utf-8") as file:
            writer = csv.DictWriter(file, fieldnames=FIELD_NAMES)
            writer.writerow(data)
        print("Record saved successfully!")
    except Exception as e:
        print(f"Oops! Something went wrong saving the record: {e}")


def load_all_records():
    """Loads all records from the CSV file."""
    records = []
    try:
        with open(CSV_FILENAME, mode="r", newline="", encoding="utf-8") as file:
            reader = csv.DictReader(file, fieldnames=FIELD_NAMES)
            next(reader)  # Skip header
            for row in reader:
                # Append the raw dictionary row
                records.append(row)
    except FileNotFoundError:
        pass  # Return empty list if file doesn't exist
    return records

def overwrite_all_records(records):
    """Overwrites the entire CSV file with a new list of records."""
    try:
        with open(CSV_FILENAME, mode="w", newline="", encoding="utf-8") as file:
            writer = csv.DictWriter(file, fieldnames=FIELD_NAMES)
            writer.writeheader()
            writer.writerows(records)
        print("All records have been overwritten successfully.")
    except Exception as e:
        print(f"Oops! Something went wrong overwriting the records: {e}")
        raise e # Re-raise the exception to be caught by the GUI
