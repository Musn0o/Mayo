package com.musno.mayo.util

import android.content.Context
import android.net.Uri
import com.musno.mayo.data.CSV_HEADER
import com.musno.mayo.data.StudentRecord
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object CsvManager {

    private fun String.csvEscape(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"${replace("\"", "\"\"")}\""
        } else {
            this
        }
    }

    fun exportToCsv(context: Context, uri: Uri, records: List<StudentRecord>): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream, Charsets.UTF_8)
                writer.write("\uFEFF")
                writer.write(CSV_HEADER)
                writer.write("\r\n")
                for (record in records) {
                    val line = listOf(
                        record.recordId.toString(),
                        record.date,
                        record.schoolName.csvEscape(),
                        record.studentName.csvEscape(),
                        record.classNo.csvEscape(),
                        record.department.csvEscape(),
                        record.diagnosis.csvEscape(),
                    ).joinToString(",")
                    writer.write(line)
                    writer.write("\r\n")
                }
                writer.flush()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun exportToCacheCsv(context: Context, records: List<StudentRecord>): File? {
        return try {
            val dir = File(context.cacheDir, "shared")
            dir.mkdirs()
            val file = File(dir, "student_records.csv")
            file.outputStream().bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write("\uFEFF")
                writer.write(CSV_HEADER)
                writer.write("\r\n")
                for (record in records) {
                    val line = listOf(
                        record.recordId.toString(),
                        record.date,
                        record.schoolName.csvEscape(),
                        record.studentName.csvEscape(),
                        record.classNo.csvEscape(),
                        record.department.csvEscape(),
                        record.diagnosis.csvEscape(),
                    ).joinToString(",")
                    writer.write(line)
                    writer.write("\r\n")
                }
            }
            file
        } catch (_: Exception) {
            null
        }
    }

    fun importFromCsv(context: Context, uri: Uri): List<StudentRecord> {
        return try {
            val records = mutableListOf<StudentRecord>()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                var firstLine = reader.readLine() ?: return@use
                if (firstLine.startsWith("\uFEFF")) {
                    firstLine = firstLine.removePrefix("\uFEFF")
                }
                val header = firstLine.split(",").map { it.trim() }
                val idIdx = header.indexOf("Record_ID")
                val dateIdx = header.indexOf("Date")
                val schoolIdx = header.indexOf("School_Name")
                val nameIdx = header.indexOf("Student_Name")
                val classIdx = header.indexOf("Class_No")
                val deptIdx = header.indexOf("Department")
                val diagIdx = header.indexOf("Diagnosis")

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val cols = parseCsvLine(line!!)
                    if (cols.size < 7) continue
                    val recordId = cols.getOrNull(idIdx)?.trim()?.toIntOrNull() ?: continue
                    records.add(
                        StudentRecord(
                            recordId = recordId,
                            date = cols.getOrNull(dateIdx)?.trim() ?: "",
                            schoolName = cols.getOrNull(schoolIdx)?.trim() ?: "",
                            studentName = cols.getOrNull(nameIdx)?.trim() ?: "",
                            classNo = cols.getOrNull(classIdx)?.trim() ?: "",
                            department = cols.getOrNull(deptIdx)?.trim() ?: "",
                            diagnosis = cols.getOrNull(diagIdx)?.trim() ?: "",
                        )
                    )
                }
            }
            records
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(c)
            }
            i++
        }
        result.add(current.toString())
        return result
    }
}