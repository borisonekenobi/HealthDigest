package com.borisonekenobi.healthdigest.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.health.connect.client.units.fluidOuncesUs
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.milliliters
import androidx.health.connect.client.units.ounces
import com.borisonekenobi.healthdigest.data.DataStoreSource
import com.borisonekenobi.healthdigest.data.PreferenceKeys
import com.borisonekenobi.healthdigest.model.settings.Range
import com.borisonekenobi.healthdigest.model.energyUnits
import com.borisonekenobi.healthdigest.model.settings.GoalPreferences
import com.borisonekenobi.healthdigest.model.settings.SystemPreferences
import com.borisonekenobi.healthdigest.model.settings.Theme
import com.borisonekenobi.healthdigest.model.settings.Units
import com.borisonekenobi.healthdigest.model.settings.WeightGoal
import com.borisonekenobi.healthdigest.model.smallMassUnits
import com.borisonekenobi.healthdigest.model.volumeUnits
import com.borisonekenobi.healthdigest.ui.components.GoalNumberField
import com.borisonekenobi.healthdigest.ui.components.ToggleButton
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun GoalPreferencesScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStoreSource = remember { DataStoreSource(context) }
    val goalPreferencesNullable by dataStoreSource.goalPreferencesFlow.collectAsState(initial = null)
    val goalPreferences = goalPreferencesNullable ?: GoalPreferences(
        WeightGoal.MAINTAIN,
        true,
        null,
        null,
        null,
        null,
        true,
        null
    )
    val systemPreferencesNullable by dataStoreSource.systemPreferencesFlow.collectAsState(initial = null)
    val systemPreferences = systemPreferencesNullable ?: SystemPreferences(
        Theme.SYSTEM, Units.METRIC
    )

    var weightGoal by remember { mutableStateOf(WeightGoal.MAINTAIN) }
    var autoNutritionGoals by remember { mutableStateOf(false) }
    var calorieRange by remember { mutableStateOf(Range<String>(null, null)) }
    var proteinRange by remember { mutableStateOf(Range<String>(null, null)) }
    var carbsRange by remember { mutableStateOf(Range<String>(null, null)) }
    var fatRange by remember { mutableStateOf(Range<String>(null, null)) }

    var autoHydrationGoals by remember { mutableStateOf(false) }
    var waterGoal by remember { mutableStateOf("") }

    var lastInitializedUnits by remember { mutableStateOf<Units?>(null) }

    val autoCalorieRange = remember { Range("2000", "2500") }
    val autoProteinRange = remember(systemPreferences.units) {
        if (systemPreferences.units == Units.METRIC) Range("150", "200") else Range("5.3", "7.1")
    }
    val autoCarbsRange = remember(systemPreferences.units) {
        if (systemPreferences.units == Units.METRIC) Range("250", "300") else Range("8.8", "10.6")
    }
    val autoFatRange = remember(systemPreferences.units) {
        if (systemPreferences.units == Units.METRIC) Range("60", "80") else Range("2.1", "2.8")
    }
    val autoWaterGoal = remember(systemPreferences.units) {
        if (systemPreferences.units == Units.METRIC) "2500" else "84.5"
    }

    fun isValidNumberInput(value: String): Boolean {
        return if (value.isEmpty()) true
        else value.substringAfter('.', "").length <= 2
    }

    LaunchedEffect(goalPreferencesNullable, goalPreferences) {
        val prefs = goalPreferencesNullable
        if (prefs != null && lastInitializedUnits != systemPreferences.units) {
            val units = systemPreferences.units

            fun formatDouble(d: Double?): String =
                d?.let {
                    if (it == 0.0) ""
                    else if (it % 1.0 == 0.0) it.toInt().toString()
                    else "%.2f".format(Locale.US, it).trimEnd('0').trimEnd('.')
                } ?: ""

            weightGoal = prefs.weightGoal
            calorieRange = Range(
                prefs.calorieGoal?.lowerBound?.inKilocalories?.let { formatDouble(it) },
                prefs.calorieGoal?.upperBound?.inKilocalories?.let { formatDouble(it) }
            )
            proteinRange = Range(
                prefs.proteinGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.proteinGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            carbsRange = Range(
                prefs.carbsGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.carbsGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            fatRange = Range(
                prefs.fatGoal?.lowerBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                },
                prefs.fatGoal?.upperBound?.let {
                    formatDouble(if (units == Units.METRIC) it.inGrams else it.inOunces)
                }
            )
            waterGoal = prefs.waterGoal?.let {
                formatDouble(if (units == Units.METRIC) it.inMilliliters else it.inFluidOuncesUs)
            } ?: ""

            if (lastInitializedUnits == null) {
                autoNutritionGoals = prefs.autoNutritionGoals
                autoHydrationGoals = prefs.autoHydrationGoals
            }

            lastInitializedUnits = units
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Weight Goal")

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val weightGoals = WeightGoal.entries
            weightGoals.forEachIndexed { index, weightGoal ->
                val isSelected = goalPreferences.weightGoal == weightGoal
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            dataStoreSource.saveUserPreference(PreferenceKeys.WEIGHT_GOAL, weightGoal.name)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        weightGoals.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = weightGoal.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(Modifier.height(8.dp))

        Text("Nutrition")

        Spacer(Modifier.height(8.dp))

        ToggleButton("Auto Nutrition Goals", autoNutritionGoals) {
            autoNutritionGoals = it
            scope.launch {
                dataStoreSource.saveUserPreference(
                    PreferenceKeys.AUTO_NUTRITION_GOALS,
                    it.toString()
                )
            }
        }

        GoalNumberField(
            if (autoNutritionGoals) autoCalorieRange else calorieRange,
            !autoNutritionGoals,
            "Calorie",
            energyUnits(),
            onLowerBoundChange = { newValue ->
                if (isValidNumberInput(newValue)) {
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
                if (isValidNumberInput(newValue)) {
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

        Spacer(Modifier.height(8.dp))

        GoalNumberField(
            if (autoNutritionGoals) autoProteinRange else proteinRange,
            !autoNutritionGoals,
            "Protein",
            smallMassUnits(systemPreferences.units),
            onLowerBoundChange = { newValue ->
                if (isValidNumberInput(newValue)) {
                    proteinRange = proteinRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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
                if (isValidNumberInput(newValue)) {
                    proteinRange = proteinRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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

        Spacer(Modifier.height(8.dp))

        GoalNumberField(
            if (autoNutritionGoals) autoCarbsRange else carbsRange,
            !autoNutritionGoals,
            "Carbs",
            smallMassUnits(systemPreferences.units),
            onLowerBoundChange = { newValue ->
                if (isValidNumberInput(newValue)) {
                    carbsRange = carbsRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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
                if (isValidNumberInput(newValue)) {
                    carbsRange = carbsRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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

        Spacer(Modifier.height(8.dp))

        GoalNumberField(
            if (autoNutritionGoals) autoFatRange else fatRange,
            !autoNutritionGoals,
            "Fat",
            smallMassUnits(systemPreferences.units),
            onLowerBoundChange = { newValue ->
                if (isValidNumberInput(newValue)) {
                    fatRange = fatRange.copy(lowerBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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
                if (isValidNumberInput(newValue)) {
                    fatRange = fatRange.copy(upperBound = newValue)
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val gramsValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.grams.inGrams
                                Units.IMPERIAL -> doubleValue.ounces.inGrams
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

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(Modifier.height(8.dp))

        Text("Hydration")

        Spacer(Modifier.height(8.dp))

        ToggleButton("Auto Hydration Goals", autoHydrationGoals) {
            autoHydrationGoals = it
            scope.launch {
                dataStoreSource.saveUserPreference(
                    PreferenceKeys.AUTO_HYDRATION_GOALS,
                    it.toString()
                )
            }
        }

        OutlinedTextField(
            value = if (autoHydrationGoals) autoWaterGoal else waterGoal,
            onValueChange = { newValue ->
                if (isValidNumberInput(newValue)) {
                    waterGoal = newValue
                    scope.launch {
                        val doubleValue = newValue.toDoubleOrNull()
                        val mlValue = if (doubleValue == null) "" else {
                            when (systemPreferences.units) {
                                Units.METRIC -> doubleValue.milliliters.inMilliliters
                                Units.IMPERIAL -> doubleValue.fluidOuncesUs.inMilliliters
                            }.toString()
                        }
                        dataStoreSource.saveUserPreference(PreferenceKeys.WATER_GOAL, mlValue)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !autoHydrationGoals,
            label = { Text("Water goal (optional)") },
            suffix = { Text(volumeUnits(systemPreferences.units)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
private fun GoalPreferencesPreview() {
    HealthDigestApp("goal-preferences")
}
