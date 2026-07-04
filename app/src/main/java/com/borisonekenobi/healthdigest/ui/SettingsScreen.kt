package com.borisonekenobi.healthdigest.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import com.borisonekenobi.healthdigest.data.DataStoreSource
import com.borisonekenobi.healthdigest.data.HealthConnectPermissions
import com.borisonekenobi.healthdigest.data.HealthPermissions
import com.borisonekenobi.healthdigest.data.PreferenceKeys
import com.borisonekenobi.healthdigest.model.Range
import com.borisonekenobi.healthdigest.model.Goals
import com.borisonekenobi.healthdigest.model.Messages
import com.borisonekenobi.healthdigest.model.Theme
import com.borisonekenobi.healthdigest.model.Units
import com.borisonekenobi.healthdigest.model.UserPreferences
import com.borisonekenobi.healthdigest.model.energyUnits
import com.borisonekenobi.healthdigest.model.smallMassUnits
import com.borisonekenobi.healthdigest.model.volumeUnits
import com.borisonekenobi.healthdigest.ui.components.GoalNumberField
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStoreSource = remember { DataStoreSource(context) }
    val userPreferencesNullable by dataStoreSource.userPreferencesFlow.collectAsState(initial = null)
    val userPreferences = userPreferencesNullable ?: UserPreferences(
        Theme.SYSTEM,
        Units.METRIC,
        Goals(null, null, null, null, null),
        Messages("", ""),
    )

    var calorieRange by remember { mutableStateOf(Range<String>(null, null)) }
    var proteinRange by remember { mutableStateOf(Range<String>(null, null)) }
    var carbsRange by remember { mutableStateOf(Range<String>(null, null)) }
    var fatRange by remember { mutableStateOf(Range<String>(null, null)) }
    var waterGoal by remember { mutableStateOf("") }

    var startMessage by remember { mutableStateOf("") }
    var endMessage by remember { mutableStateOf("") }

    var lastInitializedUnits by remember { mutableStateOf<Units?>(null) }

    LaunchedEffect(userPreferencesNullable, userPreferences.units) {
        val prefs = userPreferencesNullable
        if (prefs != null && lastInitializedUnits != userPreferences.units) {
            val units = userPreferences.units

            fun formatDouble(d: Double?): String =
                d?.let { if (it == 0.0) "" else if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: ""

            calorieRange = Range(
                prefs.goals.calorieGoal?.lowerBound?.inKilocalories?.let { formatDouble(it) },
                prefs.goals.calorieGoal?.upperBound?.inKilocalories?.let { formatDouble(it) }
            )
            proteinRange = Range(
                prefs.goals.proteinGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.goals.proteinGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            carbsRange = Range(
                prefs.goals.carbsGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.goals.carbsGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            fatRange = Range(
                prefs.goals.fatGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.goals.fatGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            waterGoal = prefs.goals.waterGoal?.let {
                formatDouble(if (units == Units.METRIC) it.inMilliliters else it.inFluidOuncesUs)
            } ?: ""

            if (lastInitializedUnits == null) {
                startMessage = prefs.messages.startMessage
                endMessage = prefs.messages.endMessage
            }

            lastInitializedUnits = units
        }
    }

    val permission: HealthPermissions = remember { HealthConnectPermissions(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { _ -> }

    var allPermissionsGranted by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        allPermissionsGranted = permission.hasAllPermissions()
    }

    suspend fun checkPermissions() {
        allPermissionsGranted = null
        allPermissionsGranted = permission.hasAllPermissions()
        Toast.makeText(
            context, when (allPermissionsGranted) {
                true -> "All permissions granted"
                else -> "Not all permissions granted"
            }, Toast.LENGTH_SHORT
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Health Connect")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = {
                permission.getPermissions(permissionLauncher)
                scope.launch { checkPermissions() }
            }) {
                Text("Connect Health Data")
            }

            if (allPermissionsGranted == null) {
                CircularProgressIndicator()
            } else {
                IconButton(
                    onClick = {
                        scope.launch { checkPermissions() }
                    }) {
                    Icon(
                        imageVector = when (allPermissionsGranted) {
                            true -> Icons.Default.CheckCircleOutline
                            else -> Icons.Default.ErrorOutline
                        }, contentDescription = "Permission Status"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Theme")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val themes = Theme.entries
            themes.forEachIndexed { index, theme ->
                val isSelected = userPreferences.theme == theme
                OutlinedIconButton(
                    onClick = {
                        scope.launch {
                            dataStoreSource.saveUserPreference(PreferenceKeys.THEME, theme.name)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        themes.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Icon(
                        imageVector = theme.value, contentDescription = theme.name
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Units")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val units = Units.entries
            units.forEachIndexed { index, unit ->
                val isSelected = userPreferences.units == unit
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            dataStoreSource.saveUserPreference(PreferenceKeys.UNITS, unit.name)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        units.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = unit.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Goals")

        Spacer(modifier = Modifier.height(8.dp))

        GoalNumberField(
            calorieRange, "Calorie", energyUnits(),
            onLowerBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    calorieRange = calorieRange.copy(lowerBound = newValue)
                    scope.launch {
                        val valueToSave = newValue.toDoubleOrNull()?.toString() ?: ""
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.CALORIE_GOAL_LOWER,
                            valueToSave
                        )
                    }
                }
            },
            onUpperBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    calorieRange = calorieRange.copy(upperBound = newValue)
                    scope.launch {
                        val valueToSave = newValue.toDoubleOrNull()?.toString() ?: ""
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.CALORIE_GOAL_UPPER,
                            valueToSave
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        GoalNumberField(
            proteinRange, "Protein", smallMassUnits(userPreferences.units),
            onLowerBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    proteinRange = proteinRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.PROTEIN_GOAL_LOWER,
                            gramsValue
                        )
                    }
                }
            },
            onUpperBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    proteinRange = proteinRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.PROTEIN_GOAL_UPPER,
                            gramsValue
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        GoalNumberField(
            carbsRange, "Carbs", smallMassUnits(userPreferences.units),
            onLowerBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    carbsRange = carbsRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.CARBS_GOAL_LOWER,
                            gramsValue
                        )
                    }
                }
            },
            onUpperBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    carbsRange = carbsRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.CARBS_GOAL_UPPER,
                            gramsValue
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        GoalNumberField(
            fatRange, "Fat", smallMassUnits(userPreferences.units),
            onLowerBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    fatRange = fatRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.FAT_GOAL_LOWER,
                            gramsValue
                        )
                    }
                }
            },
            onUpperBoundChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    fatRange = fatRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Mass.grams(doubleValue).inGrams
                                Units.IMPERIAL -> Mass.ounces(doubleValue).inGrams
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.FAT_GOAL_UPPER,
                            gramsValue
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = waterGoal,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(".")) {
                    waterGoal = newValue
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val mlValue = if (doubleValue == null) "" else {
                            when (userPreferences.units) {
                                Units.METRIC -> Volume.liters(doubleValue).inMilliliters
                                Units.IMPERIAL -> Volume.fluidOuncesUs(doubleValue).inMilliliters
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(PreferenceKeys.WATER_GOAL, mlValue)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Water goal (optional)") },
            suffix = { Text(volumeUnits(userPreferences.units)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    HealthDigestApp("settings")
}
