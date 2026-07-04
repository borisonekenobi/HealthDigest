package com.borisonekenobi.healthdigest.model

import androidx.health.connect.client.units.Energy
import com.borisonekenobi.healthdigest.model.settings.Units

data class Activity(
    val totalSteps: Long?,
    val averageStepsPerDay: Long?,
    val activeCalories: Energy?,
    val exerciseMinutes: Long?,
    val workoutCount: Int?,
    val units: Units,
) {
    override fun toString(): String {
        return """
            Total Steps: ${totalSteps ?: "N/A"}
            Average Steps Per Day: ${averageStepsPerDay ?: "N/A"}
            Active Calories: ${convert(activeCalories)}
            Exercise Minutes: ${exerciseMinutes ?: "N/A"}
            Workout Count: ${workoutCount ?: "N/A"}"""
    }
}
