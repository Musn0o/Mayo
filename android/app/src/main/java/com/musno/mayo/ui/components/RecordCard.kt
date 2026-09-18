package com.musno.mayo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musno.mayo.data.StudentRecord

@Composable
fun RecordCard(
    record: StudentRecord,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${record.recordId} — ${record.date}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // removed: diagnosis chip (per dentist preference, diagnoses are marked manually on paper)
                /*
                AssistChip(
                    onClick = {},
                    label = { Text(record.diagnosis) },
                )
                */
            }
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = record.studentName,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "المدرسة: ${record.schoolName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "الفصل: ${record.classNo}  |  الشعبة: ${record.department}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onShare) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("بطاقة PDF", style = MaterialTheme.typography.labelLarge)
                }
                TextButton(onClick = onEdit) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("تعديل", style = MaterialTheme.typography.labelLarge)
                }
                TextButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "حذف",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecordDialog(
    record: StudentRecord,
    onDismiss: () -> Unit,
    onSave: (StudentRecord) -> Unit,
) {
    var schoolName by remember { mutableStateOf(record.schoolName) }
    var studentName by remember { mutableStateOf(record.studentName) }
    var classNo by remember { mutableStateOf(record.classNo) }
    var department by remember { mutableStateOf(record.department) }
    // removed: diagnosis selection box (per dentist preference)
    // diagnoses are printed unmarked in the PDF and marked manually after printing.
    var diagnosis = record.diagnosis
    // removed: diagnosisExpanded state (was used only by the hidden dropdown below)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل السجل") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("اسم المدرسة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("اسم الطالب") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classNo,
                        onValueChange = { classNo = it },
                        label = { Text("الفصل") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("الشعبة") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                /* removed diagnosis dropdown
                ExposedDropdownMenuBox(
                    expanded = diagnosisExpanded,
                    onExpandedChange = { diagnosisExpanded = !diagnosisExpanded },
                ) {
                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("التشخيص") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = diagnosisExpanded)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = diagnosisExpanded,
                        onDismissRequest = { diagnosisExpanded = false },
                    ) {
                        DIAGNOSES.forEach { diag ->
                            DropdownMenuItem(
                                text = { Text(diag) },
                                onClick = {
                                    diagnosis = diag
                                    diagnosisExpanded = false
                                },
                            )
                        }
                    }
                }
                */
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        record.copy(
                            schoolName = schoolName.trim(),
                            studentName = studentName.trim(),
                            classNo = classNo.trim(),
                            department = department.trim(),
                            diagnosis = diagnosis,
                        )
                    )
                },
                enabled = studentName.isNotBlank(),
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
    )
}