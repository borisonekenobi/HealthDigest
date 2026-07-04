package com.borisonekenobi.healthdigest.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.borisonekenobi.healthdigest.model.Range
import com.borisonekenobi.healthdigest.ui.HealthDigestApp

@Composable
fun GoalNumberField(
    range: Range<String>,
    goalText: String,
    units: String,
    onLowerBoundChange: (String) -> Unit,
    onUpperBoundChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = range.lowerBound ?: "",
            onValueChange = onLowerBoundChange,
            modifier = Modifier.weight(1f),
            label = { Text("Min $goalText") },
            suffix = { Text(units) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp,
                bottomStart = 8.dp,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        OutlinedTextField(
            value = range.upperBound ?: "",
            onValueChange = onUpperBoundChange,
            modifier = Modifier.weight(1f),
            label = { Text("Max $goalText") },
            suffix = { Text(units) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 0.dp,
            ),
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
fun SettingsScreenPreview() {
    HealthDigestApp("settings")
}
