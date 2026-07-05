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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.borisonekenobi.healthdigest.R

@Composable
fun SettingsScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.personal_information)) },
            modifier = Modifier.clickable { navController.navigate("personal-information") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.personal_information)
                )
            })

        ListItem(
            headlineContent = { Text(stringResource(R.string.goal_preferences)) },
            modifier = Modifier.clickable { navController.navigate("goal-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = stringResource(R.string.goal_preferences)
                )
            })

        ListItem(
            headlineContent = { Text(stringResource(R.string.report_preferences)) },
            modifier = Modifier.clickable { navController.navigate("report-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = stringResource(R.string.report_preferences)
                )
            })

        ListItem(
            headlineContent = { Text(stringResource(R.string.system_preferences)) },
            modifier = Modifier.clickable { navController.navigate("system-preferences") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.system_preferences)
                )
            })

        ListItem(
            headlineContent = { Text(stringResource(R.string.health_connect)) },
            modifier = Modifier.clickable { navController.navigate("health-connect") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = stringResource(R.string.health_connect)
                )
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    HealthDigestApp("settings")
}
