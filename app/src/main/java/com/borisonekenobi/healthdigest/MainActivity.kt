package com.borisonekenobi.healthdigest

import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthDigestApp()
        }
    }
}

data class WeeklyReport(
    val calories: Int,
    val protein: Int,
    val steps: Int,
    val weight: Double,
    val notes: String,
)

fun WeeklyReport.toDisplayString(): String {
    return """
WEEKLY REPORT

Calories: $calories
Protein: $protein
Steps: $steps
Weight: $weight

Notes:
$notes
""".trimIndent()
}

@Composable
fun HealthDigestApp() {
    var report by remember { mutableStateOf<WeeklyReport?>(null) }
    var notes by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

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
            text = report?.toDisplayString() ?: "Press Generate Weekly Report", modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            report = generateFakeReport(notes)
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

fun generateFakeReport(notes: String): WeeklyReport {
    return WeeklyReport(
        calories = 2100,
        protein = 160,
        steps = 82000,
        weight = 100.6,
        notes = notes.ifBlank { "No notes provided." })
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    HealthDigestApp()
}
