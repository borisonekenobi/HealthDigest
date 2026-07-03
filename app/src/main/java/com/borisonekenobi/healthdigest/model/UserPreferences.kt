package com.borisonekenobi.healthdigest.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector

data class UserPreferences(
    var units: Units,
    var startMessage: String,
    var endMessage: String,
    var theme: Theme,
)

enum class Units {
    METRIC,
    IMPERIAL,
}

enum class Theme(val value: ImageVector) {
    LIGHT(Icons.Default.LightMode),
    SYSTEM(Icons.Default.Contrast),
    DARK(Icons.Default.DarkMode),
}
