package com.musno.mayo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.musno.mayo.R
import com.musno.mayo.data.DIAGNOSES
import com.musno.mayo.data.StudentRecord
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    const val A4_WIDTH = 595f
    const val A4_HEIGHT = 842f
    private const val MARGIN = 36f

    private var arabicTypeface: Typeface? = null
    private var logoBitmap: Bitmap? = null

    fun init(context: Context) {
        arabicTypeface = ResourcesCompat.getFont(context, R.font.noto_naskh_arabic_regular)
        logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ministry_of_health_iraq_logo)
    }

    private fun textPaint(size: Float = 14f): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            color = Color.BLACK
            typeface = arabicTypeface ?: Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }
    }

    private fun boldTextPaint(size: Float = 14f): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            color = Color.BLACK
            typeface = Typeface.create(
                arabicTypeface ?: Typeface.create(Typeface.SERIF, Typeface.NORMAL),
                Typeface.BOLD
            )
        }
    }

    private fun underlineTextPaint(size: Float = 12f): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            color = Color.BLACK
            typeface = Typeface.create(
                arabicTypeface ?: Typeface.create(Typeface.SERIF, Typeface.NORMAL),
                Typeface.BOLD
            )
            isUnderlineText = true
        }
    }

    private fun drawRtlText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        width: Int,
        paint: TextPaint,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    ): Int {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setAlignment(alignment)
            .setLineSpacing(0f, 1f)
            .build()
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
        return layout.height
    }

    private fun drawLogo(canvas: Canvas, boxSize: Float) {
        val bmp = logoBitmap ?: return
        val scale = minOf(boxSize / bmp.width, boxSize / bmp.height)
        val w = bmp.width * scale
        val h = bmp.height * scale
        val left = MARGIN + (boxSize - w) / 2f
        val top = MARGIN + (boxSize - h) / 2f
        canvas.drawBitmap(
            bmp,
            null,
            RectF(left, top, left + w, top + h),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG),
        )
    }

    private fun drawCard(canvas: Canvas, record: StudentRecord, contentWidth: Int, pageHeight: Float) {
        // compact mode for the narrow summary columns, matching the official paper form
        val compact = contentWidth < 300
        val titleSize = if (compact) 13f else 16f
        val bodySize = if (compact) 10f else 12f
        val noteSize = if (compact) 9f else 11f
        val stampSize = if (compact) 9f else 10f
        val gapSmall = if (compact) 4f else 6f
        val gapMedium = if (compact) 6f else 10f
        val gapLarge = if (compact) 8f else 14f

        // ministry logo at the top-left corner of each card, like the official paper form
        drawLogo(canvas, if (compact) 26f else 46f)

        var y = MARGIN

        // centered title like the official form
        y += drawRtlText(
            canvas,
            "بطاقة ولي امر الطالب",
            MARGIN,
            y,
            contentWidth,
            boldTextPaint(titleSize),
            Layout.Alignment.ALIGN_CENTER,
        ) + gapMedium

        y += drawRtlText(
            canvas,
            "المركز الصحي: الشهيد الدكتور سيف زكي",
            MARGIN,
            y,
            contentWidth,
            boldTextPaint(bodySize),
        ) + gapSmall

        y += drawRtlText(
            canvas,
            "اسم المدرسة: ${record.schoolName}",
            MARGIN,
            y,
            contentWidth,
            textPaint(bodySize),
        ) + gapSmall

        y += drawRtlText(
            canvas,
            "اسم الطالب: ${record.studentName}",
            MARGIN,
            y,
            contentWidth,
            textPaint(bodySize),
        ) + gapSmall

        y += drawRtlText(
            canvas,
            "الفصل: ${record.classNo}",
            MARGIN,
            y,
            contentWidth,
            textPaint(bodySize),
        ) + gapSmall

        y += drawRtlText(
            canvas,
            "الشعبة: ${record.department}",
            MARGIN,
            y,
            contentWidth,
            textPaint(bodySize),
        ) + gapMedium

        y += drawRtlText(
            canvas,
            "بعد الفحص تبين وجود الاحتياجات التالية للعلاج :",
            MARGIN,
            y,
            contentWidth,
            textPaint(bodySize),
        ) + gapSmall

        // removed: auto-marking of the stored diagnosis.
        // all diagnoses are printed unmarked (official-form style) to be marked manually after printing.
        for (option in DIAGNOSES) {
            y += drawRtlText(canvas, "- $option", MARGIN, y, contentWidth, textPaint(bodySize))
        }
        y += gapMedium

        y += drawRtlText(
            canvas,
            "ملاحظة مهمة:",
            MARGIN,
            y,
            contentWidth,
            underlineTextPaint(bodySize),
        ) + gapSmall

        val notesParagraph =
            "الرجاء مراجعة المركز الصحي بأسرع وقت لتلافي عدم معالجة الأسنان ، علما إن الفحص والعلاج مجاني طيلة أيام السنة ."
        y += drawRtlText(canvas, notesParagraph, MARGIN, y, contentWidth, textPaint(noteSize)) + gapLarge

        // footer stamps pinned to the actual page bottom,
        // so nothing is cut off on the shorter landscape pages
        val drawBottom = pageHeight - MARGIN - 20f
        val stampEditableWidth = contentWidth + MARGIN
        val stampWidth = if (compact) (contentWidth / 2) else 130

        val stampPaint = textPaint(stampSize)
        drawRtlText(canvas, "ختم إدارة", MARGIN, drawBottom - 28f, stampWidth, stampPaint, Layout.Alignment.ALIGN_OPPOSITE)
        drawRtlText(canvas, "المركز الصحي", MARGIN, drawBottom - 12f, stampWidth, stampPaint, Layout.Alignment.ALIGN_OPPOSITE)

        val rightX = stampEditableWidth - stampWidth
        drawRtlText(canvas, "ختم إدارة", rightX, drawBottom - 28f, stampWidth, stampPaint, Layout.Alignment.ALIGN_NORMAL)
        drawRtlText(canvas, "المدرسة", rightX, drawBottom - 12f, stampWidth, stampPaint, Layout.Alignment.ALIGN_NORMAL)
    }

    fun drawCardsToCanvas(
        canvas: Canvas,
        records: List<StudentRecord>,
        pageWidth: Float,
        pageHeight: Float,
        maxPerPage: Int = 3,
    ) {
        val cards = records.take(maxPerPage)
        val cardWidth = (pageWidth - (cards.size + 1) * MARGIN) / cards.size
        cards.forEachIndexed { index, record ->
            canvas.save()
            canvas.translate(MARGIN + index * (cardWidth + MARGIN), 0f)
            canvas.clipRect(0f, 0f, cardWidth, pageHeight)
            drawCard(canvas, record, (cardWidth - 2 * MARGIN).toInt(), pageHeight)
            canvas.restore()
        }
        // vertical dividers between columns, like the official form
        if (cards.size > 1) {
            val dividerPaint = Paint().apply {
                color = Color.DKGRAY
                strokeWidth = 1f
            }
            for (k in 1 until cards.size) {
                val x = MARGIN + k * cardWidth + (k - 1) * MARGIN + MARGIN / 2f
                canvas.drawLine(x, MARGIN, x, pageHeight - MARGIN, dividerPaint)
            }
        }
    }

    fun generateSingleCardPdf(context: Context, record: StudentRecord): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH.toInt(), A4_HEIGHT.toInt(), 1).create()
        val page = doc.startPage(pageInfo)
        drawCard(page.canvas, record, (A4_WIDTH - 2 * MARGIN).toInt(), A4_HEIGHT)
        doc.finishPage(page)

        val dir = File(context.filesDir, "pdf")
        dir.mkdirs()
        val file = File(dir, "card_${record.recordId}_${record.studentName}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }

    fun generateCombinedPdf(context: Context, records: List<StudentRecord>): File? {
        if (records.isEmpty()) return null
        val doc = PdfDocument()
        val pageWidth = A4_HEIGHT.toInt()
        val pageHeight = A4_WIDTH.toInt()

        var pageNum = 1
        for (i in records.indices step 3) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            val page = doc.startPage(pageInfo)
            drawCardsToCanvas(page.canvas, records.subList(i, minOf(i + 3, records.size)), pageWidth.toFloat(), pageHeight.toFloat(), 3)
            doc.finishPage(page)
            pageNum++
        }

        val dir = File(context.filesDir, "pdf")
        dir.mkdirs()
        val file = File(dir, "dental_records_summary.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }
}