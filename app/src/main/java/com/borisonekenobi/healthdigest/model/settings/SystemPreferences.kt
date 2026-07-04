package com.borisonekenobi.healthdigest.model.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector

data class SystemPreferences(
    var theme: Theme,
    var units: Units,
)

enum class Theme(val value: ImageVector) {
    LIGHT(Icons.Default.LightMode),
    SYSTEM(Icons.Default.Contrast),
    DARK(Icons.Default.DarkMode),
}

enum class Units {
    METRIC,
    IMPERIAL,
}
