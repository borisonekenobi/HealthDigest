package com.borisonekenobi.healthdigest.model

data class Activity(
    val totalSteps: Long?,
    val averageStepsPerDay: Long?,
    val activeCalories: Double?,
    val exerciseMinutes: Long?,
    val workoutCount: Int?
) {
    override fun toString(): String {
        return """
            Total Steps: ${totalSteps ?: "N/A"}
            Average Steps Per Day: ${averageStepsPerDay ?: "N/A"}
            Active Calories: ${activeCalories ?: "N/A"}
            Exercise Minutes: ${exerciseMinutes ?: "N/A"}
            Workout Count: ${workoutCount ?: "N/A"}"""
    }
}
