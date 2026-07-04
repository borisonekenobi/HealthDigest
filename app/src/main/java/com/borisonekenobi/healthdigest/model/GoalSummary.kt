package com.borisonekenobi.healthdigest.model

import com.borisonekenobi.healthdigest.model.settings.GoalPreferences
import com.borisonekenobi.healthdigest.model.settings.Units

data class GoalSummary(val goals: GoalPreferences, val units: Units) {
    override fun toString(): String {
        return """
            Calorie goal range: ${convert(goals.calorieGoal?.lowerBound)} - ${convert(goals.calorieGoal?.upperBound)}
            Protein goal range: ${convertSmall(goals.proteinGoal?.lowerBound, units)} - ${convertSmall(goals.proteinGoal?.upperBound, units)}
            Carbs goal range: ${convertSmall(goals.carbsGoal?.lowerBound, units)} - ${convertSmall(goals.carbsGoal?.upperBound, units)}
            Fat goal range: ${convertSmall(goals.fatGoal?.lowerBound, units)} - ${convertSmall(goals.fatGoal?.upperBound, units)}
            Water goal: ${convert(goals.waterGoal, units)}"""
    }
}
