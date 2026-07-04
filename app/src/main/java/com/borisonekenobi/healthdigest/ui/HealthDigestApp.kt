package com.borisonekenobi.healthdigest.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.borisonekenobi.healthdigest.data.DataStoreSource
import com.borisonekenobi.healthdigest.model.settings.SystemPreferences
import com.borisonekenobi.healthdigest.model.settings.Theme
import com.borisonekenobi.healthdigest.model.settings.Units
import com.borisonekenobi.healthdigest.ui.theme.HealthDigestTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDigestApp(startDestination: String = "main") {
    val context = LocalContext.current
    val dataStoreSource = remember { DataStoreSource(context) }
    val systemPreferences by dataStoreSource.systemPreferencesFlow.collectAsState(
        initial = SystemPreferences(
            Theme.SYSTEM,
            Units.METRIC,
        )
    )

    val darkTheme = when (systemPreferences.theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.SYSTEM -> isSystemInDarkTheme()
    }

    HealthDigestTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable("main") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Health Digest") }, actions = {
                                IconButton(onClick = { navController.navigate("settings") }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            })
                        }) { innerPadding ->
                        MainScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("settings") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Settings") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        SettingsScreen(navController = navController, modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("personal-information") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Personal Information") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        PersonalInformationScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("goal-preferences") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Goal Preferences") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        GoalPreferencesScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("report-preferences") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Report Preferences") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        ReportPreferencesScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("system-preferences") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("System Preferences") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        SystemPreferencesScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
                composable("health-connect") {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text("Health Connect") }, navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                        }) { innerPadding ->
                        HealthConnectScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HealthDigestAppPreview() {
    HealthDigestApp()
}
