package com.borisonekenobi.healthdigest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.borisonekenobi.healthdigest.data.DataStoreSource
import com.borisonekenobi.healthdigest.data.PreferenceKeys
import com.borisonekenobi.healthdigest.model.settings.ReportPreferences
import kotlinx.coroutines.launch

@Composable
fun ReportPreferencesScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStoreSource = remember { DataStoreSource(context) }
    val reportPreferencesNullable by dataStoreSource.reportPreferencesFlow.collectAsState(initial = null)
    val reportPreferences = reportPreferencesNullable ?: ReportPreferences("", "")

    var startMessage by remember { mutableStateOf("") }
    var endMessage by remember { mutableStateOf("") }

    LaunchedEffect(reportPreferencesNullable, reportPreferences) {
        val prefs = reportPreferencesNullable
        if (prefs != null) {
            startMessage = prefs.startMessage
            endMessage = prefs.endMessage
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Messages")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = startMessage,
            onValueChange = {
                startMessage = it
                scope.launch {
                    dataStoreSource.saveUserPreference(PreferenceKeys.START_MESSAGE, it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Start Message (optional)") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = endMessage,
            onValueChange = {
                endMessage = it
                scope.launch {
                    dataStoreSource.saveUserPreference(PreferenceKeys.END_MESSAGE, it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("End Message (optional)") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportPreferencesPreview() {
    HealthDigestApp("report-preferences")
}
