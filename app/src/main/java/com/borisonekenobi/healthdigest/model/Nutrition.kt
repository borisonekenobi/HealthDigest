package com.borisonekenobi.healthdigest.model

data class Nutrition(
    val averageCalories: Double?,
    val averageProteinGrams: Double?,
    val averageCarbsGrams: Double?,
    val averageFatGrams: Double?,
    val averageWaterLitres: Double?,

    val daysLogged: Int?,
    val daysWithinCalorieGoal: Int?,
    val daysProteinGoalMet: Int?
) {
    override fun toString(): String {
        return """
            Average Calories: ${averageCalories ?: "N/A"} kcal
            Average Protein: ${averageProteinGrams ?: "N/A"} g
            Average Carbs: ${averageCarbsGrams ?: "N/A"} g
            Average Fat: ${averageFatGrams ?: "N/A"} g
            Average Water: ${averageWaterLitres ?: "N/A"} L
            Days Logged: ${daysLogged ?: "N/A"} / 7
            Days Within Calorie Goal: ${daysWithinCalorieGoal ?: "N/A"} / 7
            Days Protein Goal Met: ${daysProteinGoalMet ?: "N/A"} / 7"""
    }
}
