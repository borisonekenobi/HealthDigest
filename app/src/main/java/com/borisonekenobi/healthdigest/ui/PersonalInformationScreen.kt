package com.borisonekenobi.healthdigest.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.borisonekenobi.healthdigest.R
import com.borisonekenobi.healthdigest.data.DataStoreSource
import com.borisonekenobi.healthdigest.data.PreferenceKeys
import com.borisonekenobi.healthdigest.model.settings.PersonalInformation
import com.borisonekenobi.healthdigest.model.settings.Sex
import com.borisonekenobi.healthdigest.ui.components.ClearButton
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PersonalInformationScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStoreSource = remember { DataStoreSource(context) }
    val personalInformationNullable by dataStoreSource.personalInformationFlow.collectAsState(
        initial = null
    )
    val personalInformation = personalInformationNullable ?: PersonalInformation(null, null)

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = personalInformation.birthDate?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()?.toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    scope.launch {
                        dataStoreSource.saveUserPreference(
                            PreferenceKeys.BIRTH_DATE,
                            date.toString()
                        )
                    }
                }
                showDatePicker = false
            }) {
                Text(stringResource(R.string.ok))
            }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) {
                Text(stringResource(R.string.cancel))
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Top
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.sex))

            ClearButton(personalInformation.sex != null) {
                scope.launch { dataStoreSource.saveUserPreference(PreferenceKeys.SEX, "") }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val sexes = Sex.entries
            sexes.forEachIndexed { index, sex ->
                val isSelected = personalInformation.sex == sex
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            dataStoreSource.saveUserPreference(PreferenceKeys.SEX, sex.name)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 0.dp)
                        .height(48.dp)
                        .zIndex(if (isSelected) 1f else 0f),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        sexes.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        else -> RectangleShape
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = sex.name.lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_", " "),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.birth_date))

            ClearButton(personalInformation.birthDate != null) {
                scope.launch { dataStoreSource.saveUserPreference(PreferenceKeys.BIRTH_DATE, "") }
            }
        }

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL) }
            Text(
                text = personalInformation.birthDate?.format(formatter)
                    ?: stringResource(R.string.set_birth_date),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonalInformationPreview() {
    HealthDigestApp("personal-information")
}
