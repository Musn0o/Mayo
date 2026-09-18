package com.musno.mayo.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.musno.mayo.data.StudentRecord
import java.io.FileOutputStream

object PrintHelper {

    fun printRecords(context: Context, records: List<StudentRecord>) {
        if (records.isEmpty()) return

        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        val attributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "PDF", 300, 300))
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        val adapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal,
                callback: LayoutResultCallback,
                extras: Bundle?,
            ) {
                if (cancellationSignal.isCanceled) {
                    callback.onLayoutCancelled()
                    return
                }
                val pages = (records.size + 2) / 3
                val info = PrintDocumentInfo.Builder("dental_records_summary")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pages)
                    .build()
                callback.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal,
                callback: WriteResultCallback,
            ) {
                try {
                    val doc = PdfDocument()
                    val pageWidth = PdfGenerator.A4_HEIGHT.toInt()
                    val pageHeight = PdfGenerator.A4_WIDTH.toInt()

                    for (i in records.indices step 3) {
                        if (cancellationSignal.isCanceled) {
                            callback.onWriteCancelled()
                            return
                        }
                        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, (i / 3) + 1).create()
                        val page = doc.startPage(pageInfo)
                        PdfGenerator.drawCardsToCanvas(
                            page.canvas,
                            records.subList(i, minOf(i + 3, records.size)),
                            pageWidth.toFloat(),
                            pageHeight.toFloat(),
                            3,
                        )
                        doc.finishPage(page)
                    }

                    FileOutputStream(destination.fileDescriptor).use { doc.writeTo(it) }
                    doc.close()
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback.onWriteFailed(e.message)
                }
            }
        }

        printManager.print("سجلات الطلاب", adapter, attributes)
    }
}