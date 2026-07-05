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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.borisonekenobi.healthdigest.model.settings.SystemPreferences
import com.borisonekenobi.healthdigest.model.settings.Theme
import com.borisonekenobi.healthdigest.model.settings.Units
import kotlinx.coroutines.launch

@Composable
fun SystemPreferencesScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStoreSource = remember { DataStoreSource(context) }
    val systemPreferencesNullable by dataStoreSource.systemPreferencesFlow.collectAsState(initial = null)
    val systemPreferences = systemPreferencesNullable ?: SystemPreferences(
        Theme.SYSTEM,
        Units.METRIC,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = stringResource(R.string.theme))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val themes = Theme.entries
            themes.forEachIndexed { index, theme ->
                val isSelected = systemPreferences.theme == theme
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

        Text(text = stringResource(R.string.units))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val units = Units.entries
            units.forEachIndexed { index, unit ->
                val isSelected = systemPreferences.units == unit
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
    }
}

@Preview(showBackground = true)
@Composable
private fun SystemPreferencesPreview() {
    HealthDigestApp("system-preferences")
}
