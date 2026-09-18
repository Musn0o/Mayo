package com.musno.mayo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musno.mayo.ui.MainViewModel
import com.musno.mayo.ui.navigation.MayoAppNavHost
import com.musno.mayo.ui.theme.MayoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MayoTheme {
                val context = LocalContext.current
                val app = context.applicationContext as MayoApplication
                val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(app))

                MayoAppNavHost(
                    viewModel = viewModel,
                    context = this,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}