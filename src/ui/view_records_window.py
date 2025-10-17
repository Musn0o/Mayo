from PySide6.QtWidgets import (
    QWidget,
    QVBoxLayout,
    QHBoxLayout,
    QTableWidget,
    QTableWidgetItem,
    QPushButton,
    QMessageBox,
)
from PySide6.QtCore import Qt
from src.data.data_manager import load_all_records, FIELD_NAMES, overwrite_all_records

class ViewRecordsWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("View and Edit Records")
        self.setMinimumSize(800, 600)

        main_layout = QVBoxLayout(self)

        self.table = QTableWidget()
        main_layout.addWidget(self.table)

        button_layout = QHBoxLayout()
        self.refresh_button = QPushButton("Refresh")
        self.refresh_button.clicked.connect(self.populate_table)
        button_layout.addWidget(self.refresh_button)

        self.save_button = QPushButton("Save Changes")
        self.save_button.clicked.connect(self.save_changes)
        button_layout.addWidget(self.save_button)

        main_layout.addLayout(button_layout)

        self.populate_table()

    def populate_table(self):
        records = load_all_records()
        
        self.table.setRowCount(len(records))
        self.table.setColumnCount(len(FIELD_NAMES) + 1)
        self.table.setHorizontalHeaderLabels(FIELD_NAMES + ["Generate PDF"])

        for row_idx, record in enumerate(records):
            for col_idx, field in enumerate(FIELD_NAMES):
                item = QTableWidgetItem(record.get(field, ""))
                if field == "Record_ID":
                    item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
                self.table.setItem(row_idx, col_idx, item)
            
            pdf_button = QPushButton("Generate PDF")
            pdf_button.clicked.connect(lambda checked, rec=record: self.generate_single_pdf(rec))
            self.table.setCellWidget(row_idx, len(FIELD_NAMES), pdf_button)

    def generate_single_pdf(self, record):
        try:
            from src.pdf.pdf_generator import generate_notification_card
            pdf_filename = f"notification_card_{record['Record_ID']}_{record['Student_Name']}.pdf"
            generate_notification_card(record, pdf_filename)
            QMessageBox.information(self, "Success", f"PDF generated successfully: {pdf_filename}")
        except Exception as e:
            QMessageBox.critical(self, "Error", f"Could not generate PDF: {e}")

    def save_changes(self):
        updated_records = []
        for row in range(self.table.rowCount()):
            record = {}
            for col, field in enumerate(FIELD_NAMES):
                item = self.table.item(row, col)
                record[field] = item.text() if item else ""
            updated_records.append(record)

        try:
            overwrite_all_records(updated_records)
            QMessageBox.information(self, "Success", "Changes saved successfully!")
        except Exception as e:
            QMessageBox.critical(self, "Error", f"Could not save changes: {e}")
