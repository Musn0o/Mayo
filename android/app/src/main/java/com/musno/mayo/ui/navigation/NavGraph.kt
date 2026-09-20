package com.musno.mayo.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musno.mayo.ui.MainViewModel
import com.musno.mayo.ui.screens.HomeScreen
import com.musno.mayo.ui.screens.RecordsScreen
import com.musno.mayo.util.PrintHelper

object Routes {
    const val HOME = "home"
    const val RECORDS = "records"
}

@Composable
fun MayoAppNavHost(
    viewModel: MainViewModel,
    context: Context,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val records by viewModel.records.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importCsv(it) }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
            viewModel.onToastShown()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                formState = formState,
                isRecording = isRecording,
                onStudentNameChange = viewModel::updateStudentName,
                onSchoolNameChange = viewModel::updateSchoolName,
                onClassNameChange = viewModel::updateClassName,
                onDepartmentChange = viewModel::updateDepartment,
                onDiagnosisChange = viewModel::updateDiagnosis,
                onSave = viewModel::saveCurrentRecord,
                onStartRecording = { viewModel.setRecording(true) },
                onRecordingFinished = { viewModel.setRecording(false) },
                onViewRecords = { navController.navigate(Routes.RECORDS) },
            )
        }
        composable(Routes.RECORDS) {
            RecordsScreen(
                records = records,
                onBack = { navController.navigateUp() },
                onDelete = viewModel::deleteRecord,
                onUpdate = viewModel::updateRecord,
                onShareCard = viewModel::generateSingleCard,
                onPickCsv = { importLauncher.launch(arrayOf("*/*")) },
                onGenerateSummary = viewModel::generateCombinedPdf,
                onPrint = { PrintHelper.printRecords(context, records) },
                onClearAll = viewModel::clearAllRecords,
            )
        }
    }
}