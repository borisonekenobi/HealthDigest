package com.borisonekenobi.healthdigest.model

data class GoalSummary(
    val calorieGoalMet: Boolean,
    val proteinGoalMet: Boolean,
    val stepGoalMet: Boolean,
    val weightGoalMet: Boolean
) {
    override fun toString(): String {
        return """
            Calorie Goal Met: ${if (calorieGoalMet) "Yes" else "No"}
            Protein Goal Met: ${if (proteinGoalMet) "Yes" else "No"}
            Step Goal Met: ${if (stepGoalMet) "Yes" else "No"}
            Weight Goal Met: ${if (weightGoalMet) "Yes" else "No"}"""
    }
}
