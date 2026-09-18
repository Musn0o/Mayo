package com.musno.mayo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<StudentRecord>>

    @Query("SELECT * FROM student_records ORDER BY id DESC")
    suspend fun getAllRecordsList(): List<StudentRecord>

    @Query("SELECT * FROM student_records WHERE id = :id")
    suspend fun getById(id: Long): StudentRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: StudentRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<StudentRecord>)

    @Update
    suspend fun update(record: StudentRecord)

    @Delete
    suspend fun delete(record: StudentRecord)

    @Query("DELETE FROM student_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT MAX(recordId) FROM student_records")
    suspend fun getMaxRecordId(): Int?

    @Query("DELETE FROM student_records")
    suspend fun deleteAll()
}