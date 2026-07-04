package com.borisonekenobi.healthdigest.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume

data class UserPreferences(
    var theme: Theme,
    var units: Units,
    val goals: Goals,
    val messages: Messages,
)

enum class Theme(val value: ImageVector) {
    LIGHT(Icons.Default.LightMode),
    SYSTEM(Icons.Default.Contrast),
    DARK(Icons.Default.DarkMode),
}

enum class Units {
    METRIC, IMPERIAL,
}

data class Goals(
    var calorieGoal: Range<Energy>?,
    var proteinGoal: Range<Mass>?,
    var carbsGoal: Range<Mass>?,
    var fatGoal: Range<Mass>?,
    var waterGoal: Volume?,
)

data class Range<T : Comparable<T>>(
    var lowerBound: T?,
    var upperBound: T?,
)

fun <T : Comparable<T>> T.inRange(range: Range<T>): Boolean {
    return (range.lowerBound == null || this >= range.lowerBound!!) &&
           (range.upperBound == null || this <= range.upperBound!!)
}

data class Messages(
    var startMessage: String,
    var endMessage: String,
)
