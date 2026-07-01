package com.borisonekenobi.healthdigest.ui

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.borisonekenobi.healthdigest.data.FakeHealthRepository
import com.borisonekenobi.healthdigest.model.WeeklyReport
import com.borisonekenobi.healthdigest.model.toDisplayString
import kotlinx.coroutines.launch

@Composable
fun HealthDigestApp() {
    var report by remember { mutableStateOf<WeeklyReport?>(null) }
    var notes by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val repository = remember { FakeHealthRepository() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Weekly notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = report?.toDisplayString() ?: "Press Generate Weekly Report",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                report = repository.getWeeklyReport(notes)
            }
        }) {
            Text("Generate Weekly Report")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val clipData = ClipData.newPlainText("Health Report", report?.toDisplayString())
            scope.launch {
                clipboardManager.setClipEntry(ClipEntry(clipData))
            }
        }, enabled = report?.toDisplayString()?.isNotBlank() ?: false) {
            Text("Copy to Clipboard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    HealthDigestApp()
}
