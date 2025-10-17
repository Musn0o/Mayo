import datetime
import os
import sys

from PySide6.QtWidgets import (
    QApplication,
    QMainWindow,
    QWidget,
    QGridLayout,
    QLabel,
    QLineEdit,
    QPushButton,
    QComboBox,
    QMessageBox,
    QFrame,
)
from PySide6.QtCore import Qt

from src.utils.config import DIAGNOSES, PDF_FILENAME
from src.data.data_manager import get_next_record_id, save_new_record, load_all_records
from src.audio.speech_recognizer import transcribe_arabic_name
from src.pdf.pdf_generator import generate_combined_pdf

try:
    import reportlab

    HAS_REPORTLAB = True
except ImportError:
    HAS_REPORTLAB = False


class DentistApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Dental Student Record Automation (المدخلات السنية)")
        self.setFixedWidth(600)
        self.create_widgets()

    def create_widgets(self):
        main_widget = QWidget()
        self.setCentralWidget(main_widget)
        main_widget.setLayoutDirection(Qt.LayoutDirection.RightToLeft)
        layout = QGridLayout(main_widget)

        self.school_entry = QLineEdit("أبو بكر الصديق")
        self.school_entry.setAlignment(Qt.AlignmentFlag.AlignLeft)
        self.class_entry = QLineEdit("الأول")
        self.class_entry.setAlignment(Qt.AlignmentFlag.AlignLeft)
        self.department_entry = QLineEdit("أ")
        self.department_entry.setAlignment(Qt.AlignmentFlag.AlignLeft)
        self.name_entry = QLineEdit()
        self.name_entry.setPlaceholderText("اضغط على الزر للتسجيل الصوتي")
        self.name_entry.setAlignment(Qt.AlignmentFlag.AlignLeft)
        self.diagnosis_dropdown = QComboBox()
        self.diagnosis_dropdown.addItems(DIAGNOSES)
        self.diagnosis_dropdown.setCurrentIndex(0)

        layout.addWidget(QLabel("اسم المدرسة / School Name"), 0, 0)
        layout.addWidget(self.school_entry, 0, 1, 1, 3)
        layout.addWidget(QLabel("الفصل / Class No"), 1, 0)
        layout.addWidget(self.class_entry, 1, 1)
        layout.addWidget(QLabel("الشعبة / Department"), 1, 2)
        layout.addWidget(self.department_entry, 1, 3)
        layout.addWidget(QLabel("اسم الطالب / Student Name"), 2, 0)
        layout.addWidget(self.name_entry, 2, 1, 1, 2)

        self.record_button = QPushButton("🎙️ سجل الاسم / Record Name")
        self.record_button.clicked.connect(self.handle_recording)
        layout.addWidget(self.record_button, 2, 3)

        layout.addWidget(QLabel("التشخيص / Diagnosis"), 3, 0)
        layout.addWidget(self.diagnosis_dropdown, 3, 1, 1, 3)

        button_frame = QFrame()
        button_layout = QGridLayout(button_frame)
        save_button = QPushButton("💾 حفظ السجل / Save Record")
        save_button.clicked.connect(self.save_current_record)
        button_layout.addWidget(save_button, 0, 0)

        self.print_button = QPushButton("🖨️ طباعة نموذج / Generate PDF")
        self.print_button.clicked.connect(self.generate_print_file)
        if not HAS_REPORTLAB:
            self.print_button.setEnabled(False)
            self.print_button.setText("🖨️ تثبيت reportlab مطلوب")
        button_layout.addWidget(self.print_button, 0, 1)

        self.view_button = QPushButton("👁️ عرض السجلات / View Records")
        self.view_button.clicked.connect(self.open_view_records_window)
        button_layout.addWidget(self.view_button, 0, 2)

        layout.addWidget(button_frame, 4, 0, 1, 4)

    def handle_recording(self):
        self.record_button.setEnabled(False)
        self.record_button.setText("...يرجى الانتظار / Calibrating...")
        QApplication.processEvents()

        name = transcribe_arabic_name()

        if name:
            self.name_entry.setText(name)
        else:
            self.name_entry.setText("— لم يتم التعرف / Not Recognized —")

        self.record_button.setEnabled(True)
        self.record_button.setText("🎙️ سجل الاسم / Record Name")

    def save_current_record(self):
        student_name = self.name_entry.text().strip()
        if not student_name or student_name.startswith("—"):
            QMessageBox.critical(self, "خطأ / Error", "يرجى تسجيل اسم الطالب أولاً.")
            return

        new_record = {
            "Record_ID": get_next_record_id(),
            "Date": datetime.date.today().strftime("%Y-%m-%d"),
            "School_Name": self.school_entry.text().strip(),
            "Student_Name": student_name,
            "Class_No": self.class_entry.text().strip(),
            "Department": self.department_entry.text().strip(),
            "Diagnosis": self.diagnosis_dropdown.currentText(),
        }

        save_new_record(new_record)
        QMessageBox.information(self, "نجاح / Success", "تم حفظ السجل بنجاح!")
        self.name_entry.clear()
        self.diagnosis_dropdown.setCurrentIndex(0)
        self.name_entry.setFocus()

    def open_view_records_window(self):
        from src.ui.view_records_window import ViewRecordsWindow
        self.view_window = ViewRecordsWindow()
        self.view_window.show()

    def generate_print_file(self):
        if not HAS_REPORTLAB:
            QMessageBox.critical(
                self,
                "خطأ / Error",
                "يجب تثبيت reportlab لإنشاء ملفات PDF. يرجى تشغيل: pip install reportlab",
            )
            return

        records = load_all_records()
        if not records:
            QMessageBox.warning(
                self, "تحذير / Warning", "لا توجد سجلات لحفظها في ملف PDF."
            )
            return

        try:
            generate_combined_pdf(records, PDF_FILENAME)
            QMessageBox.information(self, "نجاح / Success", f"تم إنشاء ملخص السجلات بنجاح في: {PDF_FILENAME}")
        except Exception as e:
            QMessageBox.critical(self, "خطأ / Error", f"حدث خطأ أثناء إنشاء ملف PDF: {e}")
