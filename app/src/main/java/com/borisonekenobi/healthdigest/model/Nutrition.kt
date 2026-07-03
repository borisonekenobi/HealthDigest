package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume

data class Nutrition(
    val averageCalories: Energy?,
    val averageProtein: Mass?,
    val averageCarbs: Mass?,
    val averageFat: Mass?,
    val averageWater: Volume?,

    val daysLogged: Int?,
    val daysWithinCalorieGoal: Int?,
    val daysProteinGoalMet: Int?,

    val units: Units,
) {
    override fun toString(): String {
        return """
            Average Calories: ${convert(averageCalories, units)}
            Average Protein: ${convertSmall(averageProtein, units)}
            Average Carbs: ${convertSmall(averageCarbs, units)}
            Average Fat: ${convertSmall(averageFat, units)}
            Average Water: ${convert(averageWater, units)}
            Days Logged: ${daysLogged ?: "N/A"} / 7
            Days Within Calorie Goal: ${daysWithinCalorieGoal ?: "N/A"} / 7
            Days Protein Goal Met: ${daysProteinGoalMet ?: "N/A"} / 7"""
    }
}
