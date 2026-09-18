package com.musno.mayo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.musno.mayo.ui.HomeFormState
import com.musno.mayo.util.SpeechHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    formState: HomeFormState,
    isRecording: Boolean,
    onStudentNameChange: (String) -> Unit,
    onSchoolNameChange: (String) -> Unit,
    onClassNameChange: (String) -> Unit,
    onDepartmentChange: (String) -> Unit,
    onDiagnosisChange: (String) -> Unit,
    onSave: () -> Unit,
    onStartRecording: () -> Unit,
    onRecordingFinished: () -> Unit,
    onViewRecords: () -> Unit,
) {
    val context = LocalContext.current
    var listeningState by remember { mutableStateOf(false) }
    var recordingError by remember { mutableStateOf<String?>(null) }
    var hasSetReady by remember { mutableStateOf(false) }

    fun startListening() {
        if (listeningState) return
        listeningState = true
        recordingError = null
        hasSetReady = false
        onStartRecording()
        SpeechHelper.startListening(
            context = context,
            onResult = { text ->
                if (text.isNotEmpty()) {
                    onStudentNameChange(text)
                }
                listeningState = false
                onRecordingFinished()
            },
            onError = { msg ->
                recordingError = msg
                listeningState = false
                onRecordingFinished()
            },
            onPartialResult = { partial ->
                if (partial.isNotEmpty()) {
                    onStudentNameChange(partial)
                    if (!hasSetReady) {
                        hasSetReady = true
                        onStartRecording()
                    }
                }
            },
            onReady = {
                hasSetReady = true
                onStartRecording()
            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startListening()
        } else {
            recordingError = "تحتاج إلى إذن الميكروفون للتسجيل الصوتي"
        }
    }

    fun onMicClick() {
        val permission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val isListening = listeningState || isRecording

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("تسجيل بيانات الطلاب", color = Color.White)
                        Text(
                            "Dental Student Record",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                value = formState.schoolName,
                onValueChange = onSchoolNameChange,
                label = { Text("اسم المدرسة") },
                placeholder = { Text("School Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = formState.className,
                    onValueChange = onClassNameChange,
                    label = { Text("الفصل") },
                    placeholder = { Text("Class No") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = formState.department,
                    onValueChange = onDepartmentChange,
                    label = { Text("الشعبة") },
                    placeholder = { Text("Department") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            OutlinedTextField(
                value = formState.studentName,
                onValueChange = onStudentNameChange,
                label = { Text("اسم الطالب") },
                placeholder = { Text("اضغط على الميكروفون للتسجيل الصوتي") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { onMicClick() }, enabled = !isListening) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "تسجيل الصوت",
                            tint = if (isListening) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                singleLine = true,
            )

            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text("جاري تسجيل الاسم الصوتي ... قل الاسم الآن")
                    }
                }
            }

            AnimatedVisibility(
                visible = recordingError != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = recordingError ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            // removed: diagnosis selection box (per dentist preference)
            // diagnoses are printed unmarked in the PDF and marked manually after printing.
            /*
            var diagnosisExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = diagnosisExpanded,
                onExpandedChange = { diagnosisExpanded = !diagnosisExpanded },
            ) {
                OutlinedTextField(
                    value = formState.diagnosis,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("التشخيص") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = diagnosisExpanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                )
                ExposedDropdownMenu(
                    expanded = diagnosisExpanded,
                    onDismissRequest = { diagnosisExpanded = false },
                ) {
                    DIAGNOSES.forEach { diag ->
                        DropdownMenuItem(
                            text = { Text(diag) },
                            onClick = {
                                onDiagnosisChange(diag)
                                diagnosisExpanded = false
                            },
                        )
                    }
                }
            }
            */

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.studentName.isNotBlank(),
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text("حفظ السجل")
            }

            Button(
                onClick = onViewRecords,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text("عرض السجلات")
            }
        }
    }
}