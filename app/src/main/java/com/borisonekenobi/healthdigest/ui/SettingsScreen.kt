package com.borisonekenobi.healthdigest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text("Personal Information") },
            modifier = Modifier.clickable { navController.navigate("personal-information") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person, contentDescription = "Personal Information"
                )
            })

        ListItem(
            headlineContent = { Text("Goal Preferences") },
            modifier = Modifier.clickable { navController.navigate("goal-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Flag, contentDescription = "Goal Preferences"
                )
            })

        ListItem(
            headlineContent = { Text("Report Preferences") },
            modifier = Modifier.clickable { navController.navigate("report-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Analytics, contentDescription = "Report Preferences"
                )
            })

        ListItem(
            headlineContent = { Text("System Preferences") },
            modifier = Modifier.clickable { navController.navigate("system-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Settings, contentDescription = "System Preferences"
                )
            })

        ListItem(
            headlineContent = { Text("Health Connect") },
            modifier = Modifier.clickable { navController.navigate("health-connect") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Sync, contentDescription = "Health Connect"
                )
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    HealthDigestApp("settings")
}
