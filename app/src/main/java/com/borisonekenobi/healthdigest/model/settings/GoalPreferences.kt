package com.borisonekenobi.healthdigest.model.settings

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import androidx.health.connect.client.units.kilocalories

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
    LOSE_QUICKLY((-750.0).kilocalories),
    LOSE((-500.0).kilocalories),
    LOSE_SLOWLY((-250.0).kilocalories),
    MAINTAIN(0.0.kilocalories),
    GAIN_SLOWLY(250.0.kilocalories),
    GAIN(500.0.kilocalories),
}

data class Range<T : Comparable<T>>(
    var lowerBound: T?,
    var upperBound: T?,
)

fun <T : Comparable<T>> T.inRange(range: Range<T>): Boolean {
    return (range.lowerBound == null || this >= range.lowerBound!!) &&
           (range.upperBound == null || this <= range.upperBound!!)
}
