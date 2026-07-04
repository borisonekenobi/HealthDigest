package com.borisonekenobi.healthdigest

import android.content.ClipData
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.borisonekenobi.healthdigest.data.HealthConnectRepository
import com.borisonekenobi.healthdigest.data.HealthRepository
import com.borisonekenobi.healthdigest.model.EnergyLevel
import com.borisonekenobi.healthdigest.model.HungerLevel
import com.borisonekenobi.healthdigest.model.UserData
import com.borisonekenobi.healthdigest.model.WaistFit
import com.borisonekenobi.healthdigest.model.WeeklyReport
import com.borisonekenobi.healthdigest.ui.HealthDigestApp
import kotlinx.coroutines.launch

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    val repository: HealthRepository = remember { HealthConnectRepository(context) }

    var report by remember { mutableStateOf<WeeklyReport?>(null) }

    var selectedHungerLevel by remember { mutableStateOf(HungerLevel.OK) }
    var hungerLevelComments by remember { mutableStateOf("") }
    var selectedEnergyLevel by remember { mutableStateOf(EnergyLevel.OK) }
    var energyLevelComments by remember { mutableStateOf("") }
    var selectedWaistFit by remember { mutableStateOf(WaistFit.SAME) }
    var selectedPainOrInjury by remember { mutableStateOf(false) }
    var selectedIllness by remember { mutableStateOf(false) }
    var healthNotes by remember { mutableStateOf("") }
    var weeklyNotes by remember { mutableStateOf("") }

    val userData = remember(
        selectedHungerLevel,
        hungerLevelComments,
        selectedEnergyLevel,
        energyLevelComments,
        selectedWaistFit,
        selectedPainOrInjury,
        selectedIllness,
        healthNotes,
        weeklyNotes
    ) {
        UserData(
            selectedHungerLevel,
            hungerLevelComments,
            selectedEnergyLevel,
            energyLevelComments,
            selectedWaistFit,
            selectedPainOrInjury,
            selectedIllness,
            healthNotes,
            weeklyNotes,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Hunger Level")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val levels = HungerLevel.entries
            levels.forEachIndexed { index, level ->
                val isSelected = selectedHungerLevel == level
                OutlinedButton(
                    onClick = {
                        selectedHungerLevel = level
                        userData.hungerLevel = level
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp)
                        levels.size - 1 -> RoundedCornerShape(topEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = level.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        OutlinedTextField(
            value = hungerLevelComments,
            onValueChange = { hungerLevelComments = it },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-1).dp),
            placeholder = { Text("Comments (optional)") },
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Energy Level")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val levels = EnergyLevel.entries
            levels.forEachIndexed { index, level ->
                val isSelected = selectedEnergyLevel == level
                OutlinedButton(
                    onClick = {
                        selectedEnergyLevel = level
                        userData.energyLevel = level
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp)
                        levels.size - 1 -> RoundedCornerShape(topEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = level.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        OutlinedTextField(
            value = energyLevelComments,
            onValueChange = { energyLevelComments = it },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-1).dp),
            placeholder = { Text("Comments (optional)") },
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Clothes/Waist Fit")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val levels = WaistFit.entries
            levels.forEachIndexed { index, level ->
                val isSelected = selectedWaistFit == level
                OutlinedButton(
                    onClick = {
                        selectedWaistFit = level
                        userData.waistFit = level
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        levels.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = level.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Health Details")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val isPainSelected = selectedPainOrInjury
            OutlinedButton(
                onClick = {
                    selectedPainOrInjury = !selectedPainOrInjury
                    userData.painOrInjury = selectedPainOrInjury
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 0.dp)
                    .height(48.dp)
                    .zIndex(if (isPainSelected) 1f else 0f),
                shape = RoundedCornerShape(topStart = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isPainSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (isPainSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "Pain or Injury", style = MaterialTheme.typography.labelSmall
                )
            }

            val isIllnessSelected = selectedIllness
            OutlinedButton(
                onClick = {
                    selectedIllness = !selectedIllness
                    userData.illness = selectedIllness
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .zIndex(if (isIllnessSelected) 1f else 0f),
                shape = RoundedCornerShape(topEnd = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isIllnessSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (isIllnessSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Illness", style = MaterialTheme.typography.labelSmall
                )
            }
        }

        OutlinedTextField(
            value = healthNotes,
            onValueChange = {
                healthNotes = it
                userData.healthNotes = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-1).dp),
            placeholder = { Text("Health notes (e.g. knee pain, cold symptoms)") },
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = weeklyNotes,
            onValueChange = { weeklyNotes = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Additional notes (optional)") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    scope.launch {
                        report = repository.getWeeklyReport(userData)
                    }
                }
            ) {
                Text("Generate Weekly Report")
            }

            IconButton(
                onClick = {
                    val clipData = ClipData.newPlainText("Health Report", report?.toString())
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(clipData))
                    }
                },
                enabled = report != null,
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Report"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-1).dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Text(
                text = report?.toString() ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HealthDigestApp("main")
}
