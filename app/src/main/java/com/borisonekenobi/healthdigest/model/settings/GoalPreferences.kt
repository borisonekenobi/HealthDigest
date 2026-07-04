package com.borisonekenobi.healthdigest.model.settings

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume

data class GoalPreferences(
    var weightGoal: WeightGoal,
    var autoNutritionGoals: Boolean,
    var calorieGoal: Range<Energy>?,
    var proteinGoal: Range<Mass>?,
    var carbsGoal: Range<Mass>?,
    var fatGoal: Range<Mass>?,
    var autoHydrationGoals: Boolean,
    var waterGoal: Volume?,
)

enum class WeightGoal(val value: Energy) {
    LOSE_QUICKLY(Energy.kilocalories(-750.0)),
    LOSE(Energy.kilocalories(-500.0)),
    LOSE_SLOWLY(Energy.kilocalories(-250.0)),
    MAINTAIN(Energy.kilocalories(0.0)),
    GAIN_SLOWLY(Energy.kilocalories(250.0)),
    GAIN(Energy.kilocalories(500.0)),
}

data class Range<T : Comparable<T>>(
    var lowerBound: T?,
    var upperBound: T?,
)

fun <T : Comparable<T>> T.inRange(range: Range<T>): Boolean {
    return (range.lowerBound == null || this >= range.lowerBound!!) &&
            (range.upperBound == null || this <= range.upperBound!!)
}
