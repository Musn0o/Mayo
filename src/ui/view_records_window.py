from PySide6.QtWidgets import (
    QWidget,
    QVBoxLayout,
    QHBoxLayout,
    QTableWidget,
    QTableWidgetItem,
    QPushButton,
    QMessageBox,
    QHeaderView,
)
from PySide6.QtCore import Qt
from src.data.data_manager import load_all_records, FIELD_NAMES, overwrite_all_records

class ViewRecordsWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("View and Edit Records (عرض وتعديل السجلات)")
        self.setStyleSheet("QWidget { font-size: 14pt; }")
        self.showMaximized()

        main_layout = QVBoxLayout(self)

        self.table = QTableWidget()
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.Stretch)
        main_layout.addWidget(self.table)

        button_layout = QHBoxLayout()
        self.refresh_button = QPushButton("تحديث")
        self.refresh_button.clicked.connect(self.populate_table)
        button_layout.addWidget(self.refresh_button)

        self.save_button = QPushButton("حفظ التغييرات")
        self.save_button.clicked.connect(self.save_changes)
        button_layout.addWidget(self.save_button)

        main_layout.addLayout(button_layout)

        self.populate_table()

    def populate_table(self):
        records = load_all_records()

        header_map = {
            "Record_ID": "المعرف",
            "Date": "التاريخ",
            "School_Name": "اسم المدرسة",
            "Student_Name": "اسم الطالب",
            "Class_No": "الفصل",
            "Department": "الشعبة",
            "Diagnosis": "التشخيص",
        }
        arabic_headers = [header_map.get(field, field) for field in FIELD_NAMES]

        self.table.setRowCount(len(records))
        self.table.setColumnCount(len(FIELD_NAMES) + 1)
        self.table.setHorizontalHeaderLabels(arabic_headers + ["إنشاء PDF"])

        # Hide the Record_ID column as it's redundant with the row numbers
        if "Record_ID" in FIELD_NAMES:
            record_id_index = FIELD_NAMES.index("Record_ID")
            self.table.setColumnHidden(record_id_index, True)

        for row_idx, record in enumerate(records):
            for col_idx, field in enumerate(FIELD_NAMES):
                item = QTableWidgetItem(record.get(field, ""))
                if field == "Record_ID":
                    item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
                self.table.setItem(row_idx, col_idx, item)

            pdf_button = QPushButton("إنشاء PDF")
            pdf_button.clicked.connect(lambda checked, rec=record: self.generate_single_pdf(rec))
            self.table.setCellWidget(row_idx, len(FIELD_NAMES), pdf_button)

    def generate_single_pdf(self, record):
        try:
            from src.pdf.pdf_generator import generate_notification_card
            from src.utils.config import CARDS_DIRECTORY_AR
            import os

            # Ensure the student cards directory exists
            if not os.path.exists(CARDS_DIRECTORY_AR):
                os.makedirs(CARDS_DIRECTORY_AR)

            pdf_filename = f"notification_card_{record['Record_ID']}_{record['Student_Name']}.pdf"
            full_path = os.path.join(CARDS_DIRECTORY_AR, pdf_filename)
            generate_notification_card(record, full_path)
            QMessageBox.information(self, "Success", f"PDF generated successfully: {full_path}")
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
