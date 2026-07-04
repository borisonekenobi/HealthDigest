package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import com.borisonekenobi.healthdigest.model.settings.Units

data class Nutrition(
    val averageCalories: Energy?,
    val averageProtein: Mass?,
    val averageCarbs: Mass?,
    val averageFat: Mass?,
    val averageWater: Volume?,

    val daysLogged: Int,
    val daysWithinCalorieGoal: Int?,
    val daysProteinGoalMet: Int?,
    val daysCarbsGoalMet: Int?,
    val daysFatGoalMet: Int?,
    val daysWaterGoalMet: Int?,

    val units: Units,
) {
    override fun toString(): String {
        return """
            Average Calories: ${convert(averageCalories)}
            Average Protein: ${convertSmall(averageProtein, units)}
            Average Carbs: ${convertSmall(averageCarbs, units)}
            Average Fat: ${convertSmall(averageFat, units)}
            Average Water: ${convert(averageWater, units)}
            Days Logged: $daysLogged
            Days Within Calorie Goal: ${daysWithinCalorieGoal ?: "N/A"} / $daysLogged
            Days Protein Goal Met: ${daysProteinGoalMet ?: "N/A"} / $daysLogged
            Days Carbs Goal Met: ${daysCarbsGoalMet ?: "N/A"} / $daysLogged
            Days Fat Goal Met: ${daysFatGoalMet ?: "N/A"} / $daysLogged
            Days Water Goal Met: ${daysWaterGoalMet ?: "N/A"} / $daysLogged"""
    }
}
