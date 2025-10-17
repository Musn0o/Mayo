from reportlab.lib.pagesizes import A4, landscape
from reportlab.platypus import (
    SimpleDocTemplate,
    Table,
    TableStyle,
    Paragraph,
    Spacer,
    Image,
    PageBreak,
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.enums import TA_CENTER, TA_RIGHT, TA_LEFT
from reportlab.lib import colors
import arabic_reshaper
from bidi.algorithm import get_display
import os
import sys

# --- Arabic Font Setup ---
try:
    font_path = "/usr/share/fonts/truetype/noto/NotoNaskhArabic-Regular.ttf"
    pdfmetrics.registerFont(TTFont("Arabic-Font", font_path))
    ARABIC_FONT_NAME = "Arabic-Font"
except:  # noqa: E722
    print("Warning: Could not register the Arabic font. Falling back to Helvetica.")
    ARABIC_FONT_NAME = "Helvetica"

def resource_path(relative_path):
    """ Get absolute path to resource, works for dev and for PyInstaller """
    try:
        # PyInstaller creates a temp folder and stores path in _MEIPASS
        base_path = sys._MEIPASS
    except Exception:
        base_path = os.path.abspath(".")

    return os.path.join(base_path, relative_path)

def _create_card_elements(record):
    elements = []

    configuration = {
        "delete_harakat": True,
        "support_ligatures": True,
        "language": "ar",
    }
    reshaper = arabic_reshaper.ArabicReshaper(configuration=configuration)

    def rtl_text(text):
        reshaped_text = reshaper.reshape(text)
        bidi_text = get_display(reshaped_text)
        if isinstance(bidi_text, memoryview):
            return bytes(bidi_text).decode('utf-8')
        elif isinstance(bidi_text, (bytes, bytearray)):
            return bidi_text.decode('utf-8')
        return bidi_text

    # Styles
    styles = getSampleStyleSheet()
    right_style = ParagraphStyle(
        "Right",
        parent=styles["Normal"],
        fontName=ARABIC_FONT_NAME,
        alignment=TA_RIGHT,
        fontSize=10,
        leading=12,
    )
    header_style = ParagraphStyle(
        "Header",
        parent=styles["Normal"],
        fontName=ARABIC_FONT_NAME,
        alignment=TA_RIGHT,
        fontSize=12,
        leading=14,
    )

    # 1. Header
    logo_path = resource_path("logo.png")
    try:
        logo = Image(logo_path, width=0.5 * inch, height=0.5 * inch)
    except Exception:
        logo = Paragraph(rtl_text("[LOGO]"), right_style)

    header_text = rtl_text("بطاقة ولي امر الطالب")
    header_paragraph = Paragraph(header_text, header_style)

    header_table = Table(
        [[logo, header_paragraph]],
        colWidths=[0.6 * inch, None],
        style=[
            ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
            ("ALIGN", (0, 0), (0, 0), "LEFT"),
            ("ALIGN", (1, 0), (1, 0), "RIGHT"),
        ],
    )
    elements.append(header_table)
    elements.append(Spacer(1, 0.2 * inch))

    # 2. Info
    line_spacing = 0.1 * inch
    elements.append(Paragraph(rtl_text("المركز الصحي: الشهيد الدكتور سيف زكي"), right_style))
    elements.append(Spacer(1, line_spacing))
    elements.append(Paragraph(rtl_text(f"اسم المدرسة: {record.get('School_Name', '')}"), right_style))
    elements.append(Spacer(1, line_spacing))
    elements.append(Paragraph(rtl_text(f"اسم الطالب: {record.get('Student_Name', '')}"), right_style))
    elements.append(Spacer(1, line_spacing))
    elements.append(Paragraph(rtl_text(f"الفصل: {record.get('Class_No', '')}"), right_style))
    elements.append(Spacer(1, line_spacing))
    elements.append(Paragraph(rtl_text(f"الشعبة: {record.get('Department', '')}"), right_style))
    elements.append(Spacer(1, 0.25 * inch))

    # 3. Diagnosis intro
    elements.append(Paragraph(rtl_text("بعد الفحص تبين وجود الاحتياجات التالية للعلاج :"), right_style))
    elements.append(Spacer(1, 0.1 * inch))

    # 4. Treatment options
    treatment_options = ["قلع سن", "حشوات أسنان", "تطبيق فلورايد", "تنظيف أسنان", "أخرى"]
    record_diagnosis = record.get("Diagnosis", "")

    for option in treatment_options:
        if option in record_diagnosis:
            line = f"<b>✓ {rtl_text(option)}</b>"
            elements.append(Paragraph(line, right_style))
        else:
            line = f"&nbsp;&nbsp; {rtl_text(option)}"
            elements.append(Paragraph(line, right_style))

    elements.append(Spacer(1, 0.25 * inch))

    # 5. Important Note
    note_heading = rtl_text("ملاحظة مهمة:")
    elements.append(Paragraph(f"<b><u>{note_heading}</u></b>", right_style))
    elements.append(Spacer(1, 0.05 * inch))
    note_part1 = rtl_text("الرجاء مراجعة المركز الصحي بأسرع وقت لتلافي عدم معالجة الأسنان ،")
    note_part2 = rtl_text("علما إن الفحص والعلاج مجاني طيلة أيام السنة")
    elements.append(Paragraph(note_part1, right_style))
    elements.append(Paragraph(note_part2, right_style))

    elements.append(Spacer(1, 0.25 * inch))

    # 6. Footer
    school_stamp_line1 = rtl_text("ختم إدارة")
    school_stamp_line2 = rtl_text("المدرسة")
    health_center_stamp_line1 = rtl_text("ختم إدارة")
    health_center_stamp_line2 = rtl_text("المركز الصحي")

    school_stamp_elements = [
        Paragraph(school_stamp_line1, right_style),
        Paragraph(school_stamp_line2, right_style),
    ]

    footer_left_style = ParagraphStyle('FooterLeft', parent=right_style, alignment=TA_LEFT)
    health_center_stamp_elements = [
        Paragraph(health_center_stamp_line1, footer_left_style),
        Paragraph(health_center_stamp_line2, footer_left_style),
    ]

    footer_table = Table(
        [[
            health_center_stamp_elements,
            school_stamp_elements
        ]],
        colWidths=[1.3 * inch, 1.3 * inch]
    )
    footer_table.setStyle(TableStyle([('VALIGN', (0, 0), (-1, -1), 'TOP')]))
    elements.append(footer_table)

    return elements

def generate_notification_card(record, filename):
    doc = SimpleDocTemplate(filename, pagesize=A4)
    elements = _create_card_elements(record)
    doc.build(elements)

def generate_combined_pdf(records, filename):
    doc = SimpleDocTemplate(filename, pagesize=landscape(A4))

    def chunks(lst, n):
        for i in range(0, len(lst), n):
            yield lst[i:i + n]

    record_chunks = list(chunks(records, 3))
    
    all_tables = []
    for chunk in record_chunks:
        row_data = []
        for record in chunk:
            card_elements = _create_card_elements(record)
            row_data.append(card_elements)
        
        while len(row_data) < 3:
            row_data.append([])

        table = Table([row_data], colWidths=[2.7*inch, 2.7*inch, 2.7*inch])
        table.setStyle(TableStyle([
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('LINEAFTER', (0, 0), (-2, -1), 1, colors.grey),
        ]))
        all_tables.append(table)
        all_tables.append(Spacer(1, 0.2*inch))

    doc.build(all_tables)
