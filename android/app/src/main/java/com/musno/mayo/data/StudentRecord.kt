package com.musno.mayo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "student_records")
data class StudentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordId: Int,
    val date: String,
    val schoolName: String,
    val studentName: String,
    val classNo: String,
    val department: String,
    val diagnosis: String,
) {
    companion object {
        fun empty(): StudentRecord = StudentRecord(
            recordId = 0,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            schoolName = "",
            studentName = "",
            classNo = "",
            department = "",
            diagnosis = DIAGNOSES[0],
        )
    }
}