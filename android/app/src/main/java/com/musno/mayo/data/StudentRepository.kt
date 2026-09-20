package com.musno.mayo.data

import android.content.Context
import android.net.Uri
import com.musno.mayo.util.CsvManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).studentDao()

    val allRecords: Flow<List<StudentRecord>> = dao.getAllRecords()

    suspend fun getNextRecordId(): Int {
        val maxId = dao.getMaxRecordId() ?: 0
        return maxId + 1
    }

    suspend fun save(record: StudentRecord) {
        dao.insert(record)
    }

    suspend fun update(record: StudentRecord) {
        dao.update(record)
    }

    suspend fun delete(record: StudentRecord) {
        dao.delete(record)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }

    suspend fun getById(id: Long): StudentRecord? {
        return dao.getById(id)
    }

    suspend fun createNewRecord(
        schoolName: String,
        studentName: String,
        classNo: String,
        department: String,
        diagnosis: String,
    ): StudentRecord {
        val nextId = getNextRecordId()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val record = StudentRecord(
            recordId = nextId,
            date = date,
            schoolName = schoolName,
            studentName = studentName,
            classNo = classNo,
            department = department,
            diagnosis = diagnosis,
        )
        dao.insert(record)
        return record
    }

    suspend fun exportToCsv(context: Context, uri: Uri): Boolean {
        val records = dao.getAllRecordsList()
        return CsvManager.exportToCsv(context, uri, records)
    }

    suspend fun importFromCsv(context: Context, uri: Uri): Result<Int> {
        val records = CsvManager.importFromCsv(context, uri)
        if (records.isEmpty()) {
            return Result.failure(Exception("لم يتم العثور على سجلات في الملف"))
        }
        dao.insertAll(records)
        return Result.success(records.size)
    }

    suspend fun exportToCsvFile(context: Context): java.io.File? {
        val records = dao.getAllRecordsList()
        return CsvManager.exportToCacheCsv(context, records)
    }
}