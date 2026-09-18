package com.musno.mayo.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.musno.mayo.MayoApplication
import com.musno.mayo.data.StudentRecord
import com.musno.mayo.data.StudentRepository
import com.musno.mayo.util.FileSharer
import com.musno.mayo.util.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeFormState(
    val schoolName: String = "أبو بكر الصديق",
    val className: String = "الأول",
    val department: String = "أ",
    val studentName: String = "",
    val diagnosis: String = "",
)

sealed class SaveResult {
    data object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = StudentRepository(app)

    private val _formState = MutableStateFlow(HomeFormState())
    val formState: StateFlow<HomeFormState> = _formState

    private val _records = MutableStateFlow<List<StudentRecord>>(emptyList())
    val records: StateFlow<List<StudentRecord>> = _records.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _lastCardFile = MutableStateFlow<java.io.File?>(null)
    val lastCardFile: StateFlow<java.io.File?> = _lastCardFile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allRecords.collect {
                _records.value = it
            }
        }
    }

    fun updateStudentName(name: String) {
        _formState.value = _formState.value.copy(studentName = name)
    }

    fun updateSchoolName(name: String) {
        _formState.value = _formState.value.copy(schoolName = name)
    }

    fun updateClassName(name: String) {
        _formState.value = _formState.value.copy(className = name)
    }

    fun updateDepartment(name: String) {
        _formState.value = _formState.value.copy(department = name)
    }

    fun updateDiagnosis(diag: String) {
        _formState.value = _formState.value.copy(diagnosis = diag)
    }

    fun clearForm() {
        _formState.value = HomeFormState(
            schoolName = _formState.value.schoolName,
            className = _formState.value.className,
            department = _formState.value.department,
        )
    }

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun onToastShown() {
        _toastMessage.value = null
    }

    fun saveCurrentRecord() {
        val state = _formState.value
        val name = state.studentName.trim()
        if (name.isEmpty()) {
            showToast("يرجى إدخال اسم الطالب")
            return
        }
        viewModelScope.launch {
            repository.createNewRecord(
                schoolName = state.schoolName.trim(),
                studentName = name,
                classNo = state.className.trim(),
                department = state.department.trim(),
                diagnosis = state.diagnosis,
            )
            clearForm()
            showToast("تم حفظ السجل بنجاح")
        }
    }

    fun updateRecord(record: StudentRecord) {
        viewModelScope.launch {
            repository.update(record)
            showToast("تم تحديث السجل")
        }
    }

    fun deleteRecord(record: StudentRecord) {
        viewModelScope.launch {
            repository.delete(record)
            showToast("تم حذف السجل")
        }
    }

    fun generateSingleCard(record: StudentRecord) {
        viewModelScope.launch {
            val file = PdfGenerator.generateSingleCardPdf(app, record)
            _lastCardFile.value = file
            FileSharer.sharePdf(app, file)
        }
    }

    fun generateCombinedPdf() {
        viewModelScope.launch {
            val file = PdfGenerator.generateCombinedPdf(app, _records.value)
            if (file != null) {
                _lastCardFile.value = file
                FileSharer.sharePdf(app, file)
            } else {
                showToast("لا توجد سجلات")
            }
        }
    }

    fun generateCombinedPdfForPrint(): java.io.File? {
        return PdfGenerator.generateCombinedPdf(app, _records.value)
    }

    fun exportCsv() {
        viewModelScope.launch {
            val file = repository.exportToCsvFile(app)
            if (file != null && file.exists() && file.length() > 0) {
                FileSharer.shareCsv(app, file)
            } else {
                showToast("لا توجد سجلات للتصدير")
            }
        }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            val result = repository.importFromCsv(app, uri)
            result.onSuccess { count ->
                showToast("تم استيراد $count سجل")
            }.onFailure { e ->
                showToast(e.message ?: "خطأ في الاستيراد")
            }
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(app) as T
        }
    }
}